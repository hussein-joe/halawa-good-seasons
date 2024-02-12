package com.halawa.goodseasons.dao.jpa;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.halawa.goodseasons.dao.BaseDao;
import com.halawa.goodseasons.dao.ConfigurationDao;
import com.halawa.goodseasons.model.entity.Configuration;

public class JpaConfigurationDao extends BaseDao implements ConfigurationDao{

	public Configuration loadConfiguration(String name) {
		
		Query query = entityManager.createNamedQuery("loadConfigurationByName");
		query.setParameter("name", name);
		
		try {
			
			return (Configuration) query.getSingleResult();
		} catch(NoResultException exception) {
			
			return  null;
		}
	}
	
	public void updateConfiguration(String name, String value) {
		
		Query query = this.entityManager.createNamedQuery("updateConfiguration");
		query.setParameter("propertyName", name);
		query.setParameter("propertyValue", value);
		
		query.executeUpdate();
	}

	

	public void addConfiguration(Configuration configuration) {

		this.entityManager.persist(configuration);
	}

	@SuppressWarnings("unchecked")
	public List<Configuration> loadConfigurations() {
		
		Query query = this.entityManager.createNamedQuery("loadConfigurations");
		return (List<Configuration>) query.getResultList();
	}

}
