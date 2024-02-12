package com.halawa.goodseasons.service.model;

import java.io.Serializable;

public class CampaignSummaryReportCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3310854390552683276L;
	
	private Long campaignId;
	
	public Long getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}
}
