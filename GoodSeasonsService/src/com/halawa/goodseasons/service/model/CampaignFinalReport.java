package com.halawa.goodseasons.service.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.halawa.goodseasons.model.entity.Campaign;

public class CampaignFinalReport {

	private Campaign campaign;
	private int countCampaignBenefits;
	/**
	 * This field will be filled by the number of needy cases which didn't take benefit during this campaign.
	 */
	private int countRemaingingCampaignBenefits;
	private List<CampaignFinalReportItem> campaignFinalReportItems = new ArrayList<CampaignFinalReportItem>();
	
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	public int getCountCampaignBenefits() {
		return countCampaignBenefits;
	}
	public void setCountCampaignBenefits(int countCampaignBenefits) {
		this.countCampaignBenefits = countCampaignBenefits;
	}
	public List<CampaignFinalReportItem> getCampaignFinalReportItems() {
		return campaignFinalReportItems;
	}
	public void setCampaignFinalReportItems(
			List<CampaignFinalReportItem> campaignFinalReportItems) {
		this.campaignFinalReportItems = campaignFinalReportItems;
	}
	
	public int getCountRemaingingCampaignBenefits() {
		return countRemaingingCampaignBenefits;
	}
	public void setCountRemaingingCampaignBenefits(
			int countRemaingingCampaignBenefits) {
		this.countRemaingingCampaignBenefits = countRemaingingCampaignBenefits;
	}
	public void addCampaignFinalReportItems(Collection<CampaignFinalReportItem> items) {
		
		for(CampaignFinalReportItem item: items) {
			
			this.campaignFinalReportItems.add(item);
		}
	}
}
