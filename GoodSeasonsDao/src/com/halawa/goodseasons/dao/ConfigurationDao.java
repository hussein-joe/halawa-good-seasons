package com.halawa.goodseasons.dao;

import java.util.List;

import com.halawa.goodseasons.model.entity.Configuration;

public interface ConfigurationDao {

	List<Configuration> loadConfigurations();
	Configuration loadConfiguration(String name);
	void updateConfiguration(String name, String value);
	void addConfiguration(Configuration configuration);
}
