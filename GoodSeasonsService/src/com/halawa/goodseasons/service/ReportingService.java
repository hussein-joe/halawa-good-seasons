package com.halawa.goodseasons.service;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.goodseasons.service.model.CampaignActionsUserReportCriteria;
import com.halawa.goodseasons.service.model.CampaignActionsUserReportResult;
import com.halawa.goodseasons.service.model.NeedyCaseBenefitsReportCriteria;
import com.halawa.goodseasons.service.model.NeedyCaseBenefitsReportResult;

public interface ReportingService {

	CampaignActionsUserReportResult generateUserCampaignActionsReport(
			CampaignActionsUserReportCriteria criteria) throws HalawaBusinessException;
	
	NeedyCaseBenefitsReportResult generateNeedyCaseBenefitReport(NeedyCaseBenefitsReportCriteria criteria) throws HalawaBusinessException;
	
}
