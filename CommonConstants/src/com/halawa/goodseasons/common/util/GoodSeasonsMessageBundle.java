package com.halawa.goodseasons.common.util;

import java.util.ResourceBundle;

public class GoodSeasonsMessageBundle {
	
	private ResourceBundle resourceBundle;
	private static GoodSeasonsMessageBundle instance;
	
	private GoodSeasonsMessageBundle() {
		
	}
	
	public static GoodSeasonsMessageBundle getInstance() {
		
		if ( instance == null ) {
			
			instance = new GoodSeasonsMessageBundle();
			try {
				instance.resourceBundle = ResourceBundle.getBundle("com.halawa.goodseasons.web.messages.goodseasonsMessages");
			} catch(Exception exp) {
				
				exp.printStackTrace();
			}
		}
		
		return instance; 
	}
	
	public String getMessage(String key) {
		
		return this.resourceBundle.getString(key);
	}
}
