package com.halawa.goodseasons.dao.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.halawa.goodseasons.common.constants.NeedyCaseStatusTypes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.dao.BaseDao;
import com.halawa.goodseasons.dao.NeedyCaseDao;
import com.halawa.goodseasons.dao.PaginationData;
import com.halawa.goodseasons.model.NeedyCaseSearchCriteria;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.RegularCase;

public class JpaNeedyCaseDao extends BaseDao implements NeedyCaseDao {

	public NeedyCase loadNeedyCaseByNationalId(String nationalId) {
		
		Query query = this.entityManager.createNamedQuery("loadNeedyCaseByNationalId");
		query.setParameter("nationalId", nationalId.trim());
		query.setParameter("deletedNeedyCaseStatusValues", getNeedyCasesStatusValues(NeedyCaseStatusTypes.DELETED, NeedyCaseStatusTypes.CONVERTED_AS_REGULAR));
		
		try {
			return (NeedyCase) query.getSingleResult();
		} catch(NoResultException exception) {
			
			return null;
		}
	}

	public NeedyCase loadNeedyCaseByOldNumber(String oldNumber) {
		
		Query query = this.entityManager.createNamedQuery("loadNeedyCaseByOldNumber");
		query.setParameter("smartCardNumber", oldNumber.trim());
		query.setParameter("deletedNeedyCaseStatusValues", getNeedyCasesStatusValues(NeedyCaseStatusTypes.DELETED, NeedyCaseStatusTypes.CONVERTED_AS_REGULAR));
		
		try {
			return (NeedyCase) query.getSingleResult();
		} catch(NoResultException exception) {
			
			return null;
		}
	}
	
	public NeedyCase loadIrregularNeedyCaseForConverting(String nationalId) {
		
		Query query = this.entityManager.createNamedQuery("loadIrregularNeedyCaseForConverting");
		query.setParameter("nationalId", nationalId.trim());
		query.setParameter("deletedNeedyCaseStatusValues", getNeedyCasesStatusValues(NeedyCaseStatusTypes.DELETED, NeedyCaseStatusTypes.CONVERTED_AS_REGULAR));
		query.setParameter("irregularNeedyCaseTypeValue", ((short) 2));
		
		try {
			return (NeedyCase) query.getSingleResult();
		} catch(NoResultException exception) {
			
			return null;
		}
	}



	public NeedyCase loadNeedyCaseById(Long id) {
		
		Query query = this.entityManager.createNamedQuery("loadNeedyCaseById");
		query.setParameter("needyCaseId", id);
		
		try {
			return (NeedyCase) query.getSingleResult();
		} catch(NoResultException exception) {
			
			return null;
		}
	}



	public void updateNeedyCasePersonalPhoto(Long needyCaseId, byte[] photo) {
		
		Query query = this.entityManager.createQuery("update NeedyCase as r set r.personalPhoto=:p_photo where r.id =" + needyCaseId);
		query.setParameter("p_photo", photo);
		query.executeUpdate();
	}
	
	public void updateNeedyCaseAgentPersonalPhoto(Long needyCaseId, byte[] photo) {
		
		Query query = this.entityManager.createQuery("update NeedyCaseAgent as r set r.personalPhoto=:p_photo where r.needyCase.id =" + needyCaseId);
		query.setParameter("p_photo", photo);
		query.executeUpdate();
	}

	public void addNeedyCase(NeedyCase needyCase) {
		
		this.entityManager.persist(needyCase);
	}
	
	public void updateNeedyCase(NeedyCase needyCase) {
		
		this.entityManager.merge(needyCase);
	}



	public NeedyCase editNeedyCase(Long needyCaseId) {
		
		NeedyCase needyCase;
		Query query = this.entityManager.createNamedQuery("editNeedyCase");
		query.setParameter("needyCaseId", needyCaseId);
		
		try {
			
			needyCase = (NeedyCase) query.getSingleResult();
			
			return needyCase;
		} catch(NoResultException exception) {
			
			return null;
		}
	}
		
	@SuppressWarnings("unchecked")
	public List<NeedyCase> loadNeedyCases(NeedyCaseTypes needyCaseType, PaginationData paginationData, NeedyCaseStatusTypes... needyCaseStatusTypes) {
		
		Query query = this.entityManager.createNamedQuery("loadNeedyCasesByTypeAndStatus");
	
		query.setParameter("needyCaseType", needyCaseType.getType());
		query.setParameter("needyCaseStatusTypes", getNeedyCasesStatusValues(needyCaseStatusTypes));
		
		if ( paginationData != null ) {
			
			if ( paginationData.getStartIndex() >= 0 ) {
				
				query.setFirstResult( paginationData.getStartIndex() );
			}
			if ( paginationData.getPageSize() > 0 ) {
				
				query.setMaxResults(paginationData.getPageSize());
			}
		}
		
		return query.getResultList();
	}



