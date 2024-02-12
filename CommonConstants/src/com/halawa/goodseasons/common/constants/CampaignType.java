package com.halawa.goodseasons.common.constants;

public enum CampaignType {

	REGULAR_CAMPAIGN((short) 1),
	EXCEPTIONAL_CAMPAIGN((short) 2),
	BOTH((short) 3);
	
	private short type;
	private CampaignType(short type) {
		
		this.type = type;
	}
	public short getType() {
		return type;
	}
}
