package com.halawa.common.exception;


public class HalawaSystemException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private HalawaErrorCode errorCode = HalawaErrorCode.GENERIC_ERROR;
	private Throwable cause;
	
	public HalawaSystemException() {
		super();
	}

	public HalawaSystemException(String msg) {
		super( msg );
	}

	public HalawaSystemException(Throwable cause) {
		super();
		this.cause = cause;
	}
   
	public HalawaSystemException(String msg, Throwable cause) {
		super( msg );
		this.cause = cause;
	}
	
	public HalawaSystemException(HalawaErrorCode errorCode) {

		this.errorCode = errorCode;
	}
	
	public HalawaSystemException(HalawaErrorCode errorCode, String message) {

		this(message);
		this.errorCode = errorCode;
	}
	
	public HalawaSystemException(HalawaErrorCode errorCode, Throwable cause) {

		this(cause);
		this.errorCode = errorCode;
	}	

	public HalawaSystemException(HalawaErrorCode errorCode, String message, Throwable cause) {

		this(message, cause);
		this.errorCode = errorCode;
	}	
	
    public HalawaErrorCode getErrorCode(){
        return errorCode;
    }
    
    public Throwable getCause() {
		return cause;
	}
}