	public int countNeedyCases(NeedyCaseTypes needyCaseType, NeedyCaseStatusTypes... needyCaseStatusTypes) {
		
		Query query;
		
		query = this.entityManager.createNamedQuery("countNeedyCases");
		
		query.setParameter("needyCaseType", needyCaseType.getType());
		query.setParameter("needyCaseStatusTypes", getNeedyCasesStatusValues(needyCaseStatusTypes));
		
		return ((Number) query.getSingleResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<NeedyCase> searchNeedyCases(NeedyCaseSearchCriteria searchCriteria) {
		
		StringBuffer query = new StringBuffer("SELECT needyCase FROM ");
		if ( searchCriteria.getType() == NeedyCaseTypes.REGULAR.getType() || 
				searchCriteria.getMaxMonthlyAmount() > 0 || searchCriteria.getMinMonthlyAmount() > 0) {
			
			query.append(" RegularCase ");
		} else if (  searchCriteria.getType() == NeedyCaseTypes.IRREGULAR.getType() ) {
			
			query.append(" IrregularCase ");
		} else {
			
			query.append(" NeedyCase ");
		}
		query.append(" AS needyCase WHERE 1=1 ");
		
		if ( !StringUtil.isEmpty(searchCriteria.getNationalId()) ) {
			query.append(" AND needyCase.nationalId LIKE '%" + searchCriteria.getNationalId().trim() + "%'");
		}
		if ( !StringUtil.isEmpty(searchCriteria.getOldCardNumber()) ) {
			query.append(" AND needyCase.smartCardNumber = '" + searchCriteria.getOldCardNumber().trim() + "'");
		}
		
		if ( !StringUtil.isEmpty(searchCriteria.getName()) ) {
			query.append(" AND needyCase.fullName LIKE '%" + searchCriteria.getName() + "%'");
		}
		if ( searchCriteria.getMinFamilyPersons() > 0 ) {
			
			query.append(" AND needyCase.familyPersons >= " + searchCriteria.getMinFamilyPersons());
		}
		if ( searchCriteria.getMaxFamilyPersons() > 0 ) {
			
			query.append(" AND needyCase.familyPersons <= " + searchCriteria.getMaxFamilyPersons());
		}
		
		if ( searchCriteria.getMinMonthlyAmount() > 0 ) {
			
			query.append(" AND needyCase.monthlyAmount >= " + searchCriteria.getMinMonthlyAmount());
		}
		if ( searchCriteria.getMaxMonthlyAmount() > 0 ) {
			
			query.append(" AND needyCase.monthlyAmount <= " + searchCriteria.getMaxMonthlyAmount());
		}
		if ( searchCriteria.getMaritalStatus() > 0 ) {
			
			query.append(" AND needyCase.maritalStatus = '" + searchCriteria.getMaritalStatus() + "'");
		}
		
		query.append(" order by needyCase.fullName");
		Query jpaQuery = this.entityManager.createQuery(query.toString());
		return jpaQuery.getResultList();
	}

	public int updateRegularNeedyCaseLastBenefitDate(RegularCase regularCase,
			Date lastBenefitDate) {
		
		Query query = this.entityManager.createQuery("update RegularCase as r set r.lastBenefitDate=:lastBenefitDate where r.id =" + regularCase.getId());
		query.setParameter("lastBenefitDate", lastBenefitDate);
		return query.executeUpdate();
	}

	public void updateNeedCaseStatus(Long needyCaseId, NeedyCaseStatusTypes newStatus) {
		
		Query query = this.entityManager.createQuery("update NeedyCase as r set r.status=:deleteStatus where r.id =" + needyCaseId);
		query.setParameter("deleteStatus", newStatus.getValue());
		query.executeUpdate();
	}
	
	public void deleteNeedyCaseAgent(Long needyCaseId) {
		
		Query query = this.entityManager.createQuery("DELETE NeedyCaseAgent where needyCase.id = " + needyCaseId);
		query.executeUpdate();
	}

	private List<Short> getNeedyCasesStatusValues(NeedyCaseStatusTypes...needyCaseStatusTypes) {
		
		List<Short> needyCaseStatusTypeValues = new ArrayList<Short>();
		for(NeedyCaseStatusTypes statusType: needyCaseStatusTypes) {
			
			needyCaseStatusTypeValues.add( statusType.getValue() );
		}
		
		return needyCaseStatusTypeValues;
	}

	
}
