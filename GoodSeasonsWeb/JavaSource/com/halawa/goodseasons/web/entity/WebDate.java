package com.halawa.goodseasons.web.entity;

import java.util.Calendar;
import java.util.Date;

import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.web.util.WebErrorCodes;

public class WebDate {

	private String day;
	private String month;
	private String year;
	
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	
	public Date generateDate() throws HalawaSystemException{
		
		try {
			if ( this.year.length() == 2 ) {
				
				this.year = "19" + this.year;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
			calendar.set(Calendar.MONTH, Integer.parseInt(month) -1);
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
			return calendar.getTime();
		} catch(Exception exception) {
		
			throw new HalawaSystemException(WebErrorCodes.DATE_IS_NOT_VALID.getErrorCode(), exception);
		}
		
		
	}
	
	public static WebDate generateWebDate(Date date) {
		
		WebDate webDate = new WebDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		webDate.setDay( String.valueOf( calendar.get(Calendar.DAY_OF_MONTH) ) );
		webDate.setMonth( String.valueOf( calendar.get(Calendar.MONTH) + 1 ) );
		webDate.setYear(String.valueOf( calendar.get(Calendar.YEAR) ) );
		
		return webDate;
	}
}
