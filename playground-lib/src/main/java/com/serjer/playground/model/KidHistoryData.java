package com.serjer.playground.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.serjer.playground.common.PlaySiteItem;

import lombok.Data;

@Data
public class KidHistoryData {
	
	private int ticketNo;
	private KidInfo kidInfo;
	private Map<PlaySiteItem, List<HistoryData>> historyDataMap = new HashMap<>();
	
	 public KidHistoryData(KidInfo kidInfo) {
	        this.kidInfo = kidInfo;
	        this.ticketNo = kidInfo.getTicketNumber();
	    }
	 
	 public KidHistoryData(int ticketNo) {
	        this.ticketNo = ticketNo;
	    }

	@Override
	public String toString() {
	        return "KidHistoryData{" +
	                "ticketNo=" + ticketNo +
	                ", kidInfo=" + kidInfo +
	                ", historyDataMap=" + historyDataMap +
	                '}';
	    }

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KidHistoryData that = (KidHistoryData) o;
        return ticketNo == that.ticketNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ticketNo;
		return result;
	}

	 
}
