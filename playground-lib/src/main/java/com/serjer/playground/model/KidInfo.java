package com.serjer.playground.model;


import org.springframework.util.Assert;


public class KidInfo {

	private static int autoIncrTicketId;
	
	private int ticketNumber;
	private String kidName;
	private int age;
	private boolean canWaitInQueue;
	private boolean vip;
	
	
	private KidInfo(String kidName, int age, boolean canWaitInQueue, boolean isVip) {
        this.kidName = kidName;
        this.age = age;
        this.canWaitInQueue = canWaitInQueue;
        this.vip = isVip;
    }

    private void setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
    
    public int getTicketNumber() {
        return ticketNumber;
    }

    public String getKidName() {
        return kidName;
    }

    public int getAge() {
        return age;
    }

    public boolean isCanWaitInQueue() {
        return canWaitInQueue;
    }

    public boolean isVip() {
        return vip;
    }

    public void overrideMyWaitInQueueSetting(boolean canWaitInQueue) {
        this.canWaitInQueue = canWaitInQueue;
    }
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KidInfo kidInfo = (KidInfo) o;
        return ticketNumber == kidInfo.ticketNumber;
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ticketNumber;
		return result;
	}
	
	public static class KidInfoBuilder {
		
		private String kidName;
		private int age = 0;
		private boolean canWaitInQuery = true;
		private boolean vip = false;
		
		public KidInfoBuilder addKidName(String name) {
			this.kidName = name;
			return this;
		}
		
		public KidInfoBuilder addAge(int age) {
            this.age = age;
            return this;
        }

        public KidInfoBuilder notInterestedInWaiting() {
            this.canWaitInQuery = false;
            return this;
        }

        public KidInfoBuilder iAmAVip() {
            this.vip = true;
            return this;
        }

        private synchronized int getTicketId() {
            return KidInfo.autoIncrTicketId++;
        }
        
        public KidInfo build() {
        	Assert.hasText(kidName, "name must not be empty");
        	
        	KidInfo kidInfo = new KidInfo(this.kidName, 
        								  this.age, 
        								  this.canWaitInQuery, 
        								  this.vip); 
        	kidInfo.setTicketNumber(getTicketId());
        	
        	return kidInfo;
        }

	}
}
