package com.halawa.goodseasons.service.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignItemType;

public class CampaignActionsUserReportResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7660302407855626929L;
	private List<CampaignActionsUserReportResultItem> campaignActionsUserReportResultItems = new ArrayList<CampaignActionsUserReportResultItem>();
	
	public List<CampaignActionsUserReportResultItem> getCampaignActionsUserReportResultItems() {
		return campaignActionsUserReportResultItems;
	}

	public void setCampaignActionsUserReportResultItems(
			List<CampaignActionsUserReportResultItem> campaignActionsUserReportResultItems) {
		this.campaignActionsUserReportResultItems = campaignActionsUserReportResultItems;
	}

	public void addCampaignActionsUserReportResultItem(CampaignActionsUserReportResultItem item) {
		
		this.campaignActionsUserReportResultItems.add(item);
	}
	
	public static class CampaignActionsUserReportResultItem implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4480555838226528471L;
		private Campaign campaign;
		private int numberOfCases;
		private List<TotalCampaignItem> totalCampaignItems = new ArrayList<TotalCampaignItem>();
		
		public Campaign getCampaign() {
			return campaign;
		}
		public void setCampaign(Campaign campaign) {
			this.campaign = campaign;
		}
		public int getNumberOfCases() {
			return numberOfCases;
		}
		public void setNumberOfCases(int numberOfCases) {
			this.numberOfCases = numberOfCases;
		}
		public List<TotalCampaignItem> getTotalCampaignItems() {
			return totalCampaignItems;
		}
		public void setTotalCampaignItems(List<TotalCampaignItem> totalCampaignItems) {
			this.totalCampaignItems = totalCampaignItems;
		}
		
		public void addTotalCampaignItem(TotalCampaignItem item) {
			
			this.totalCampaignItems.add(item);
		}
	}
	
	public static class TotalCampaignItem implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3235953371793881854L;
		private CampaignItemType campaignItemType;
		private double totalQuantity;
		public CampaignItemType getCampaignItemType() {
			return campaignItemType;
		}
		public void setCampaignItemType(CampaignItemType campaignItemType) {
			this.campaignItemType = campaignItemType;
		}
		public double getTotalQuantity() {
			return totalQuantity;
		}
		public void setTotalQuantity(double totalQuantity) {
			this.totalQuantity = totalQuantity;
		}
	}
}
