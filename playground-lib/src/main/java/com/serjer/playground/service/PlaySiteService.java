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

	        if (playSitesMap.containsKey(playSiteItem)) {
	            return playSitesMap.get(playSiteItem);
	        } else {
	            throw new NotFoundException("PlaySite Info not found");
	        }
	    }

	    /**
	     * Throws IllegalArgumentException if playsite builder sis not found
	     */
	    public PlaySiteService registerPlaySite(final PlaySites.PlaySitesBuilder playSitesBuilder) {
	        Assert.notNull(playSitesBuilder, "Builder cannot be null");
	    //    for (int i = 1; i <= playSitesBuilder.getPlaySitesAvailable().size() MANO IDETAS
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

	            Optional<PlaySiteInfo> playSiteItemWithSpace = playSites.getPlaySitesAvailable()
	                    .stream()
	                    .filter(playSiteInfo -> playSiteInfo.getKidInfoSet().size() < playSites.getCapacityOfEachPlaySite())
	                    .findAny();

	            if (playSiteItemWithSpace.isPresent() && !kidInfo.isVip()) {
	                PlaySiteInfo playSiteInfo = playSiteItemWithSpace.get();
	                playSiteInfo.getKidInfoSet().add(kidInfo);

	            } else if (playSiteItemWithSpace.isPresent() && kidInfo.isVip()) {

	                reorderQueueForVip(kidInfo, playSiteItemWithSpace);

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

	        Integer vipIndex = null;

	        /*
	         * We should check whether vip is existing anywhere in the queue.
	         * So going back from queue, will help us to add 3 indexes after the last vip present.
	         */
	        for (int index = queueList.size() - 1; index > -1; index--) {
	            KidInfo kidInfo1 = queueList.get(index);

	            if (kidInfo1.isVip()) {
	                vipIndex = index;
	                break;
	            }
	        }

	        //No vip is present
	        if (vipIndex == null) {

	            kidInfoSet.add(kidInfo);
	            kidInfoSet.addAll(queueList);

	        } else if (vipIndex + 3 >= queueList.size() - 1) { //If vip is there by the end of queue
	            kidInfoSet.addAll(queueList);
	            kidInfoSet.add(kidInfo);

	        } else {

	            AtomicInteger index = new AtomicInteger(0);
	            AtomicInteger vipIndexWrapped = new AtomicInteger(vipIndex);

	            queueList.forEach(kidInfo1 -> {

	                if (index.get() < vipIndexWrapped.get() || index.get() > vipIndexWrapped.get()) {

	                    kidInfoSet.add(kidInfo1);

	                } else if (index.get() == vipIndexWrapped.get()) {

	                    kidInfoSet.add(kidInfo);
	                }

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
