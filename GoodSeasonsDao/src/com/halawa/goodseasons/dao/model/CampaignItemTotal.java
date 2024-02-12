package com.halawa.goodseasons.dao.model;

import com.halawa.goodseasons.model.entity.CampaignItemType;

public class CampaignItemTotal {

	private CampaignItemType itemType;
	private double totalQuantity;
	
	
	
	public CampaignItemTotal(CampaignItemType itemType, double totalQuantity) {
		super();
		this.itemType = itemType;
		this.totalQuantity = totalQuantity;
	}
	
	public CampaignItemType getItemType() {
		return itemType;
	}
	public void setItemType(CampaignItemType itemType) {
		this.itemType = itemType;
	}
	public double getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	
	
}
