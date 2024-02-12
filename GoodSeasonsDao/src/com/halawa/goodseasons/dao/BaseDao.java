package com.halawa.goodseasons.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.halawa.goodseasons.common.constants.NeedyCaseTypes;

public abstract class BaseDao {

	@PersistenceContext
	protected EntityManager entityManager;
	
	protected List<Short> getNeedyCaseTypeValues(NeedyCaseTypes...caseTypes) {
		
		List<Short> typeValues = new ArrayList<Short>();
		for(NeedyCaseTypes caseType: caseTypes) {
			
			typeValues.add( caseType.getType() );
		}
		
		return typeValues;
	}
}
