package com.serjer.playground.service.utilization;

import com.serjer.playground.model.PlaySiteInfo;
import com.serjer.playground.model.PlaySites;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUtilizationService implements IUtilization {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUtilizationService.class);

    @Override
    public Double getUtilizationForPlaySite(final PlaySites playSites) {

        int totalCapacity = playSites.getPlaySitesAvailable().size() * playSites.getCapacityOfEachPlaySite();
        long totalPresent = playSites.getPlaySitesAvailable()
                .stream()
                .map(PlaySiteInfo::getKidInfoSet)
                .map(kidInfos -> kidInfos.size())
                .count();

        double utilization = (totalPresent / Double.parseDouble(String.valueOf(totalCapacity))) * 100.0;

        LOGGER.info("Total Utilization for {} with total items {} is {}", playSites.getPlaySiteName(), playSites.getPlaySitesAvailable().size(), utilization);

        return utilization;
    }


}