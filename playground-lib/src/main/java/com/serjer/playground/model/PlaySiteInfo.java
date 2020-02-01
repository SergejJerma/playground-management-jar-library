package com.serjer.playground.model;

import java.util.concurrent.LinkedBlockingQueue;


public class PlaySiteInfo {

	private String name;
	private LinkedBlockingQueue<KidInfo> kidInfoSet;
	
	PlaySiteInfo(String name, LinkedBlockingQueue<KidInfo> kidInfoSet) {
        this.name = name;
        this.kidInfoSet = kidInfoSet;
    }

    public String getName() {
        return name;
    }

    public LinkedBlockingQueue<KidInfo> getKidInfoSet() {
        return kidInfoSet;
    }
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlaySiteInfo playSiteInfo = (PlaySiteInfo) o;
		if (name == null) {
			if (playSiteInfo.name != null)
				return false;
		} else if (!name.equals(playSiteInfo.name))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	
}
