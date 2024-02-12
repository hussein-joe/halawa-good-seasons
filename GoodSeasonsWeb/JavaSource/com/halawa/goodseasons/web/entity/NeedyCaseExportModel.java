package com.halawa.goodseasons.web.entity;

import com.halawa.goodseasons.common.constants.NeedyCaseTypes;

public class NeedyCaseExportModel {

	private short needyCaseTypeValue;

	public short getNeedyCaseTypeValue() {
		return needyCaseTypeValue;
	}

	public void setNeedyCaseTypeValue(short needyCaseTypeValue) {
		this.needyCaseTypeValue = needyCaseTypeValue;
	}
	
	public NeedyCaseTypes getNeedyCaseType() {
		
		return NeedyCaseTypes.getNeedyCaseType(needyCaseTypeValue);
	}
}
