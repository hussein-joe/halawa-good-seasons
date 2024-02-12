package com.halawa.goodseasons.common.constants;

public enum CampaignActionStatusType {

	DELIVERED_TO_NEEDYCASE((short) 1),
	DELIVERED_TO_NEEDYCASE_AFTER_PENDING((short) 2),
	PENDING((short) 3),
	PENDING_REFUSED((short) 4),
	DELIVERED_AFTER_PENDING_FROM_ANOTHER_CAMPAIGN((short) 5),
	EXCEPTIONAL_CAMPAIGN_FORGOT_ACTION((short) 6);
	
	private short value;
	private CampaignActionStatusType(short value) {
		
		this.value = value;
	}
	
	public short getValue() {
		
		return this.value;
	}
}
