package com.serjer.playground.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.serjer.playground.common.PlaySiteItem;
import com.serjer.playground.exception.NotFoundException;
import com.serjer.playground.model.KidInfo;
import com.serjer.playground.model.PlaySites;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public final class KidService {

    private final static Logger LOGGER = LoggerFactory.getLogger(KidService.class);
    private static Map<Integer, KidInfo> kidInfoList = new HashMap<>();

    private final PlaySiteService playSiteService;

    @Autowired
    public KidService(PlaySiteService playSiteService) {
        this.playSiteService = playSiteService;
    }

    /**
     * Throws NotFoundException if kid info is not found
     */
    public KidInfo getKidInfo(int ticketNumber) {

        if (kidInfoList.containsKey(ticketNumber)) {
            return kidInfoList.get(ticketNumber);
        } else {
            throw new NotFoundException("Kid Info not found");
        }
    }

    /**
     * Throws IllegalArgumentException if kid info builder is empty
     */
    public KidInfo registerKid(final KidInfo.KidInfoBuilder kidInfoBuilder) {
        Assert.notNull(kidInfoBuilder, "Builder cannot be null");
       
        KidInfo kidInfo = kidInfoBuilder.build();
        kidInfoList.put(kidInfo.getTicketNumber(), kidInfo);
        LOGGER.info("Kid {} registered", kidInfo.getTicketNumber());
        return kidInfo;
    }

    /**
     * Throws NotFoundException if kid info or playlist item is not found
     */
    public boolean addKidToPlaySite(int ticketNumber, final PlaySiteItem playSiteItem, Optional<Boolean> overrideWaitInQueueSetting) {

        KidInfo kidInfo = getKidInfo(ticketNumber);
        final PlaySites playSitesInfo = playSiteService.getPlaySitesInfo(playSiteItem);

        kidInfo = setCanWaitInQueueSetting(overrideWaitInQueueSetting, kidInfo);
        final boolean isKidAdded = playSiteService.addKidToPlaySite(playSitesInfo, kidInfo);
        LOGGER.info("Kid {} added to playsite {}", kidInfo.getTicketNumber(), playSiteItem);
        return isKidAdded;
    }

   
    private KidInfo setCanWaitInQueueSetting(Optional<Boolean> overrideWaitInQueueSetting, KidInfo kidInfo) {

        boolean canWaitInQueue = overrideWaitInQueueSetting.isPresent() ? overrideWaitInQueueSetting.get() : kidInfo.isCanWaitInQueue();

        kidInfo.overrideMyWaitInQueueSetting(canWaitInQueue);
        return kidInfo;
    }
    
    /**
     * Throws NotFoundException if kid info or playlist item is not found
     */
    public boolean removeKidFromPlaySite(int ticketNumber, final PlaySiteItem playSiteItem) {

        KidInfo kidInfo = getKidInfo(ticketNumber);
        final PlaySites playSitesInfo = playSiteService.getPlaySitesInfo(playSiteItem);

        final boolean isKidRemoved = playSiteService.removeKidFromPlaySite(playSitesInfo, kidInfo);

        LOGGER.info("Kid {} removed from playsite {}", kidInfo.getTicketNumber(), playSiteItem);

        return isKidRemoved;
    }
}