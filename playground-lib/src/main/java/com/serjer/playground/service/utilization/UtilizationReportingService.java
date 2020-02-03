package com.serjer.playground.service.utilization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serjer.playground.common.PlaySiteItem;
import com.serjer.playground.model.PlaySites;
import com.serjer.playground.model.UtilizationSetting;
import com.serjer.playground.reporting.UtilizationReportingHolder;
import com.serjer.playground.service.PlaySiteService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.IntStream;

@Service
public class UtilizationReportingService {

    private UtilizationSetting utilizationSetting = new UtilizationSetting();
    private Map<Integer, List<UtilizationReportingHolder>> utilizationReportingHolderList = new LinkedHashMap<>();

    private final PlaySiteService playSiteService;
    private static ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR;

    @Autowired
    public UtilizationReportingService(PlaySiteService playSiteService) {
        this.playSiteService = playSiteService;
    }

    @PostConstruct
    void startUtilizationReporting() {
        SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1);
        SCHEDULED_THREAD_POOL_EXECUTOR.scheduleAtFixedRate(this::scheduledProcess, 1, 1, utilizationSetting.getTimeUnit());
    }

    private void scheduledProcess() {

        if (isBetweenWorkHours()) {

            Calendar instance = Calendar.getInstance();

            String date = instance.getTime().toString();

            int hour = instance.get(Calendar.HOUR_OF_DAY);

            playSiteService.getPlayListItemNames()
                    .forEach(playSiteItem -> {

                        updateUtilizationReportHolder(date, hour, playSiteItem);
                    });
        }
    }

    private void updateUtilizationReportHolder(String date, int hour, PlaySiteItem playSiteItem) {
        PlaySites playSitesInfo = playSiteService.getPlaySitesInfo(playSiteItem);
        String utilizationPercent = String.valueOf(playSitesInfo.getGetUtilizationPercent()).concat(" %");

        UtilizationReportingHolder utilizationReportingHolder = new UtilizationReportingHolder(
                date, playSiteItem, playSitesInfo.getPlaySitesAvailable().size(), playSitesInfo.getCapacityOfEachPlaySite(), utilizationPercent
        );

        if (utilizationReportingHolderList.containsKey(hour)) {
            utilizationReportingHolderList.get(hour).add(utilizationReportingHolder);
        } else {
            List<UtilizationReportingHolder> utilizationReportingHolders = new LinkedList<>();
            utilizationReportingHolders.add(utilizationReportingHolder);
            utilizationReportingHolderList.put(hour, utilizationReportingHolders);
        }
    }

    public void registerUtilizationReportInterval(final UtilizationSetting utilizationSetting) {
        this.utilizationSetting = utilizationSetting;
        utilizationReportingHolderList.clear();
        startUtilizationReporting();
    }

    public List<UtilizationReportingHolder> getUtilizationReport(Optional<Integer> startHourOptional, Optional<Integer> stopHourOptional) {

        List<UtilizationReportingHolder> utilizationReportList = new LinkedList<>();

        if (startHourOptional.isPresent() && utilizationReportingHolderList.containsKey(startHourOptional.get())) {

            int startHour = startHourOptional.get();

            if (stopHourOptional.isPresent() && stopHourOptional.get() >= startHour) {

                IntStream.range(startHour, stopHourOptional.get())
                        .forEach(hour -> {

                            utilizationReportingHolderList.forEach((key, list) -> {
                                utilizationReportList.addAll(list);
                            });
                        });

            } else {

                utilizationReportingHolderList.keySet()
                        .stream()
                        .filter(hour -> hour >= startHour)
                        .forEach(hour -> {
                            utilizationReportList.addAll(utilizationReportingHolderList.get(hour));
                        });
            }
        } else {
            utilizationReportingHolderList.forEach((key, list) -> {
                utilizationReportList.addAll(list);
            });
        }

        return utilizationReportList;
    }

    private boolean isBetweenWorkHours() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return utilizationSetting.getStartTimeIn24HourClock() >= hour && utilizationSetting.getStopTimeIn24HourClock() <= hour;
    }
}