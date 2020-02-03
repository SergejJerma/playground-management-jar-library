package com.serjer.playground.service.validation;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.serjer.playground.exception.MultipleEntryInSamePlaySiteException;
import com.serjer.playground.model.KidInfo;
import com.serjer.playground.model.PlaySiteInfo;
import com.serjer.playground.model.PlaySites;

@Service
public class PlaySiteValidateService {

	
	public boolean canAddKidToPlaySite(final PlaySites playSites, final KidInfo kidInfo) {
		 
		 Assert.notNull(playSites, "Play Sites not available");
		 
		 if (checkKidAlreadyInThePlaySite(playSites, kidInfo)) {
			 throw new MultipleEntryInSamePlaySiteException("Kid already in the playsite");
		 }
		 
		 Optional<PlaySiteInfo> playSiteItemWithFreePlaces = playSites.getPlaySitesAvailable()
				 .stream()
				 .filter(playSiteInfo -> playSiteInfo.getKidInfoSet().size() < playSites.getCapacityOfEachPlaySite())
				 .findAny();
		 return playSiteItemWithFreePlaces.isPresent();
	}
		
	
	private boolean checkKidAlreadyInThePlaySite(final PlaySites playSites, final KidInfo kidInfo) {
		
		 long kidPresentCount = playSites.getPlaySitesAvailable()
				 .stream()
				 .filter(playSiteInfo -> playSiteInfo.getKidInfoSet().contains(kidInfo))
				 .count();
		 
		 return kidPresentCount > 0;
	}
	

	public boolean canKidWaitForHisTurn(PlaySites playSites, KidInfo kidInfo) {
		
		if (!kidInfo.isCanWaitInQueue()) {
			
			return playSites.getPlaySitesAvailable()
					.stream()
					.filter(playSiteInfo -> playSiteInfo.getKidInfoSet().isEmpty())
					.findAny()
					.isPresent();
		}
		return true;
	}
}