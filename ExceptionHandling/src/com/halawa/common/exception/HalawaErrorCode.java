package com.halawa.common.exception;

import java.io.Serializable;

public class HalawaErrorCode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7472365749970741732L;
	private String errorCode;

	public HalawaErrorCode(String errorCode) {
		
		this.errorCode = errorCode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((errorCode == null) ? 0 : errorCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HalawaErrorCode other = (HalawaErrorCode) obj;
		if (errorCode == null) {
			if (other.errorCode != null)
				return false;
		} else if (!errorCode.equals(other.errorCode))
			return false;
		return true;
	}
	
	public static final HalawaErrorCode GENERIC_ERROR = new HalawaErrorCode("GENERIC_ERROR");
	public static final HalawaErrorCode INVALID_PARAMETER = new HalawaErrorCode("INVALID_PARAMETER");
	public static final HalawaErrorCode COMMUNICATION_ERROR = new HalawaErrorCode("MAIN_BRANCH_COMMUNICATION_ERROR");
}
