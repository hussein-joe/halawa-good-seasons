package com.halawa.goodseasons.common.constants;

public enum UserStatusTypes {

	ACTIVE((short) 1),
	INACTIVE((short) 2);
	
	private short value;
	private UserStatusTypes(short value) {
		
		this.value = value;
	}
	
	public short getValue() {
		
		return this.value;
	}
}
