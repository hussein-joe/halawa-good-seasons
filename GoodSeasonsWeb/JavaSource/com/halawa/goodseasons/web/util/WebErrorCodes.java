package com.halawa.goodseasons.web.util;

import com.halawa.common.exception.HalawaErrorCode;

public enum WebErrorCodes {

	/*
	 * This error code constant should be used to display error message if the user doesn't select any entry from 
	 * manage page to edit/delete.
	 */
	NO_ENTRY_SELECTED(new HalawaErrorCode("no_entry_selected")),
	
	CUSTOM_VALIDATION_NOTVALID(new HalawaErrorCode("custom_validation_notValid")),
	USER_NOT_LOGGED_IN(new HalawaErrorCode("user_userIsNotLoggedIn")),
	DATE_IS_NOT_VALID(new HalawaErrorCode("date_dateIsNotValid"));
	
	private HalawaErrorCode errorCode;
	private WebErrorCodes(HalawaErrorCode errorCode) {
		
		this.errorCode = errorCode;
	}
	
	public HalawaErrorCode getErrorCode() {
		
		return this.errorCode;
	}
}
