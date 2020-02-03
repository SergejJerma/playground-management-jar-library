package com.serjer.playground.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.serjer.playground.model.KidInfo;

import java.util.Calendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This service is an async service which assumes,
 * every kid in all the play site gets a time span of 2 mins to play.
 */
@Service
class GameService {

    private final HistoryService historyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);
    private final TotalCountReportService totalCountReportService;
    private ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1);

    @Autowired
    GameService(HistoryService historyService, TotalCountReportService totalCountReportService) {
        this.historyService = historyService;
        this.totalCountReportService = totalCountReportService;
        SCHEDULED_THREAD_POOL_EXECUTOR.scheduleAtFixedRate(this::updateQueueEveryTwoMinutes, 2, 2, TimeUnit.MINUTES);
    }

    // Will be accessible only within the library
    void updateQueueEveryTwoMinutes() {
        PlaySiteService.getPlaySitesMap()
                .forEach((playSiteItem, playSites) -> {

                    playSites.getPlaySitesAvailable().forEach(playSiteInfo -> {

                        if (!CollectionUtils.isEmpty(playSiteInfo.getKidInfoSet())) {

                            KidInfo kidInfo = playSiteInfo.getKidInfoSet().poll();

                            Calendar instance = Calendar.getInstance();

                            Calendar startDate = Calendar.getInstance();

                            startDate.add(Calendar.MINUTE, -2);

                            historyService.addToHistory(kidInfo, playSites, startDate.getTime(), instance.getTime());

                            totalCountReportService.updateTotalCountForTheDay(instance.getTime(), kidInfo.getTicketNumber(), playSiteItem);

                            LOGGER.info("Game ended for Playsite {} for kid {}", playSiteItem, kidInfo.getTicketNumber());
                        }

                    });
                });
    }
}
