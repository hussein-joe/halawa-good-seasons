package com.halawa.goodseasons.web.util;

import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.sun.faces.util.MessageFactory;

public abstract class CommonValidators {

	private static final HalawaLogger logger = HalawaLogger.getInstance(CommonValidators.class);
	public void validateNationalId(FacesContext context, UIComponent toValidate, Object value) {
		
		if ( value == null ) {
			
			logger.fatal("The passed national id to be validated is null, which is not expected");
			return;
		}
		String nationalId = value.toString();
		if ( !validateNationaId(nationalId) ) {
			FacesMessage message = MessageFactory.getMessage("customValidators_common_nationalIdValidation");
			context.addMessage(toValidate.getClientId(context), message);
		}
	}

	protected boolean validateNationalId_14(String nationalId) {

		String pattern = "[0-9]{7}|[0-9]{14}";
		if ( nationalId == null ) {
			
			logger.fatal("The passed national id to be validated is null, which is not expected");
			return false;
		}
		if ( nationalId.trim().length() < 7 || nationalId.trim().length() > 14 || !Pattern.matches(pattern, nationalId.trim())) {
			
			return false;
		}
		
		return true;
	}
	
	protected boolean validateNationaId(String nationalId) {
		
		String pattern = "[0-9]{14}";
		if ( nationalId == null ) {
			
			logger.fatal("The passed national id to be validated is null, which is not expected");
			return false;
		}
		if ( nationalId.trim().length() > 14 || !Pattern.matches(pattern, nationalId.trim())) {
			
			return false;
		}
		
		return true;
	}
}
