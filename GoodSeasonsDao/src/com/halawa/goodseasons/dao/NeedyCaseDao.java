package com.halawa.goodseasons.dao;

import java.util.Date;
import java.util.List;

import com.halawa.goodseasons.common.constants.NeedyCaseStatusTypes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.model.NeedyCaseSearchCriteria;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.RegularCase;

public interface NeedyCaseDao {

	NeedyCase loadNeedyCaseByNationalId(String nationalId);
	NeedyCase loadNeedyCaseByOldNumber(String oldNumber);
	
	NeedyCase loadIrregularNeedyCaseForConverting(String nationalId);
	NeedyCase loadNeedyCaseById(Long id);
	void addNeedyCase(NeedyCase needyCase);
	void updateNeedyCase(NeedyCase needyCase);
	NeedyCase editNeedyCase(Long needyCaseId);
	List<NeedyCase> loadNeedyCases(NeedyCaseTypes needyCaseType, PaginationData paginationData, NeedyCaseStatusTypes... needyCaseStatusTypes);
	int countNeedyCases(NeedyCaseTypes needyCaseType, NeedyCaseStatusTypes... needyCaseStatusTypes);
	List<NeedyCase> searchNeedyCases(NeedyCaseSearchCriteria searchCriteria);
	int updateRegularNeedyCaseLastBenefitDate(RegularCase regularCase, Date lastBenefitDate);
	void updateNeedCaseStatus(Long needyCaseId, NeedyCaseStatusTypes newStatus);
	void updateNeedyCasePersonalPhoto(Long needyCaseId, byte[] photo);
	void updateNeedyCaseAgentPersonalPhoto(Long needyCaseId, byte[] photo);
	void deleteNeedyCaseAgent(Long needyCaseId);
}
