package com.halawa.common.exception;

public class HalawaBusinessException extends Exception {

	private static final long serialVersionUID = 1L;
	private HalawaErrorCode errorCode = HalawaErrorCode.GENERIC_ERROR;
	private Throwable cause;
	private Object[] parameters;
	
	public HalawaBusinessException() {
		super();
	}

	public HalawaBusinessException(String msg) {
		super( msg );
	}

	public HalawaBusinessException(Throwable cause) {
		super();
		this.cause = cause;
	}
   
	public HalawaBusinessException(String msg, Throwable cause) {
		super( msg );
		this.cause = cause;
	}
	
	public HalawaBusinessException(HalawaErrorCode errorCode) {

		this.errorCode = errorCode;
	}
	
	public HalawaBusinessException(HalawaErrorCode errorCode, String message) {

		this(message);
		this.errorCode = errorCode;
	}
	
	public HalawaBusinessException(HalawaErrorCode errorCode, Throwable cause) {

		this(cause);
		this.errorCode = errorCode;
	}	

	public HalawaBusinessException(HalawaErrorCode errorCode, String message, Throwable cause) {

		this(message, cause);
		this.errorCode = errorCode;
	}	
	
    public HalawaErrorCode getErrorCode(){
        return errorCode;
    }
    
    public Throwable getCause() {
		return cause;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object... parameters) {
		this.parameters = parameters;
	}
}
