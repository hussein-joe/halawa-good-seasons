package com.halawa.goodseasons.common.constants;

public enum NeedyCaseTypes {

	REGULAR((short) 1),
	IRREGULAR((short) 2),
	BOTH((short) 3);
	
	private short type;
	private NeedyCaseTypes(short type) {
		
		this.type = type;
	}
	public short getType() {
		return type;
	}
	
	public static NeedyCaseTypes getNeedyCaseType(short type) {
		
		for(NeedyCaseTypes needyCaseType: NeedyCaseTypes.values()) {
			
			if ( needyCaseType.getType() == type ) 
				return needyCaseType;
		}
		
		return NeedyCaseTypes.BOTH;
	}
}
