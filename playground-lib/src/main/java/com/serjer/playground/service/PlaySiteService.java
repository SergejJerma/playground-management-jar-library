package com.serjer.playground.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.serjer.playground.common.PlaySiteItem;
import com.serjer.playground.exception.CannotWaitInQueueException;
import com.serjer.playground.exception.NotFoundException;
import com.serjer.playground.exception.PlaySiteItemFullException;
import com.serjer.playground.model.KidInfo;
import com.serjer.playground.model.PlaySiteInfo;
import com.serjer.playground.model.PlaySites;
import com.serjer.playground.service.validation.PlaySiteValidateService;


public class PlaySiteService {
	

	private final static Logger LOGGER = LoggerFactory.getLogger(PlaySiteService.class);

	    private static Map<PlaySiteItem, PlaySites> playSitesMap = new HashMap<>();
	    private final PlaySiteValidateService playSiteValidateService;

	    @Autowired
	    public PlaySiteService(PlaySiteValidateService playSiteValidateService) {
	        this.playSiteValidateService = playSiteValidateService;
	    }

	    // Dont expose the static in memory playSitesMap, because its mutable object.
	    public Set<PlaySiteItem> getPlayListItemNames() {
	        return playSitesMap.keySet();
	    }

	    // Only default packages can access this - used for Game service
	    static Map<PlaySiteItem, PlaySites> getPlaySitesMap() {
	        return playSitesMap;
	    }

	    /**
	     * Throws NotFoundException if play list info is not found
	     */

	    public PlaySites getPlaySitesInfo(PlaySiteItem playSiteItem) {
	    	
	    	if (playSitesMap.containsKey(playSiteItem)) 
	    		return playSitesMap.get(playSiteItem);
	    	else 
	    		throw new NotFoundException("PlaySite Info not found");
	    }

	    /**
	     * Throws IllegalArgumentException if playsite builder sis not found
	     */
	    
	    public PlaySiteService registerPlaySite(final PlaySites.PlaySitesBuilder playSitesBuilder) {
	    	Assert.notNull(playSitesBuilder, "Builder cann't be null");
	    	
	    	PlaySites playSites = playSitesBuilder.build();
	    	playSitesMap.put(playSites.getPlaySiteName(), playSites);
	    	LOGGER.info("Play site {} registered", playSites.getPlaySiteName());
	    	return this;
	    }

	    /*
	     * Throws PlaySiteItemFullException if play site is already filled
	     * Throws IllegalArgumentException if playsite info is not found
	     * Will be accessible only within the library
	     */
	    boolean addKidToPlaySite(final PlaySites playSites, final KidInfo kidInfo) {

	         if (!playSiteValidateService.canAddKidToPlaySite(playSites, kidInfo)) {
	            throw new PlaySiteItemFullException("Play site is already filled");
	        }

	        if (!playSiteValidateService.canKidWaitForHisTurn(playSites, kidInfo)) {
	            throw new CannotWaitInQueueException("Cannot wait in queue");
	        }

	        synchronized (playSites.getPlaySiteName()) {

	            Optional<PlaySiteInfo> playSiteItemWithFreePlases = playSites.getPlaySitesAvailable()
	                    .stream()
	                    .filter(playSiteInfo -> playSiteInfo.getKidInfoSet().size() < playSites.getCapacityOfEachPlaySite())
	                    .findAny();

	            if (playSiteItemWithFreePlases.isPresent() && !kidInfo.isVip()) {
	                PlaySiteInfo playSiteInfo = playSiteItemWithFreePlases.get();
	                playSiteInfo.getKidInfoSet().add(kidInfo);

	            } else if (playSiteItemWithFreePlases.isPresent() && kidInfo.isVip()) {

	                reorderQueueForVip(kidInfo, playSiteItemWithFreePlases);

	            } else {

	                throw new PlaySiteItemFullException("Play site is already filled");
	            }
	        }
	        LOGGER.info("Kid added to the play site");

	        return true;
	    }

	    private void reorderQueueForVip(KidInfo kidInfo, Optional<PlaySiteInfo> playSiteItemWithSpace) {
	        LinkedBlockingQueue<KidInfo> kidInfoSet = playSiteItemWithSpace.get().getKidInfoSet();

	        List<KidInfo> queueList = new LinkedList<>();
	        queueList.addAll(kidInfoSet);

	        kidInfoSet.clear();

	        Integer lastVipIndex = null;

	    	        for (int index = queueList.size() - 1; index > -1; index--) {
	            KidInfo kidInfo1 = queueList.get(index);

	            if (kidInfo1.isVip()) {
	                lastVipIndex = index;
	                break;
	            }
	        }

	    	if (lastVipIndex == null) {

	            kidInfoSet.add(kidInfo);
	            kidInfoSet.addAll(queueList);

	        } else if (lastVipIndex == queueList.size() - 1 || lastVipIndex <= queueList.size() - 2) { 
	            kidInfoSet.addAll(queueList);
	            kidInfoSet.add(kidInfo);

	        } else {

	            AtomicInteger index = new AtomicInteger(0);
	            AtomicInteger vipIndexWrapped = new AtomicInteger(lastVipIndex + 4);

	            queueList.forEach(kidInfo1 -> {

	                if (index.get() != vipIndexWrapped.get()) 
	                    kidInfoSet.add(kidInfo1);
	                else  
	                    kidInfoSet.add(kidInfo);

	                index.getAndIncrement();
	            });
	        }
	    }

	    /*
	     * Can only be accessible from Kid Service with default modifier
	     * Will be accessible only within the library
	     */
	    boolean removeKidFromPlaySite(final PlaySites playSites, final KidInfo kidInfo) {

	        AtomicBoolean isRemoved = new AtomicBoolean(false);

	        playSites.getPlaySitesAvailable().forEach(playSiteInfo -> {

	            boolean kidRemoved = playSiteInfo.getKidInfoSet().remove(kidInfo);

	            if (kidRemoved) {
	                LOGGER.info("Kid Removed Successfully;");
	                isRemoved.set(true);
	            }
	        });

	        return isRemoved.get();
	    }

}
