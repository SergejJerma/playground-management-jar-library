package com.serjer.playground.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.serjer.playground.common.PlaySiteItem;
import com.serjer.playground.model.HistoryData;
import com.serjer.playground.model.KidHistoryData;
import com.serjer.playground.model.KidInfo;
import com.serjer.playground.model.PlaySites;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class HistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryService.class);
    private static List<KidHistoryData> kidHistoryDatas = new LinkedList<>();

    // Will be accessible only within the library
    void addToHistory(final KidInfo kidInfo, final PlaySites playSites, final Date startTime, final Date endTime) {

        int indexOfKidInfoPresent = kidHistoryDatas.indexOf(kidInfo);

        long timeSpentInPlaySiteInMillis = endTime.getTime() - startTime.getTime();

        HistoryData historyData = new HistoryData(startTime, endTime, timeSpentInPlaySiteInMillis);

        if (isKidInfoPresent(indexOfKidInfoPresent)) {

            addToExistingKidHistory(playSites, indexOfKidInfoPresent, historyData);
        } else {

            createNewKidHistory(kidInfo, playSites, historyData);
        }

        LOGGER.info("History added for Kid {}", kidInfo.getTicketNumber());
    }

    private void createNewKidHistory(KidInfo kidInfo, PlaySites playSites, HistoryData historyData) {
        KidHistoryData kidHistoryDataNew = new KidHistoryData(kidInfo);

        List<HistoryData> historyDataList = new LinkedList<>();
        historyDataList.add(historyData);

        kidHistoryDataNew.getHistoryDataMap().put(playSites.getPlaySiteName(), historyDataList);

        kidHistoryDatas.add(kidHistoryDataNew);
    }

    private void addToExistingKidHistory(PlaySites playSites, int indexOfKidInfoPresent, HistoryData historyData) {
        Map<PlaySiteItem, List<HistoryData>> historyDataMap = kidHistoryDatas.get(indexOfKidInfoPresent).getHistoryDataMap();

        if (historyDataMap.containsKey(playSites.getPlaySiteName())) {
            historyDataMap.get(playSites.getPlaySiteName()).add(historyData);

        } else {
            List<HistoryData> historyDataList = new LinkedList<>();
            historyDataList.add(historyData);
            historyDataMap.put(playSites.getPlaySiteName(), historyDataList);
        }
    }

    public KidHistoryData getKidHistoryData(int ticketNumber) {
        KidHistoryData kidHistoryData = new KidHistoryData(ticketNumber);
        int kidHistoryDataIndex = kidHistoryDatas.indexOf(kidHistoryData);

        if (isKidInfoPresent(kidHistoryDataIndex)) {

            final KidHistoryData historyData = kidHistoryDatas.get(kidHistoryDataIndex);

            LOGGER.info("History data for Kid {}", historyData.toString());

            return historyData;
        } else {
            return null;
        }
    }

    private boolean isKidInfoPresent(int indexOfKidInfoPresent) {
        return indexOfKidInfoPresent > -1;
    }
}