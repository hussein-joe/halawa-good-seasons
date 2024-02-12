package com.halawa.goodseasons.common.constants;

public enum Status2Values {

	ACTIVE((short) 1),
	INACTIVE((short) 2);
	
	private short value;
	private Status2Values(short value) {
		
		this.value = value;
	}
	
	public short getValue() {
		
		return this.value;
	}
	
	public static Status2Values getStatusByValue(short value) {
		
		Status2Values[] enumValues = values();
		Status2Values requiredStatus = ACTIVE;
		for(Status2Values status: enumValues) {
			
			if ( status.getValue() == value ) {
				
				requiredStatus = status;
				break;
			}
		}
		
		return requiredStatus;
	}
}