package com.halawa.goodseasons.service.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignActionItem;

public class PendingCampaignAction {

	private Campaign campaign;
	private List<PendingCampaignActionItem> pendingCampaignActionItems = new ArrayList<PendingCampaignActionItem>();
	
	public static class PendingCampaignActionItem {
		
		private CampaignActionItem campaignActionItem;
		private double validQuantity = 0;
		public CampaignActionItem getCampaignActionItem() {
			return campaignActionItem;
		}
		public void setCampaignActionItem(CampaignActionItem campaignActionItem) {
			this.campaignActionItem = campaignActionItem;
		}
		public double getValidQuantity() {
			return validQuantity;
		}
		public void setValidQuantity(double validQuantity) {
			this.validQuantity = validQuantity;
		}	
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public List<PendingCampaignActionItem> getPendingCampaignActionItems() {
		return pendingCampaignActionItems;
	}

	public void setPendingCampaignActionItems(
			List<PendingCampaignActionItem> pendingCampaignActionItems) {
		this.pendingCampaignActionItems = pendingCampaignActionItems;
	}
	
	public void addAllCampaignActionItems(Collection<CampaignActionItem> campaignActionItems) {
		
		for(CampaignActionItem item: campaignActionItems) {
			
			PendingCampaignActionItem pendingCampaignActionItem = new PendingCampaignActionItem();
			pendingCampaignActionItem.setCampaignActionItem(item);
			this.pendingCampaignActionItems.add(pendingCampaignActionItem);
		}
	}
}
