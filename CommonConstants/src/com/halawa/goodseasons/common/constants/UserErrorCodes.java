package com.halawa.goodseasons.common.constants;

import com.halawa.common.exception.HalawaErrorCode;

public enum UserErrorCodes {

	INVALID_USER_NAME(new HalawaErrorCode("user_invalid_username")),
	INVALID_PASSWORD(new HalawaErrorCode("user_invalid_password")),
	USER_IS_INACTIVE(new HalawaErrorCode("user_inactiveUser"));
	
	
	
	
	
	private HalawaErrorCode errorCode;
	private UserErrorCodes(HalawaErrorCode errorCode) {
		
		this.errorCode = errorCode;
	}
	
	public HalawaErrorCode getErrorCode() {
		
		return this.errorCode;
	}
}

