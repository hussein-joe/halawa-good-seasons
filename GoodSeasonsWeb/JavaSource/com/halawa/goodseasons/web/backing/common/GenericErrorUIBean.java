package com.halawa.goodseasons.web.backing.common;

import javax.faces.application.FacesMessage;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.util.FacesUtil;

public class GenericErrorUIBean extends AbstractBean {
	
	private FacesMessage facesErrorMessage;
	
	public GenericErrorUIBean() {
		
		Exception exception = (Exception) FacesUtil.getSessionAttribute("GLOBAL_RENDER_ERROR");
		FacesUtil.removeSessionAttribute("GLOBAL_RENDER_ERROR");
		
		 if ( exception instanceof HalawaBusinessException ) {
			
			HalawaErrorCode errorCode = ((HalawaSystemException) exception).getErrorCode();
			this.facesErrorMessage = super.getMessage(errorCode.getErrorCode(), ((HalawaBusinessException) exception).getParameters());
			super.addMessage(errorCode);
			
		} else if ( exception instanceof HalawaSystemException ) {
			
			HalawaErrorCode errorCode = ((HalawaSystemException) exception).getErrorCode();
			this.facesErrorMessage = super.getMessage(errorCode.getErrorCode());
			super.addMessage(errorCode);
		} else {
			
			this.facesErrorMessage = super.getMessage(HalawaErrorCode.GENERIC_ERROR.getErrorCode());
		}
	}

	public FacesMessage getFacesErrorMessage() {
		
		if ( this.facesErrorMessage == null ) {
			
			this.facesErrorMessage = super.getMessage(HalawaErrorCode.GENERIC_ERROR.getErrorCode());
		}
		return facesErrorMessage;
	}
}
