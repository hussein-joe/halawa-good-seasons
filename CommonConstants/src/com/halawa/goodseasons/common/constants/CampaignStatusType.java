package com.halawa.goodseasons.common.constants;

public enum CampaignStatusType {

	ACTIVE((short) 1),
	CLOSED((short) 2),
	SUSPENDED((short) 10);
	
	private short value;
	private CampaignStatusType(short value) {
		
		this.value = value;
	}
	
	public short getValue() {
		
		return this.value;
	}
}
