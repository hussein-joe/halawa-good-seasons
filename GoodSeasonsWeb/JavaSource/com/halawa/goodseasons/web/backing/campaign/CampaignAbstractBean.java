package com.halawa.goodseasons.web.backing.campaign;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.web.backing.AbstractBean;

public abstract class CampaignAbstractBean extends AbstractBean {

	private static final HalawaLogger logger = HalawaLogger.getInstance(CampaignAbstractBean.class);
	private List<SelectItem> activeCampaignsItems;
	private List<SelectItem> closedCampaignsItems;
	
	public List<SelectItem> getActiveCampaignsItems() {
		
		if ( this.activeCampaignsItems == null ) {
			
			logger.debug("Loading the active campaigns");
			try {
				this.activeCampaignsItems = new ArrayList<SelectItem>();
				List<Campaign> campaigns = this.campaignService.loadActiveCampaigns();
				for(Campaign campaign: campaigns) {
					
					SelectItem item = new SelectItem(campaign.getId(), campaign.getName());
					this.activeCampaignsItems.add(item);
				}
			}  catch(Exception exception) {
				
				logger.fatal("Failed to load the active campaigns", exception);
			}
		}
		return this.activeCampaignsItems;
	}
	
	public List<SelectItem> getClosedCampaignsItems() {
		
		if ( this.closedCampaignsItems == null ) {
			
			logger.debug("Loading the closed campaigns");
			try {
				this.closedCampaignsItems = new ArrayList<SelectItem>();
				List<Campaign> campaigns = this.campaignService.loadOldCampaigns();
				for(Campaign campaign: campaigns) {
					
					SelectItem item = new SelectItem(campaign.getId(), campaign.getName());
					this.closedCampaignsItems.add(item);
				}
			}  catch(Exception exception) {
				
				logger.fatal("Failed to load the old campaigns", exception);
			}
		}
		return this.closedCampaignsItems;
	}
	
	public List<SelectItem> getAllCampaigns() {
		
		List<SelectItem> campaigns = new ArrayList<SelectItem>();
		campaigns.addAll( this.getActiveCampaignsItems() );
		campaigns.addAll( this.getClosedCampaignsItems() );
		
		return campaigns;
	}
}
