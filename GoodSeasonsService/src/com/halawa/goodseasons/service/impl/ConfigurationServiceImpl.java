package com.halawa.goodseasons.service.impl;

import java.util.List;
import java.util.Properties;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.dao.ConfigurationDao;
import com.halawa.goodseasons.model.entity.Configuration;
import com.halawa.goodseasons.service.ConfigurationService;

public class ConfigurationServiceImpl implements ConfigurationService {

private static final HalawaLogger logger = HalawaLogger.getInstance(ConfigurationServiceImpl.class);
	
	private ConfigurationDao configurationDao;
	private Properties configuration = null;
	
	public String getProperty(String name) {
		
		String value = this.configuration.getProperty(name);
		return value;
	}
	
	public String[] getProperty(String name, String regExp) {
		
		String propetyValue = this.getProperty(name);
		if ( StringUtil.isEmpty(propetyValue) ) {
			
			return null;
		}
		
		return propetyValue.split(regExp);
	}



	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateProperty(String name, String value) {
		
		String oldValue = this.getProperty(name);
		if ( oldValue == null || oldValue.trim().equals("") ) {
			
			Configuration configuration = new Configuration();
			configuration.setName(name);
			configuration.setValue(value);
			this.configurationDao.addConfiguration( configuration );
		} else {
			
			this.configurationDao.updateConfiguration(name, value);
		}
		
		this.configuration.setProperty(name, value);
		
	}



	public void afterPropertiesSet() {
	
		logger.info("Loading the configurations from database");
		List<Configuration> configurationList = this.configurationDao.loadConfigurations();
		this.configuration = new Properties();
		for(Configuration config: configurationList) {
			
			String name = config.getName();
			String value = config.getValue();
			
			this.configuration.setProperty(name, value);
		}
		logger.info("The database configurations loaded successfully");
	}
	
	protected String resolvePlaceholder(String key, Properties properties) {
        return getProperty(key);
    }
	
	public void setConfigurationDao(ConfigurationDao configurationDao) {
		this.configurationDao = configurationDao;
	}

	public Properties getConfiguration() {
		
		return configuration;
	}
}
