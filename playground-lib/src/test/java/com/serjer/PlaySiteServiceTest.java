package com.serjer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.serjer.playground.common.PlaySiteItem;
import com.serjer.playground.exception.NotFoundException;
import com.serjer.playground.model.PlaySiteInfo;
import com.serjer.playground.model.PlaySites;
import com.serjer.playground.service.PlaySiteService;
import com.serjer.playground.service.validation.PlaySiteValidateService;


class PlaySiteServiceTest {
	
	private PlaySiteService playSiteService = new PlaySiteService(new PlaySiteValidateService());
	
	@Test
	public void getPlaySitesInfoNotFound() {
		try {
        playSiteService.getPlaySitesInfo(PlaySiteItem.carousel);
   		} catch(NotFoundException e) {
   			assertEquals("PlaySite Info not found", e.getMessage());
		}
    }
	
	@Test
    public void registerPlaySiteMethodTest() {
        PlaySites.PlaySitesBuilder playSitesBuilder = new PlaySites.PlaySitesBuilder()
                .addPlaySiteItem(2, PlaySiteItem.double_swing, 1);
        playSiteService.registerPlaySite(playSitesBuilder);
      

        PlaySites playSites = playSiteService.getPlaySitesInfo(PlaySiteItem.double_swing);
        Set <PlaySiteInfo> playSitesAvailable = playSites.getPlaySitesAvailable();
  
        assertNotNull(playSites); 
        assertEquals(PlaySiteItem.double_swing, playSites.getPlaySiteName());
        assertEquals(2, playSites.getPlaySitesAvailable().size());
        assertEquals(1, playSites.getCapacityOfEachPlaySite());
        assertTrue(playSitesAvailable.stream().anyMatch(e -> e.getName().equals("double_swing_1")));
        							
    }	
}
