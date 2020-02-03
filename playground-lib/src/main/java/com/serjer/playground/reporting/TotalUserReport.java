package com.serjer.playground.reporting;

import java.util.*;

import com.serjer.playground.common.PlaySiteItem;

public class TotalUserReport {

    Map<PlaySiteItem, Set<Integer>> ticketNumbers = new LinkedHashMap<>();

    public TotalUserReport addTicketNumbers(int ticketNumber, PlaySiteItem playSiteItem) {

        if (!ticketNumbers.containsKey(playSiteItem)) {
            ticketNumbers.put(playSiteItem, new LinkedHashSet<>());
        }

        ticketNumbers.get(playSiteItem).add(ticketNumber);

        return this;
    }

    public Map<PlaySiteItem, Integer> getCountsForEachItem() {

        Map<PlaySiteItem, Integer> itemCountMap = new HashMap<>();
        ticketNumbers.forEach((playSiteItem, integers) -> {
            itemCountMap.put(playSiteItem, integers.size());
        });
        return itemCountMap;
    }
}