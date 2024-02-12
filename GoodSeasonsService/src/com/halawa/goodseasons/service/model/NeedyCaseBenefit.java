package com.halawa.goodseasons.service.model;

import java.util.List;

import com.halawa.goodseasons.model.entity.CampaignItem;
import com.halawa.goodseasons.model.entity.NeedyCase;

public class NeedyCaseBenefit {

	private NeedyCase needyCase;
	private List<CalculatedCampaignItem> calculatedCampaignItems;
	
	public NeedyCase getNeedyCase() {
		return needyCase;
	}
	public void setNeedyCase(NeedyCase needyCase) {
		this.needyCase = needyCase;
	}
	
	public List<CalculatedCampaignItem> getCalculatedCampaignItems() {
		return calculatedCampaignItems;
	}
	public void setCalculatedCampaignItems(
			List<CalculatedCampaignItem> calculatedCampaignItems) {
		this.calculatedCampaignItems = calculatedCampaignItems;
	}


	public static class CalculatedCampaignItem {
		
		private CampaignItem campaignItem;
		private float calculatedValue;
		public CampaignItem getCampaignItem() {
			return campaignItem;
		}
		public void setCampaignItem(CampaignItem campaignItem) {
			this.campaignItem = campaignItem;
		}
		public float getCalculatedValue() {
			return calculatedValue;
		}
		public void setCalculatedValue(float calculatedValue) {
			this.calculatedValue = calculatedValue;
		}
	}
}
