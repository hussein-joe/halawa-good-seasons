package com.halawa.goodseasons.service.model;

import com.halawa.goodseasons.model.entity.CampaignItemType;

public class CampaignFinalReportItem {

	private CampaignItemType campaignItemType;
	private double quantity;
	
	
	public CampaignFinalReportItem(CampaignItemType campaignItemType,
			double quantity) {
		super();
		this.campaignItemType = campaignItemType;
		this.quantity = quantity;
	}
	
	public CampaignItemType getCampaignItemType() {
		return campaignItemType;
	}
	public void setCampaignItemType(CampaignItemType campaignItemType) {
		this.campaignItemType = campaignItemType;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public void addItem(double q) {
		
		this.quantity += q;
	}
}
