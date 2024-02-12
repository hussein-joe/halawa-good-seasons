package com.halawa.goodseasons.common.constants;

public enum NeedyCaseStatusTypes {

	ACTIVE((short) 1),
	BLOCKED((short) 2),
	DELETED((short) 10),
	CONVERTED_AS_REGULAR((short) 11);
	
	private short value;
	private NeedyCaseStatusTypes(short value) {
		
		this.value = value;
	}
	
	public short getValue() {
		
		return this.value;
	}
}
