package com.serjer.playground.model;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.serjer.playground.common.PlaySiteItem;
import com.serjer.playground.service.utilization.DefaultUtilizationService;
import com.serjer.playground.service.utilization.IUtilization;



public class PlaySites {
	
	private Set<PlaySiteInfo> playSitesAvailable;
	@JsonIgnore
	private int capacityOfEachPlaySite;
	private PlaySiteItem playSiteName;
	
	@JsonIgnore
	private int maxQueueSize;
	private IUtilization iUtilizationService;
   
	
	private PlaySites(Set<PlaySiteInfo> playSitesAvailable, int capacityOfEachPlaySite, PlaySiteItem playSiteName, IUtilization utilization, int maxQueueSize) {
        this.playSitesAvailable = playSitesAvailable;
        this.capacityOfEachPlaySite = capacityOfEachPlaySite;
        this.playSiteName = playSiteName;
        this.iUtilizationService = utilization;
        this.maxQueueSize= maxQueueSize;
    }

    public Set<PlaySiteInfo> getPlaySitesAvailable() {
        return playSitesAvailable;
    }

    public int getCapacityOfEachPlaySite() {
        return capacityOfEachPlaySite;
    }

    public PlaySiteItem getPlaySiteName() {
        return playSiteName;
    }

    @JsonIgnore
    public Double getGetUtilizationPercent() {
        return iUtilizationService.getUtilizationForPlaySite(this);
    }

    @JsonIgnore
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaySites playSites = (PlaySites) o;
        return playSiteName == playSites.playSiteName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playSiteName);
    }
    
    public static class PlaySitesBuilder{
    	private Set<PlaySiteInfo> playSitesAvailable = new LinkedHashSet<>(1);
    	private int capacityOfEachPlaySite = 1;
        private PlaySiteItem playSiteName;
        private IUtilization utilization = new DefaultUtilizationService();
        private int maxQueueSize = 10;
        
        public PlaySitesBuilder addPlaySiteItem(int numberOfItems, PlaySiteItem playSiteItem, int capacityOfEachPlaySite) {
            Assert.notNull(playSiteItem, "Play site item must not be empty");

            this.playSiteName = playSiteItem;
            this.capacityOfEachPlaySite = capacityOfEachPlaySite;

            IntStream.of(numberOfItems)
                    .forEach(item -> {
                        String name = playSiteName.name().concat("_").concat(String.valueOf(item));

                        PlaySiteInfo playSiteInfo = new PlaySiteInfo(name, new LinkedBlockingQueue<>(capacityOfEachPlaySite));
                        this.maxQueueSize = capacityOfEachPlaySite;
                        playSitesAvailable.add(playSiteInfo);
                    });

            return this;
        }
        
        public PlaySitesBuilder registerUtilizationService(IUtilization utilizationService) {
            this.utilization = utilizationService;
            return this;
        }

        public PlaySites build() {
        	Assert.notNull(playSiteName, "Play site name must not be empty");
            return new PlaySites(playSitesAvailable, capacityOfEachPlaySite, playSiteName, this.utilization, this.maxQueueSize);
        }
        
    }
}
