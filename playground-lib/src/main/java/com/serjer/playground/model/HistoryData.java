package com.serjer.playground.model;

import java.util.Date;


public class HistoryData {
	
	private Date startTime;
	private Date endTime;
	private long totalTimeInMillis;
	
	public HistoryData(Date startTime, Date endTime, long totalTimeInMillis) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalTimeInMillis = totalTimeInMillis;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public long getTotalTimeInMillis() {
        return totalTimeInMillis;
    }

}
