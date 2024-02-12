package com.halawa.goodseasons.dao.jpa;

import java.util.List;

import javax.persistence.Query;

import com.halawa.goodseasons.dao.BaseDao;
import com.halawa.goodseasons.dao.DataImporterDao;
import com.halawa.goodseasons.model.entity.ImportedDataItem;

public class JpaDataImporterDao extends BaseDao implements DataImporterDao {
	
	@SuppressWarnings("unchecked")
	public List<ImportedDataItem> loadNextItems(int start, int pageSize) {
		
		Query query = this.entityManager.createNamedQuery("loadDataItems");
		query.setFirstResult(start);
		query.setMaxResults(pageSize);
		
		return query.getResultList();
	}

}
