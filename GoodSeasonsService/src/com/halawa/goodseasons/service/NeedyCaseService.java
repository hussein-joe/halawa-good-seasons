package com.halawa.goodseasons.service;

import java.io.File;
import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.goodseasons.common.constants.NeedyCaseStatusTypes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.dao.PaginationData;
import com.halawa.goodseasons.model.NeedyCaseSearchCriteria;
import com.halawa.goodseasons.model.entity.IrregularCase;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.RegularCase;
import com.halawa.goodseasons.service.model.NeedyCaseBenefit;

public interface NeedyCaseService {

	NeedyCase loadNeedyCaseByNationalId(String nationalId) throws HalawaBusinessException;
	public NeedyCase loadNeedyCaseByOldNumber(String oldNumber) throws HalawaBusinessException;
	NeedyCase loadNeedyCaseById(Long id) throws HalawaBusinessException;
	NeedyCaseBenefit loadNeedyCaseForBenefitDelivery(String nationalId) throws HalawaBusinessException;
	NeedyCaseBenefit loadNeedyCaseForBenefitDeliveryByOldNumber(String oldNumber) throws HalawaBusinessException;
	
	NeedyCaseBenefit loadNeedyCaseForExceptionalBenefitDelivery(String nationalId) throws HalawaBusinessException;
	NeedyCaseBenefit loadNeedyCaseForExceptionalBenefitDeliveryByOldNumber(String oldNumber) throws HalawaBusinessException;
	void addRegularNeedyCase(RegularCase regularCase) throws HalawaBusinessException;
	void addIrregularNeedyCase(IrregularCase regularCase) throws HalawaBusinessException;
	RegularCase editRegularNeedyCase(Long needyCaseId) throws HalawaBusinessException;
	IrregularCase editIrregularNeedyCase(Long needyCaseId) throws HalawaBusinessException;
	List<NeedyCase> loadNeedyCases(NeedyCaseTypes needyCaseType, PaginationData paginationData, NeedyCaseStatusTypes...needyCaseStatusTypes);
	int countNeedyCases(NeedyCaseTypes needyCaseType, NeedyCaseStatusTypes...caseStatusTypes);
	void updateNeedyCase(NeedyCase needyCase) throws HalawaBusinessException;
	void convertIrregularCaseAsRegular(String nationalId, RegularCase regularCase) throws HalawaBusinessException;
	List<NeedyCase> searchNeedyCases(NeedyCaseSearchCriteria searchCriteria) throws HalawaBusinessException;
	void exportNeedyCases(File destFile, NeedyCaseTypes needyCaseType, NeedyCaseStatusTypes...needyCaseStatusTypes) throws HalawaBusinessException;
	
	void updateNeedyCasePersonalPhoto(Long needyCaseId, byte[] photo) throws HalawaBusinessException;
	void updateNeedyCaseAgentPersonalPhoto(Long needyCaseId, byte[] photo) throws HalawaBusinessException;
	byte[] loadNeedyCasePersonalPhoto(Long needyCaseId) throws HalawaBusinessException;
	byte[] loadNeedyCaseAgentPersonalPhoto(Long needyCaseId) throws HalawaBusinessException;
	void updateNeedyCaseStatus(Long needyCaseId, NeedyCaseStatusTypes newStatus) throws HalawaBusinessException;
}
