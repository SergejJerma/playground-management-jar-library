package com.serjer.playground.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.serjer.playground.common.PlaySiteItem;
import com.serjer.playground.reporting.TotalUserReport;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TotalCountReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TotalCountReportService.class);
    private static Map<String, TotalUserReport> totalUserReports = new LinkedHashMap<>();

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Will be accessible only within the library
    void updateTotalCountForTheDay(Date currentDate, int ticketNumber, PlaySiteItem playSiteItem) {

        String stringFormattedDate = DATE_FORMAT.format(currentDate);

        if (totalUserReports.containsKey(stringFormattedDate)) {

            totalUserReports.get(stringFormattedDate).addTicketNumbers(ticketNumber, playSiteItem);

        } else {

            TotalUserReport totalUserReport = new TotalUserReport()
                    .addTicketNumbers(ticketNumber, playSiteItem);

            totalUserReports.put(stringFormattedDate, totalUserReport);
        }

        LOGGER.info("Total count for the {} is added", stringFormattedDate);
    }

    public Map getTotalUserReport(String dateFormatted) {
        if (totalUserReports.containsKey(dateFormatted)) {

            TotalUserReport totalUserReport = totalUserReports.get(dateFormatted);

            final Map<PlaySiteItem, Integer> countsForEachItem = totalUserReport.getCountsForEachItem();

            LOGGER.info("Total count for {} is {}", dateFormatted, countsForEachItem);

            return countsForEachItem;
        }
        return Collections.EMPTY_MAP;
    }
}