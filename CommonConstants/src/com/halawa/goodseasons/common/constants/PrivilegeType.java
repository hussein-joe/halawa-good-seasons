package com.halawa.goodseasons.common.constants;

public enum PrivilegeType {

	RIGHT_MENU_PRIVILEGE((short) 1),
	USER_PRIVILEGE((short) 2);
	
	private short type;
	private PrivilegeType(short type) {
		
		this.type = type;
	}
	public short getType() {
		return type;
	}
	
	
	public static PrivilegeType getPrivilegeType(short type) {
		
		PrivilegeType[] typeValues = values();
		for(PrivilegeType privilegeType: typeValues) {
			
			if ( privilegeType.getType() == type ) {
				
				return privilegeType;
			}
		}
		
		return USER_PRIVILEGE;
	}
}
