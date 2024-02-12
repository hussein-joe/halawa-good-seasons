package com.halawa.goodseasons.common.constants;

import com.halawa.common.exception.HalawaErrorCode;

public enum NeedyCaseErrorCodes {

	NEEDYCASE_IS_NOT_EXIST(new HalawaErrorCode("needyCase_needyCaseIsNotExist")),
	NEEDYCASE_IS_NOT_ACTIVE(new HalawaErrorCode("needyCase_needyCaseIsNotActive")),
	ADD_NEEDYCASE_REGULAR_ALREADY_EXIST_AS_REGULAR(new HalawaErrorCode("needyCase_add_regular_alreadyExistAsRegular")),
	ADD_NEEDYCASE_REGULAR_ALREADY_EXIST_AS_IRREGULAR(new HalawaErrorCode("needyCase_add_regular_alreadyExistAsIrregular")),
	
	
	ADD_NEEDYCASE_IRREGULAR_ALREADY_EXIST_AS_REGULAR(new HalawaErrorCode("needyCase_add_irregular_alreadyExistAsRegular")),
	ADD_NEEDYCASE_IRREGULAR_ALREADY_EXIST_AS_IRREGULAR(new HalawaErrorCode("needyCase_add_irregular_alreadyExistAsIrregular")),
	
	UPDATE_NEEDYCASE_NATIONAL_ID_ALREADY_EXIST(new HalawaErrorCode("needyCase_update_nationalIdAlreadyExist")),
	
	CONVERT_TO_REGULAR_NEEDY_CASE_IS_ALREADY_REGULAR(new HalawaErrorCode("needyCase_convert_needyCaseIsAlreadyRegular")),
	CONVERT_IRREGULAR_NEEDYCASE_IS_NOT_EXIST(new HalawaErrorCode("needyCase_convert_noIrregularNeedyCaseExist"));
	
	private HalawaErrorCode errorCode;
	private NeedyCaseErrorCodes(HalawaErrorCode errorCode) {
		
		this.errorCode = errorCode;
	}
	
	public HalawaErrorCode getErrorCode() {
		
		return this.errorCode;
	}
}
