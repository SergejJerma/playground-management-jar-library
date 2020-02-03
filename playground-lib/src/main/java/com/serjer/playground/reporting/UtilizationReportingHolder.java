package com.serjer.playground.reporting;

import com.serjer.playground.common.PlaySiteItem;

public class UtilizationReportingHolder {

    private String reportDate;
    private PlaySiteItem playSiteItem;
    private int numberOfPlaySiteItems;
    private int maxSizeOfIndividualPlaySite;
    private String utilizationPercent;

    public UtilizationReportingHolder(String reportDate, PlaySiteItem playSiteItem, int numberOfPlaySiteItems, int maxSizeOfIndividualPlaySite, String utilizationPercent) {
        this.reportDate = reportDate;
        this.playSiteItem = playSiteItem;
        this.numberOfPlaySiteItems = numberOfPlaySiteItems;
        this.maxSizeOfIndividualPlaySite = maxSizeOfIndividualPlaySite;
        this.utilizationPercent = utilizationPercent;
    }

    public String getReportDate() {
        return reportDate;
    }

    public PlaySiteItem getPlaySiteItem() {
        return playSiteItem;
    }

    public int getNumberOfPlaySiteItems() {
        return numberOfPlaySiteItems;
    }

    public int getMaxSizeOfIndividualPlaySite() {
        return maxSizeOfIndividualPlaySite;
    }

    public String getUtilizationPercent() {
        return utilizationPercent;
    }

    @Override
    public String toString() {
        return "" +
                "ReportDate = '" + reportDate +
                ", PlaySiteItem = " + playSiteItem +
                ", Number Of PlaySite Items = " + numberOfPlaySiteItems +
                ", Max Size Of IndividualPlaySite = " + maxSizeOfIndividualPlaySite +
                ", Utilization = '" + utilizationPercent;
    }
}