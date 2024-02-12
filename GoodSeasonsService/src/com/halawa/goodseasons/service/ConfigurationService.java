package com.halawa.goodseasons.service;


public interface ConfigurationService {

	String getProperty(String name);
	void updateProperty(String name, String value);
	/**
	 * Get the property split by the passed regExp. 
	 * @param name The name of the property.
	 * @param regExp The regular expression which should be used to split the configurable property.
	 * @return The split property values.
	 */
	String[] getProperty(String name, String regExp);
}
