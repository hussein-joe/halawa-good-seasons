package com.halawa.goodseasons.service.impl;

import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.CampaignActionStatusType;
import com.halawa.goodseasons.common.constants.NeedyCaseErrorCodes;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.dao.ReportingDao;
import com.halawa.goodseasons.dao.model.CampaignItemTotal;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.service.ReportingService;
import com.halawa.goodseasons.service.model.CampaignActionsUserReportCriteria;
import com.halawa.goodseasons.service.model.CampaignActionsUserReportResult;
import com.halawa.goodseasons.service.model.NeedyCaseBenefitsReportCriteria;
import com.halawa.goodseasons.service.model.NeedyCaseBenefitsReportResult;
import com.halawa.goodseasons.service.model.CampaignActionsUserReportResult.CampaignActionsUserReportResultItem;
import com.halawa.goodseasons.service.model.CampaignActionsUserReportResult.TotalCampaignItem;

public class ReportingServiceImpl implements ReportingService {

	private static final HalawaLogger logger = HalawaLogger.getInstance(ReportingServiceImpl.class);

	private ReportingDao reportingDao;
	
	public CampaignActionsUserReportResult generateUserCampaignActionsReport(
			CampaignActionsUserReportCriteria criteria)
			throws HalawaBusinessException {
	
		logger.info("Generating the user campaign actions report");
		
		List<Campaign> campaigns = null;
		CampaignActionsUserReportResult result;
		if ( criteria == null || criteria.getSelectedUser() == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, 
					"The passed criteria is null or the user is null");
		}
		
		logger.info("The report will be generated for user with id " + 
				criteria.getSelectedUser().getId());
		
		if ( criteria.getSelectedCampaigns() == null || criteria.getSelectedCampaigns().isEmpty() ) {
			
			campaigns = this.reportingDao.getUserCampaignsInPeriod(criteria.getSelectedUser(), criteria.getNumberOfDays());
		} else {
			
			campaigns = this.reportingDao.getUserCampaignsInPeriod(criteria.getSelectedCampaigns(), criteria.getSelectedUser(), criteria.getNumberOfDays());
		}
		result = new CampaignActionsUserReportResult();
		if ( campaigns == null || campaigns.isEmpty() ) {
			
			logger.info("No campaigns returned");
			return result;
		}
		for(Campaign campaign: campaigns) {
			
			CampaignActionsUserReportResultItem campaignActionsUserReportResultItem = calculateCampaignItemsTotals(campaign, criteria);
			result.addCampaignActionsUserReportResultItem(campaignActionsUserReportResultItem);
		}
		
		return result;
	}
	
	public NeedyCaseBenefitsReportResult generateNeedyCaseBenefitReport(NeedyCaseBenefitsReportCriteria criteria)
			throws HalawaBusinessException {
		
		List<NeedyCase> needyCasesList;
		NeedyCaseBenefitsReportResult reportResult;
		if ( criteria == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		logger.info("Generating needy case benefits report where national id = " + criteria.getNationalId());
		needyCasesList = this.reportingDao.loadNeedyCasesWithNationalId(criteria.getNationalId());
		if ( needyCasesList == null || needyCasesList.isEmpty() ) {
			
			logger.error("The national id " + criteria.getNationalId() + ", is not exist");
			throw new HalawaBusinessException(NeedyCaseErrorCodes.NEEDYCASE_IS_NOT_EXIST.getErrorCode());
		}
		
		if ( needyCasesList.size() > 2 ) {
			
			logger.fatal("The national id " + criteria.getNationalId() + " exist in database " + needyCasesList.size() +
					" times which is not expected");
			throw new HalawaBusinessException(HalawaErrorCode.GENERIC_ERROR);
		}
		
		reportResult = new NeedyCaseBenefitsReportResult();
		reportResult.setNeedyCase( needyCasesList.get(0) );
		logger.info("Loading the needy cases campaign actions");
		reportResult.setNeedyCaseBenefits( this.reportingDao.loadNeedyCasesCampaignActions(needyCasesList) );
		
		return reportResult;
	}



	private CampaignActionsUserReportResultItem calculateCampaignItemsTotals(Campaign campaign, 
			CampaignActionsUserReportCriteria criteria) throws HalawaBusinessException {
	
		CampaignActionsUserReportResultItem campaignActionsUserReportResultItem = new CampaignActionsUserReportResultItem();
		List<CampaignItemTotal> campaignItemsTotals;
		
		campaignActionsUserReportResultItem.setCampaign(campaign);
		campaignActionsUserReportResultItem.setNumberOfCases(this.reportingDao.countUserCampaignActions(criteria.getSelectedUser(), 
				campaign, criteria.getNumberOfDays(), 
				CampaignActionStatusType.DELIVERED_TO_NEEDYCASE, CampaignActionStatusType.DELIVERED_AFTER_PENDING_FROM_ANOTHER_CAMPAIGN));
		campaignItemsTotals = this.reportingDao.getUserCampaignActionItems(criteria.getSelectedUser(), campaign, criteria.getNumberOfDays(), 
				CampaignActionStatusType.DELIVERED_TO_NEEDYCASE, CampaignActionStatusType.DELIVERED_AFTER_PENDING_FROM_ANOTHER_CAMPAIGN);
		for(CampaignItemTotal itemTotal: campaignItemsTotals) {
			
			TotalCampaignItem totalCampaignItem = new TotalCampaignItem();
			totalCampaignItem.setCampaignItemType(itemTotal.getItemType());
			totalCampaignItem.setTotalQuantity( itemTotal.getTotalQuantity() );
			
			campaignActionsUserReportResultItem.addTotalCampaignItem(totalCampaignItem);
		}
		
		return campaignActionsUserReportResultItem;
	}

	public void setReportingDao(ReportingDao reportingDao) {
		this.reportingDao = reportingDao;
	}
	
	
}
