package com.halawa.goodseasons.common.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HalawaLogger implements Log{
	
	protected Log logger;
	
	private HalawaLogger(String className) {
		
		this.logger = LogFactory.getLog(className);
	}
	
	private HalawaLogger(Class<? extends Object> clazz) {
		
		this.logger = LogFactory.getLog(clazz);
	}
	
	public static HalawaLogger getInstance(Class<? extends Object> clazz) {
		
		return new HalawaLogger(clazz);
	}
	
	public static HalawaLogger getInstance(String className) {
		
		return new HalawaLogger(className);
	}

	public void debug(Object arg0) {
		
		logger.debug(arg0);
	}

	public void debug(Object arg0, Throwable arg1) {
		
		logger.debug(arg0, arg1);
	}

	public void error(Object arg0) {
		
		logger.error(arg0);
	}

	public void error(Object arg0, Throwable arg1) {
		
		logger.error(arg0, arg1);
	}

	public void fatal(Object arg0) {
		
		logger.fatal(arg0);
	}

	public void fatal(Object arg0, Throwable arg1) {
		
		logger.fatal(arg0, arg1);
	}

	public void info(Object arg0) {
		
		logger.info(arg0);
	}

	public void info(Object arg0, Throwable arg1) {
		
		logger.info(arg0, arg1);
	}

	public boolean isDebugEnabled() {
		
		return logger.isDebugEnabled();
	}

	public boolean isErrorEnabled() {

		return logger.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		
		return logger.isFatalEnabled();
	}

	public boolean isInfoEnabled() {
		
		return logger.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		
		return logger.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		
		return logger.isWarnEnabled();
	}

	public void trace(Object arg0) {
		
		logger.trace(arg0);
	}

	public void trace(Object arg0, Throwable arg1) {
		
		logger.trace(arg0, arg1);
	}

	public void warn(Object arg0) {
		
		logger.warn(arg0);
	}

	public void warn(Object arg0, Throwable arg1) {
		
		logger.warn(arg0, arg1);
	}
	
	
}