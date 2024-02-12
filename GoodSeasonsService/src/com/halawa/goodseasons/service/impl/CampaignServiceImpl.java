package com.halawa.goodseasons.service.impl;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.arjuna.ats.internal.arjuna.template.HashList;
import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.CampaignActionStatusType;
import com.halawa.goodseasons.common.constants.CampaignErrorCodes;
import com.halawa.goodseasons.common.constants.CampaignStatusType;
import com.halawa.goodseasons.common.constants.CampaignType;
import com.halawa.goodseasons.common.constants.NeedyCaseStatusTypes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.dao.CampaignDao;
import com.halawa.goodseasons.dao.NeedyCaseDao;
import com.halawa.goodseasons.dao.PaginationData;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignAction;
import com.halawa.goodseasons.model.entity.CampaignActionItem;
import com.halawa.goodseasons.model.entity.CampaignItem;
import com.halawa.goodseasons.model.entity.CampaignItemType;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.RegularCase;
import com.halawa.goodseasons.model.entity.User;
import com.halawa.goodseasons.service.CampaignService;
import com.halawa.goodseasons.service.ConfigurationService;
import com.halawa.goodseasons.service.model.CampaignFinalReport;
import com.halawa.goodseasons.service.model.CampaignFinalReportItem;
import com.halawa.goodseasons.service.model.PendingCampaignAction;
import com.halawa.goodseasons.service.model.PendingNeedyCaseBenefit;
import com.halawa.goodseasons.service.model.PendingCampaignAction.PendingCampaignActionItem;

public class CampaignServiceImpl implements CampaignService {

	private static final HalawaLogger logger = HalawaLogger.getInstance(CampaignServiceImpl.class);
	private CampaignDao campaignDao;
	private NeedyCaseDao needyCaseDao;
	private ConfigurationService configurationService;
	
	public List<CampaignItemType> loadCampaignItemTypes(NeedyCaseTypes... caseTypes) {
		
		if ( caseTypes == null ) {
			
			logger.error("The passed needy case types are null");
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		return this.campaignDao.loadCampaignItemTypes(caseTypes);
	}
	
	public Campaign loadActiveCampaign(NeedyCaseTypes caseType) throws HalawaBusinessException {
		
		return this.loadActiveCampaign(CampaignType.REGULAR_CAMPAIGN, caseType);
	}
	
	public Campaign loadActiveCampaign(CampaignType campaignType, NeedyCaseTypes needyCaseType) throws HalawaBusinessException {
		
		
		List<Campaign> activeCampaigns = this.campaignDao.loadActiveCampiagns(campaignType, needyCaseType);
		
		if ( activeCampaigns == null || activeCampaigns.isEmpty() ) {
			
			logger.error("No active campaign exist for passed needy case " + needyCaseType);
			throw new HalawaBusinessException(CampaignErrorCodes.NO_ACTIVE_CAMPAIGN_DEFINED.getErrorCode());
		}
		
		if ( activeCampaigns.size() > 1 ) {
			
			logger.fatal("More than one active campaign defined for needy case of type " + needyCaseType);
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, 
					"More than one active campaign defined for needy case of type " + needyCaseType );
		}
		
		return activeCampaigns.get(0);
	}

	public CampaignAction loadCampaignAction(Campaign campaign, NeedyCase needyCase) {
		
		if ( campaign == null || needyCase == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		
		
		return this.campaignDao.loadNeedyCaseCampaignAction(campaign, needyCase);
	}
	
	public CampaignAction loadCampaignActionFetchItemsById(Long campaignActionId) {
		
		CampaignAction campaignAction = this.campaignDao.loadCampaignActionFetchItemsById(campaignActionId);
		if ( campaignAction == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, "The passed campaign action id " + campaignActionId + 
					", is not exist in the database");
		}
		return campaignAction;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateCampaign(Campaign campaign) throws HalawaBusinessException {
		
		logger.info("Updating campaign");
		
		if ( campaign == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		if (!this.checkCampaignBeforeAdd(campaign)) {
			
			logger.error("Can't add a campaign with duplicate item types");
			throw new HalawaBusinessException(CampaignErrorCodes.ADD_CAMPAIGN_DUPLICATE_ITEM_TYPES.getErrorCode());
		}
		this.campaignDao.updateCampaign(campaign);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void addRegullarCampaign(Campaign campaign) throws HalawaBusinessException {
		
		logger.info("Adding new regular campaign");
		
		if ( campaign == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		if (!this.checkCampaignBeforeAdd(campaign)) {
			
			logger.error("Can't add a campaign with duplicate item types");
			throw new HalawaBusinessException(CampaignErrorCodes.ADD_CAMPAIGN_DUPLICATE_ITEM_TYPES.getErrorCode());
		}

		List<Campaign> oldActiveCampaigns = this.campaignDao.loadActiveCampiagns(NeedyCaseTypes.REGULAR);
		if ( oldActiveCampaigns != null && !oldActiveCampaigns.isEmpty() ) {
			
			logger.error("Can't add new campaign while there are active campaigns not closed");
			throw new HalawaBusinessException(CampaignErrorCodes.ADD_CAMPAIGN_ACTIVE_CAMPAIGNS_EXISTS.getErrorCode());
		}
		if ( campaign.getCampaignItems() == null || campaign.getCampaignItems().isEmpty() ) {
			
			logger.error("The passed campaign has no items");
			throw new HalawaBusinessException(CampaignErrorCodes.CAMPAIGN_MUST_HAS_ITEMS.getErrorCode());
		}
		
		campaign.setCaseType( NeedyCaseTypes.REGULAR.getType() );
		addDefaultCampaignItems(campaign, NeedyCaseTypes.REGULAR);
		this.campaignDao.addCampaign(campaign);
		updateLastActiveCampaignDate(campaign);
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void addIrregullarCampaign(Campaign campaign) throws HalawaBusinessException {
		
		logger.info("Adding new irregular campaign");
		
		if ( campaign == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		if (!this.checkCampaignBeforeAdd(campaign)) {
			
			logger.error("Can't add a campaign with duplicate item types");
			throw new HalawaBusinessException(CampaignErrorCodes.ADD_CAMPAIGN_DUPLICATE_ITEM_TYPES.getErrorCode());
		}
		List<Campaign> oldActiveCampaigns = this.campaignDao.loadActiveCampiagns(NeedyCaseTypes.IRREGULAR);
		if ( oldActiveCampaigns != null && !oldActiveCampaigns.isEmpty() ) {
			
			logger.error("Can't add new campaign while there are active campaigns not closed");
			throw new HalawaBusinessException(CampaignErrorCodes.ADD_CAMPAIGN_ACTIVE_CAMPAIGNS_EXISTS.getErrorCode());
		}
		if ( campaign.getCampaignItems() == null || campaign.getCampaignItems().isEmpty() ) {
			
			logger.error("The passed campaign has no items");
			throw new HalawaBusinessException(CampaignErrorCodes.CAMPAIGN_MUST_HAS_ITEMS.getErrorCode());
		}
		
		for(CampaignItem campaignItem: campaign.getCampaignItems()) {
			
			if ( campaignItem.getMaxQuantity() < campaignItem.getMinQuantity() ) {
				
				//TODO use meaningfull error code
				throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
			}
			campaignItem.setQuantity( campaignItem.getMinQuantity() );
		}
		
		campaign.setCaseType(NeedyCaseTypes.IRREGULAR.getType());
		addDefaultCampaignItems(campaign, NeedyCaseTypes.IRREGULAR);
		this.campaignDao.addCampaign(campaign);
	}
	
	public List<Campaign> loadActiveCampaigns() throws HalawaBusinessException {
	
		logger.info("Loading the active campaigns");
		return this.campaignDao.loadActiveCampiagns();
	}

	public List<Campaign> loadOldCampaigns() {
		
		logger.info("Loading the closed campaigns");
		return this.campaignDao.loadClosedCampaigns(null, NeedyCaseTypes.REGULAR, NeedyCaseTypes.IRREGULAR, NeedyCaseTypes.BOTH);
	}
	
	public Date loadLastRegularCampaignMonth() {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("M-yyyy");
		String lastCampaignMonth;
		
		try {
			
			lastCampaignMonth = this.configurationService.getProperty(PARAMS_LAST_ACTIVE_CAMPAIGN_MONTH);
			if ( lastCampaignMonth == null || lastCampaignMonth.trim().equals("") ) {
				
				lastCampaignMonth = dateFormat.format(new Date());
			}
			return dateFormat.parse( lastCampaignMonth );
		} catch(Exception exception) {
		
			logger.fatal("Error occured while parsing the last active campaign month", exception);
			throw new HalawaSystemException(exception);
		}
		
	}
	
	public CampaignFinalReport generateCampaignFinalReportFaster(Long campaignId) throws HalawaBusinessException {
		
		Campaign campaign;
		Map<CampaignItemType, Double> campaignItemsMap;
		CampaignFinalReport campaignFinalReport = null;
		List<CampaignFinalReportItem> finalReportItems = new ArrayList<CampaignFinalReportItem>();
		
		
		logger.info("Generating the final campaign report");
		campaign = this.campaignDao.loadCampaignWithItemsById(campaignId);
		if ( campaign == null ) {
			
			logger.fatal("BUG: The passed campaign id " + campaignId + " is not exist in the database");
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, "The passed campaign id is not exist it is a BUG");
		}
		
		campaignFinalReport = new CampaignFinalReport();
		campaignFinalReport.setCampaign(campaign);
		
		campaignItemsMap = this.campaignDao.countCampaignActionsWithItems(campaign, 
				CampaignActionStatusType.DELIVERED_TO_NEEDYCASE, CampaignActionStatusType.DELIVERED_AFTER_PENDING_FROM_ANOTHER_CAMPAIGN);
		for(Map.Entry<CampaignItemType, Double> item: campaignItemsMap.entrySet()) {
			finalReportItems.add(new CampaignFinalReportItem(item.getKey(), item.getValue().doubleValue()));
		}
		campaignFinalReport.setCampaignFinalReportItems(finalReportItems);
		campaignFinalReport.setCountCampaignBenefits(campaignDao.countCampaignActions(campaign,
				CampaignActionStatusType.DELIVERED_TO_NEEDYCASE, CampaignActionStatusType.DELIVERED_AFTER_PENDING_FROM_ANOTHER_CAMPAIGN
		));
		if ( campaign.getCaseType() == NeedyCaseTypes.REGULAR.getType() ) {
			
			logger.debug("The needy case is regular, so we will count the delivered campaign actions");
			int totalRegularNeedyCases = this.needyCaseDao.countNeedyCases(NeedyCaseTypes.REGULAR, NeedyCaseStatusTypes.ACTIVE);	
			campaignFinalReport.setCountRemaingingCampaignBenefits(totalRegularNeedyCases - campaignFinalReport.getCountCampaignBenefits());
		}
		
		return campaignFinalReport;
	}

	public CampaignFinalReport generateCampaignFinalReport(Long campaignId) throws HalawaBusinessException {
		
		Campaign campaign;
		List<CampaignAction> campaignActions;
		CampaignFinalReport campaignFinalReport = null;
		Map<Long, CampaignFinalReportItem> campaignFinalReportItemsMap = new HashMap<Long, CampaignFinalReportItem>();
		PaginationData paginationData;
		
		logger.info("Generating the final campaign report");
		campaign = this.campaignDao.loadCampaignWithItemsById(campaignId);
		if ( campaign == null ) {
			
			logger.fatal("BUG: The passed campaign id " + campaignId + " is not exist in the database");
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, "The passed campaign id is not exist it is a BUG");
		}
		
		paginationData = new PaginationData(0, 2);
		campaignFinalReport = new CampaignFinalReport();
		campaignFinalReport.setCampaign(campaign);
		
		
		while ( true ) {
			
			campaignActions = this.campaignDao.loadCampaignActionsWithItems(campaign, paginationData, 
					CampaignActionStatusType.DELIVERED_TO_NEEDYCASE, CampaignActionStatusType.DELIVERED_AFTER_PENDING_FROM_ANOTHER_CAMPAIGN);
			if ( campaignActions == null || campaignActions.isEmpty() ) {
				
				break;
			}
			for(CampaignAction campaignAction: campaignActions) {
				
				for(CampaignActionItem campaignActionItem:campaignAction.getCampaignActionItems()) {
					
					double calculatedValue;
					calculatedValue = campaignActionItem.getQuantity();
					
					CampaignFinalReportItem campaignFinalReportItem = campaignFinalReportItemsMap.get( Long.valueOf(campaignActionItem.getCampaignItemType().getId()));
					if ( campaignFinalReportItem == null ) {
						
						campaignFinalReportItem = new CampaignFinalReportItem(campaignActionItem.getCampaignItemType(), 0);
						campaignFinalReportItemsMap.put( Long.valueOf(campaignActionItem.getCampaignItemType().getId()), campaignFinalReportItem);
					}
					
					campaignFinalReportItem.setQuantity(campaignFinalReportItem.getQuantity() + calculatedValue);
				}
			}
			campaignFinalReport.setCountCampaignBenefits( campaignFinalReport.getCountCampaignBenefits() + campaignActions.size() );
			paginationData.setStartIndex( paginationData.getStartIndex() + paginationData.getPageSize() );
		}
		
		campaignFinalReport.addCampaignFinalReportItems( campaignFinalReportItemsMap.values() );
		
		if ( campaign.getCaseType() == NeedyCaseTypes.REGULAR.getType() ) {
			
			logger.debug("The needy case is regular, so we will count the delivered campaign actions");
			int totalRegularNeedyCases = this.needyCaseDao.countNeedyCases(NeedyCaseTypes.REGULAR, NeedyCaseStatusTypes.ACTIVE);
			
			campaignFinalReport.setCountRemaingingCampaignBenefits(totalRegularNeedyCases - campaignFinalReport.getCountCampaignBenefits());
		}
		
		return campaignFinalReport;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void resetExceptionalCampaign(User actor, Campaign campaign) throws HalawaBusinessException {
		
		if ( campaign.getCampaignType() != CampaignType.EXCEPTIONAL_CAMPAIGN.getType() ) {
			
			logger.error("The campaign with id " + campaign.getId() + 
					" is not exceptional you can not reset it");
	
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		logger.info("Resetting the exceptional campaign");
		this.campaignDao.forgetExceptionalCampaignActions(actor, campaign);
		logger.info("The exceptional campaign resetted successfully");
	}
	
	public Campaign loadCampaignWithItemsById(long campaignId)throws HalawaBusinessException {
		
		return this.campaignDao.loadCampaignWithItemsById(campaignId);
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void closeCampaign(User actor, Campaign campaign) throws HalawaBusinessException {
		
		Campaign reloadedCampaign;
		
		reloadedCampaign = this.campaignDao.loadCampaignWithItemsById(campaign.getId());
		if ( reloadedCampaign.getStatus() != CampaignStatusType.ACTIVE.getValue() ) {
			
			logger.error("The status of campaign with id " + reloadedCampaign.getId() + " = " + reloadedCampaign.getStatus() + 
					" which is not active campaign so you can't close it");
			
			throw new HalawaBusinessException(CampaignErrorCodes.CAMPAIGN_ALREADY_CLOSED.getErrorCode());
		}
		reloadedCampaign.setStatus((short) CampaignStatusType.CLOSED.getValue());
		reloadedCampaign.setEndDate(new Date());
		if ( reloadedCampaign.getCaseType() == NeedyCaseTypes.REGULAR.getType() ) {
			
			logger.info("Adding pending actions for not included regular needy cases in campaign with id " + 
					reloadedCampaign.getId());
			//addPendingCampaignActions(actor, reloadedCampaign);
			addPendingCampaignActionsFaster(actor, reloadedCampaign);
		}
		this.campaignDao.updateCampaign(reloadedCampaign);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void addRegularCampaignBenefit(User actor, String needyCaseNationalId) throws HalawaBusinessException {
		
		NeedyCase reloadedNeedyCase;
		Campaign activeCampaign;
		List<Campaign> activeCampaignsList;
		List<CampaignActionItem> campaignActionItems;
		int monthlyCampaignItemTypeId;
		
		if ( actor == null || StringUtil.isEmpty(needyCaseNationalId) ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER); 
		}
		
		reloadedNeedyCase = this.needyCaseDao.loadNeedyCaseByNationalId(needyCaseNationalId);
		if ( reloadedNeedyCase == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		if ( reloadedNeedyCase instanceof RegularCase ) {
			
			activeCampaignsList = this.campaignDao.loadActiveCampiagns(NeedyCaseTypes.REGULAR);
		} else {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, "BUG: You are calling addRegularCampaignBenefit for irregular needy case");
			//activeCampaignsList = this.campaignDao.loadActiveCampiagns(NeedyCaseTypes.IRREGULAR);
		}
		
		if ( activeCampaignsList == null || activeCampaignsList.isEmpty() ) {
			
			logger.error("The needy case with id " + reloadedNeedyCase.getId() + " is of type " + 
					reloadedNeedyCase.getType() + ", and system has no active campaign of this type");
			throw new HalawaBusinessException(CampaignErrorCodes.CAMPAIGN_BENEFITS_ADD_NO_ACTIVE_CAMPAIGN_EXIST.getErrorCode());
		}
		activeCampaign = activeCampaignsList.get(0);
		activeCampaign = this.campaignDao.loadCampaignWithItemsById(activeCampaign.getId());
		
		campaignActionItems = new ArrayList<CampaignActionItem>();
		for(CampaignItem campaignItem: activeCampaign.getCampaignItems()) {
			
			CampaignActionItem campaignActionItem = new CampaignActionItem();
			campaignActionItem.setCampaignItemType(campaignItem.getCampaignItemType());
			campaignActionItem.setQuantity( calculateCampaignItemQuantity(campaignItem.getQuantity(), campaignItem.getCampaignItemType(), reloadedNeedyCase ) );
			
			campaignActionItems.add(campaignActionItem);
		}
		
		this.addCampaignAction(actor, reloadedNeedyCase, activeCampaign, campaignActionItems);
		
		monthlyCampaignItemTypeId = Integer.parseInt(this.configurationService.getProperty(CONFIG_CAMPAIGN_ITEMTYPE_MONTHLY_ITEM_ID));
		for(CampaignActionItem actionItem: campaignActionItems) {
			
			if ( actionItem.getCampaignItemType().getId() == monthlyCampaignItemTypeId ) {
				
				RegularCase regularCaseCasted = (RegularCase) reloadedNeedyCase;
				this.addMonthsToLastNeedycaseMonthBenefit((RegularCase) reloadedNeedyCase, 
						Double.valueOf(actionItem.getQuantity()/regularCaseCasted.getMonthlyAmount()).intValue() );
			}
		}
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void addRegularCampaignBenefit(User actor, String nationalId, List<CampaignActionItem> campaignActionItems) throws HalawaBusinessException {
		
		NeedyCase reloadedNeedyCase;
		Campaign activeCampaign;
		List<Campaign> activeCampaignsList;
		List<CampaignActionItem> filteredCampaignActionItems = new ArrayList<CampaignActionItem>();
		
		if ( actor == null || StringUtil.isEmpty(nationalId) ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER); 
		}
		
		reloadedNeedyCase = this.needyCaseDao.loadNeedyCaseByNationalId(nationalId);
		if ( reloadedNeedyCase == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		if ( reloadedNeedyCase instanceof RegularCase ) {
			
			activeCampaignsList = this.campaignDao.loadActiveCampiagns(NeedyCaseTypes.REGULAR);
		} else {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, "BUG: You are calling addRegularCampaignBenefit for irregular needy case");
			//activeCampaignsList = this.campaignDao.loadActiveCampiagns(NeedyCaseTypes.IRREGULAR);
		}
		
		if ( activeCampaignsList == null || activeCampaignsList.isEmpty() ) {
			
			logger.error("The needy case with id " + reloadedNeedyCase.getId() + " is of type " + 
					reloadedNeedyCase.getType() + ", and system has no active campaign of this type");
			throw new HalawaBusinessException(CampaignErrorCodes.CAMPAIGN_BENEFITS_ADD_NO_ACTIVE_CAMPAIGN_EXIST.getErrorCode());
		}
		activeCampaign = activeCampaignsList.get(0);
		for(CampaignActionItem item: campaignActionItems) {
			
			if ( item.getQuantity() != 0 ) {
				
				filteredCampaignActionItems.add(item);
			}
		}
		this.addCampaignAction(actor, reloadedNeedyCase, activeCampaign, filteredCampaignActionItems);
		
		int monthlyCampaignItemTypeId = Integer.parseInt(this.configurationService.getProperty(CONFIG_CAMPAIGN_ITEMTYPE_MONTHLY_ITEM_ID));
		for(CampaignActionItem actionItem: filteredCampaignActionItems) {
			
			if ( actionItem.getCampaignItemType().getId() == monthlyCampaignItemTypeId ) {
				
				RegularCase regularCaseCasted = (RegularCase) reloadedNeedyCase;
				this.addMonthsToLastNeedycaseMonthBenefit((RegularCase) reloadedNeedyCase, 
						Double.valueOf(actionItem.getQuantity()/regularCaseCasted.getMonthlyAmount()).intValue() );
			}
		}
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void addIrregularCampaignBenefit(User actor, String needyCaseNationalId, List<CampaignActionItem> campaignActionItems) throws HalawaBusinessException {
		
		NeedyCase reloadedNeedyCase;
		Campaign activeCampaign;
		List<Campaign> activeCampaignsList;
		if ( actor == null || StringUtil.isEmpty(needyCaseNationalId) ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER); 
		}
		
		reloadedNeedyCase = this.needyCaseDao.loadNeedyCaseByNationalId(needyCaseNationalId);
		if ( reloadedNeedyCase == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		if ( reloadedNeedyCase instanceof RegularCase ) {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, "BUG: You are calling addIrregularCampaignBenefit for regular needy case");
			//activeCampaignsList = this.campaignDao.loadActiveCampiagns(NeedyCaseTypes.REGULAR);
		} else {
			
			
			activeCampaignsList = this.campaignDao.loadActiveCampiagns(NeedyCaseTypes.IRREGULAR);
		}
		
		if ( activeCampaignsList == null || activeCampaignsList.isEmpty() ) {
			
			logger.error("The needy case with id " + reloadedNeedyCase.getId() + " is of type " + 
					reloadedNeedyCase.getType() + ", and system has no active campaign of this type");
			throw new HalawaBusinessException(CampaignErrorCodes.CAMPAIGN_BENEFITS_ADD_NO_ACTIVE_CAMPAIGN_EXIST.getErrorCode());
		}
		
		activeCampaign = activeCampaignsList.get(0);
		/*
		activeCampaign = campaignDao.loadCampaignWithItemsById(activeCampaign.getId());
		for( CampaignActionItem item: campaignActionItems ) {
			
			for(CampaignItem campaignItem: activeCampaign.getCampaignItems()) {
				
				if ( item.getId().equals( campaignItem.getId() ) ) {
					
					if ( item.getQuantity() > campaignItem.getMaxQuantity() ||  
							item.getQuantity() < campaignItem.getMinQuantity() ) {
						
						HalawaBusinessException exception = new HalawaBusinessException(CampaignErrorCodes.CAMPAIGN_BENEFITS_ADD_VALUE_NOT_IN_RANGE.getErrorCode());
						exception.setParameters(campaignItem.getCampaignItemType().getName());
						throw exception;
					}
				}
			}
		}
		*/
		this.addCampaignAction(actor, reloadedNeedyCase, activeCampaign, campaignActionItems);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void addExceptionalCampaignBenefit(User actor, String nationalId, List<CampaignActionItem> campaignActionItems, String note)
			throws HalawaBusinessException {
		
		NeedyCase reloadedNeedyCase;
		Campaign activeCampaign;
		List<Campaign> activeCampaignsList;
		List<CampaignActionItem> filteredCampaignActionItems = new ArrayList<CampaignActionItem>();
		
		if ( actor == null || StringUtil.isEmpty(nationalId) ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER); 
		}
		
		reloadedNeedyCase = this.needyCaseDao.loadNeedyCaseByNationalId(nationalId);
		if ( reloadedNeedyCase == null ) {
			
			logger.error("The passed national id " + nationalId + ", is not defined which is strange");
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		if ( reloadedNeedyCase instanceof RegularCase ) {
			
			activeCampaignsList = this.campaignDao.loadActiveCampiagns(CampaignType.EXCEPTIONAL_CAMPAIGN, NeedyCaseTypes.REGULAR);
		} else {
			
			activeCampaignsList = this.campaignDao.loadActiveCampiagns(CampaignType.EXCEPTIONAL_CAMPAIGN, NeedyCaseTypes.IRREGULAR);
		}
		
		if ( activeCampaignsList == null || activeCampaignsList.isEmpty() ) {
			
			logger.error("The needy case with id " + reloadedNeedyCase.getId() + " is of type " + 
					reloadedNeedyCase.getType() + ", and system has no active exceptional campaign of this type");
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, "The system has no active exceptional campaign for needy case witrh type " + 
					reloadedNeedyCase.getType());
		}
		activeCampaign = activeCampaignsList.get(0);
		for(CampaignActionItem item: campaignActionItems) {
			
			if ( item.getQuantity() != 0 ) {
				
				item.setQuantity( calculateCampaignItemQuantity(item.getQuantity(), item.getCampaignItemType(), reloadedNeedyCase) );
				filteredCampaignActionItems.add(item);
			}
		}
		this.addCampaignAction(actor, reloadedNeedyCase, activeCampaign, filteredCampaignActionItems, note);
		if ( reloadedNeedyCase instanceof RegularCase ) {
			
			int monthlyCampaignItemTypeId = Integer.parseInt(this.configurationService.getProperty(CONFIG_ADVANCED_MONTHS_CAMPAIGN_ITEM_ID));
			for(CampaignActionItem actionItem: campaignActionItems) {
				
				if ( actionItem.getCampaignItemType().getId() == monthlyCampaignItemTypeId ) {
					
					RegularCase regularCaseCasted = (RegularCase) reloadedNeedyCase;
					this.addMonthsToLastNeedycaseMonthBenefit((RegularCase) reloadedNeedyCase, 
							Double.valueOf(actionItem.getQuantity()/regularCaseCasted.getMonthlyAmount()).intValue() );
				}
			}
		}
	}
	
	public PendingNeedyCaseBenefit loadNeedyCasePendingBenefits(NeedyCase needyCase) throws HalawaBusinessException {
		
		List<CampaignAction> campaignActions;
		PendingNeedyCaseBenefit pendingNeedyCaseBenefit;
		List<PendingCampaignAction> pendingCampaignActions;
		
		logger.info("Loading the needy case pending campaign actions for needy case with national id " + needyCase.getNationalId());
		
		campaignActions = this.campaignDao.loadNeedyCaseActionsFetchCampaignAndActionItems(needyCase, CampaignActionStatusType.PENDING);
		if ( campaignActions == null || campaignActions.isEmpty() ) {
			
			return null;
		}
		
		pendingNeedyCaseBenefit = new PendingNeedyCaseBenefit();
		pendingCampaignActions = new ArrayList<PendingCampaignAction>();
		
		pendingNeedyCaseBenefit.setNeedyCase(needyCase);
		for(CampaignAction campaignAction: campaignActions) {
			
			PendingCampaignAction pendingCampaignAction = new PendingCampaignAction();
			pendingCampaignAction.setCampaign(campaignAction.getCampaign());
			pendingCampaignAction.addAllCampaignActionItems(campaignAction.getCampaignActionItems());
			
			pendingCampaignActions.add(pendingCampaignAction);
		}
		
		pendingNeedyCaseBenefit.setPendingCampaignActions(pendingCampaignActions);
		
		return pendingNeedyCaseBenefit;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void refuseAllPendingCampaignActions(NeedyCase needyCase) throws HalawaBusinessException {
		
		if ( needyCase == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		logger.info("Refusing all pending campaign actions for needy with id " + needyCase.getId());
		this.campaignDao.updateAllPendingCampaignActions(needyCase, CampaignActionStatusType.PENDING_REFUSED);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void deliverPendingCampaignBenefit(User actor, PendingNeedyCaseBenefit pendingNeedyCaseBenefit) throws HalawaBusinessException {
		
		Campaign currentActiveCampaign;
		boolean refuseCampaignAction = true;
		boolean acceptCampaignActions = false;
		List<Campaign> refusedCampaigns = new ArrayList<Campaign>();
		
		if ( pendingNeedyCaseBenefit == null || pendingNeedyCaseBenefit.getNeedyCase() == null || 
				pendingNeedyCaseBenefit.getPendingCampaignActions() == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		logger.info("Delivering the pending campaign benefit for needy case with id " + pendingNeedyCaseBenefit.getNeedyCase().getId());
		currentActiveCampaign = this.loadActiveCampaign(NeedyCaseTypes.getNeedyCaseType(pendingNeedyCaseBenefit.getNeedyCase().getType()));
		for(PendingCampaignAction campaignAction: pendingNeedyCaseBenefit.getPendingCampaignActions()) {
			
			refuseCampaignAction = true;
			List<CampaignActionItem> campaignActionItems = new ArrayList<CampaignActionItem>();
			for(PendingCampaignActionItem pendingCampaignActionItem: campaignAction.getPendingCampaignActionItems()) {
				
				if ( pendingCampaignActionItem.getValidQuantity() > 0 ) 
					refuseCampaignAction = false;
				CampaignActionItem newCampaignActionItem = new CampaignActionItem();
				BeanUtils.copyProperties(pendingCampaignActionItem.getCampaignActionItem(), newCampaignActionItem, 
						new String[]{"id", "campaignAction"});
				newCampaignActionItem.setQuantity(pendingCampaignActionItem.getValidQuantity());
				campaignActionItems.add(newCampaignActionItem);
			}
			if ( !refuseCampaignAction ) {
				
				logger.info("Adding campaign action items to current active campaign, where old campaign had id " + 
						campaignAction.getCampaign().getId());
				this.addCampaignAction(actor, pendingNeedyCaseBenefit.getNeedyCase(), currentActiveCampaign, campaignActionItems, CampaignActionStatusType.DELIVERED_AFTER_PENDING_FROM_ANOTHER_CAMPAIGN);
				acceptCampaignActions = true;
			} else {
				
				logger.info("Refuse the action for campaign with id " + campaignAction.getCampaign().getId());
				refusedCampaigns.add(campaignAction.getCampaign());
			}
		}
		if ( acceptCampaignActions ) {
		
			logger.info("Update the campaign action status for accepted campaign actions");
			this.campaignDao.updateAllPendingCampaignActions(pendingNeedyCaseBenefit.getNeedyCase(), CampaignActionStatusType.DELIVERED_TO_NEEDYCASE_AFTER_PENDING);
		}
		if ( !refusedCampaigns.isEmpty() ) {
			
			logger.info("Update the status of pending refused campaigns");
			this.campaignDao.updatePendingCampaignActionsForCampaigns(pendingNeedyCaseBenefit.getNeedyCase(), refusedCampaigns, 
					CampaignActionStatusType.PENDING_REFUSED);
		}
	}

	private void addCampaignAction(User actor, NeedyCase needyCase, Campaign activeCampaign, List<CampaignActionItem> campaignActionItems, 
			CampaignActionStatusType actionStatusType, String note) throws HalawaBusinessException {
		
		
		CampaignAction campaignAction = null;
		
		logger.info("Adding campaign action for needy case with national id " + needyCase.getNationalId() + ", and using campaign with id " + 
				activeCampaign.getId());
		
		campaignAction = new CampaignAction();
		campaignAction.setNeedyCase(needyCase);
		campaignAction.setUser(actor);
		campaignAction.setCampaign(activeCampaign);
		campaignAction.setNote(note);
		for(CampaignActionItem campaignActionItem: campaignActionItems) {
			
			campaignActionItem.setCampaignAction(campaignAction);
			campaignAction.getCampaignActionItems().add(campaignActionItem);
		}
		
		campaignAction.setStatus( actionStatusType.getValue() );
		this.campaignDao.addCampaignAction(campaignAction);
	}

	private void addCampaignAction(User actor, NeedyCase needyCase, Campaign activeCampaign, List<CampaignActionItem> campaignActionItems, 
			CampaignActionStatusType actionStatusType) throws HalawaBusinessException {
		
		
		CampaignAction campaignAction = null;
		
		logger.info("Adding campaign action for needy case with national id " + needyCase.getNationalId() + ", and using campaign with id " + 
				activeCampaign.getId());
		
		campaignAction = new CampaignAction();
		campaignAction.setNeedyCase(needyCase);
		campaignAction.setUser(actor);
		campaignAction.setCampaign(activeCampaign);
		for(CampaignActionItem campaignActionItem: campaignActionItems) {
			
			campaignActionItem.setCampaignAction(campaignAction);
			campaignAction.getCampaignActionItems().add(campaignActionItem);
		}
		
		campaignAction.setStatus( actionStatusType.getValue() );
		this.campaignDao.addCampaignAction(campaignAction);
	}

	private void addCampaignAction(User actor, NeedyCase needyCase, Campaign activeCampaign, List<CampaignActionItem> campaignActionItems, String note) throws HalawaBusinessException {
		this.addCampaignAction(actor, needyCase, activeCampaign, campaignActionItems, CampaignActionStatusType.DELIVERED_TO_NEEDYCASE, note);
	}
	private void addCampaignAction(User actor, NeedyCase needyCase, Campaign activeCampaign, List<CampaignActionItem> campaignActionItems) throws HalawaBusinessException {
		
		this.addCampaignAction(actor, needyCase, activeCampaign, campaignActionItems, CampaignActionStatusType.DELIVERED_TO_NEEDYCASE);
	}
	
	private double calculateCampaignItemQuantity(double quantity, CampaignItemType campaignItemType, NeedyCase needyCase) {
		
		double calculatedValue = 0;
		
		if ( campaignItemType.isMonthlyBased() && needyCase instanceof RegularCase) {
			
			calculatedValue = quantity * ((RegularCase) needyCase).getMonthlyAmount();
			calculatedValue = (calculatedValue * campaignItemType.getPercentage()) / 100;
		} else {
			
			calculatedValue = quantity;
		}
		
		return calculatedValue;
	}
	
	private double calculateCampaignItemQuantityFaster(double quantity, CampaignItemType campaignItemType, int monthlyAmount) {
		
		double calculatedValue = 0;
		
		if ( campaignItemType.isMonthlyBased() ) {
			
			calculatedValue = quantity * monthlyAmount;
			calculatedValue = (calculatedValue * campaignItemType.getPercentage()) / 100;
		} else {
			
			calculatedValue = quantity;
		}
		
		return calculatedValue;
	}

	private void updateLastActiveCampaignDate(Campaign campaign) throws HalawaBusinessException{
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("M-yyyy");
		Calendar calendar = Calendar.getInstance();
		String lastCampaignMonth;
		
		try {
			lastCampaignMonth = this.configurationService.getProperty(PARAMS_LAST_ACTIVE_CAMPAIGN_MONTH);
			if ( lastCampaignMonth == null || lastCampaignMonth.trim().equals("") ) {
				
				lastCampaignMonth = dateFormat.format(new Date());
			}
			
			calendar.setTime(dateFormat.parse( lastCampaignMonth ));
			for(CampaignItem item: campaign.getCampaignItems()) {
				
				if ( item.getCampaignItemType().getId() == 1 ) {
					
					calendar.add(Calendar.MONTH, Double.valueOf(item.getQuantity()).intValue() );
				}
			}
			this.configurationService.updateProperty(PARAMS_LAST_ACTIVE_CAMPAIGN_MONTH, dateFormat.format( calendar.getTime() ));
		} catch(Exception exception) {
			
			throw new HalawaSystemException(exception);
		}
	}

	private void addDefaultCampaignItems(Campaign campaign, NeedyCaseTypes needyCaseType) throws HalawaBusinessException {
		
		List<CampaignItemType> defaultCampaignItemTypes = this.campaignDao.loadDefaultCampaignItemTypes(needyCaseType);
		if ( defaultCampaignItemTypes == null || defaultCampaignItemTypes.isEmpty() ) {
			
			logger.debug("The campaign with name " + campaign.getName() + ", and type " + needyCaseType + ", has no default items");
			return;
		}
		for(CampaignItemType itemType: defaultCampaignItemTypes) {
			
			CampaignItem campaignItem = new CampaignItem();
			campaignItem.setCampaign(campaign);
			campaignItem.setMaxQuantity(0);
			campaignItem.setMinQuantity(0);
			campaignItem.setQuantity(0);
			campaignItem.setCampaignItemType(itemType);
			
			campaign.getCampaignItems().add(campaignItem);
		}
	}
	
	public void setCampaignDao(CampaignDao campaignDao) {
		this.campaignDao = campaignDao;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setNeedyCaseDao(NeedyCaseDao needyCaseDao) {
		this.needyCaseDao = needyCaseDao;
	}
	
	/**
	 * Add the passed months to the regular needy case last benefit month
	 * @param regularCase
	 * @param numberOfMonths
	 */
	private void addMonthsToLastNeedycaseMonthBenefit(RegularCase regularCase, int numberOfMonths) {
		
		logger.info("Add " + numberOfMonths + " to the last benefit date to regular case with id " + regularCase.getId());
		Calendar calendar = Calendar.getInstance();
		if ( regularCase.getLastBenefitDate() == null ) {
			
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		} else {
			
			calendar.setTime( regularCase.getLastBenefitDate() );
		}
		
		calendar.add(Calendar.MONTH, numberOfMonths);
		logger.info("The regular case with id " + regularCase.getId() + ", will be updated with date " + calendar.getTime());
		RegularCase reloadedRegularCase = (RegularCase) this.needyCaseDao.loadNeedyCaseById(regularCase.getId());
		if ( reloadedRegularCase == null ) {
			
			logger.fatal("The regular needy case with id " + regularCase.getId() + ", and national id " + regularCase.getNationalId() + 
				" is not exist in database, as we failed to update its last benefit date");
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		reloadedRegularCase.setLastBenefitDate(calendar.getTime());
		this.needyCaseDao.updateNeedyCase(reloadedRegularCase);
	}
	
	private void addMonthsToLastNeedycaseMonthBenefit(long needyCaseId, Date lastBenefitDate, int numberOfMonths) {
		
		logger.info("Add " + numberOfMonths + " to the last benefit date to regular case with id " + needyCaseId);
		Calendar calendar = Calendar.getInstance();
		if ( lastBenefitDate == null ) {
			
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		} else {
			
			calendar.setTime( lastBenefitDate );
		}
		
		calendar.add(Calendar.MONTH, numberOfMonths);
		logger.info("The regular case with id " + needyCaseId + ", will be updated with date " + calendar.getTime());
		
		this.campaignDao.updateRegularNeedyCaseLastBenefitDate(needyCaseId, calendar.getTime());
	}
	

	private void addPendingCampaignActionsFaster(User actor, Campaign campaign) throws HalawaBusinessException {
		
		List<Object[]> pendingNeedycasesData = campaignDao.loadCampaingPendingNeedyCasesIds(campaign);
		for(Object[] pendingNeedycaseData: pendingNeedycasesData) {
			Date lastBenefitDate = null;
			if (pendingNeedycaseData[2] != null) {
				
				lastBenefitDate = (Date) pendingNeedycaseData[2];
				
			}
			if ( pendingNeedycaseData[2] == null || new Date().after( lastBenefitDate ) ) {
				
				addPendingCampaignActionFaster(actor, campaign, 
						Long.parseLong( pendingNeedycaseData[0].toString() ), Integer.parseInt( pendingNeedycaseData[1].toString() ), 
						lastBenefitDate);
			} else {
				
				logger.info("The needy case with national id " + pendingNeedycaseData[0] + 
						" has last benefit date which = " + lastBenefitDate + 
						" which exceed the current active regular campaign, so no pending benefit will be added");
			}
		}
	}
	
	private void addPendingCampaignActions(User actor, Campaign campaign) throws HalawaBusinessException {
		
		PaginationData paginationData = new PaginationData(0, 100);
		List<NeedyCase> needyCasesList;
		List<BigInteger> needyCaseIds;
		
		logger.info("Adding pending campaign actions for  needy cases which didn't come to take its benefit, where the type of the campaign is " + 
				campaign.getCaseType());
		
		needyCaseIds = this.campaignDao.loadCampaignActionsNeedyCasesIds(campaign, CampaignActionStatusType.DELIVERED_TO_NEEDYCASE);
		while ( true ) {
			
			needyCasesList = this.needyCaseDao.loadNeedyCases(NeedyCaseTypes.getNeedyCaseType(campaign.getCaseType()), paginationData, 
					NeedyCaseStatusTypes.ACTIVE);
			
			if ( needyCasesList == null || needyCasesList.isEmpty() ) {
				
				break;
			}
			for(NeedyCase needyCase: needyCasesList) {
				
				if ( Collections.binarySearch(needyCaseIds, BigInteger.valueOf((needyCase.getId()))) < 0) {
					
					RegularCase castedRegularCase = (RegularCase) needyCase;
					if ( castedRegularCase.getLastBenefitDate() == null || new Date().after( castedRegularCase.getLastBenefitDate() ) ) {
						
						addPendingCampaignAction(actor, campaign, needyCase);
					} else {
						
						logger.info("The needy case with national id " + castedRegularCase.getNationalId() + 
								" has last benefit date which = " + castedRegularCase.getLastBenefitDate() + 
								" which exceed the current active regular campaign, so no pending benefit will be added");
					}
					
				}
			}
			paginationData.setStartIndex( paginationData.getStartIndex() + paginationData.getPageSize() );
		}
	}
	
	private void addPendingCampaignAction(User actor, Campaign campaign, NeedyCase needyCase) throws HalawaBusinessException {
		
		List<CampaignActionItem> pendingCampaignActionItems = new ArrayList<CampaignActionItem>();
		for(CampaignItem item: campaign.getCampaignItems()) {
			
			if ( item.getCampaignItemType().isAllowPending() ) {
				
				CampaignActionItem actionItem = new CampaignActionItem();
				actionItem.setCampaignItemType(item.getCampaignItemType());
				
				actionItem.setQuantity( calculateCampaignItemQuantity(item.getQuantity(), item.getCampaignItemType(), needyCase) );
				
				pendingCampaignActionItems.add(actionItem);
			}
		}
		this.addCampaignAction(actor, needyCase, campaign, pendingCampaignActionItems, CampaignActionStatusType.PENDING);
		
		
		int monthlyCampaignItemTypeId = Integer.parseInt(this.configurationService.getProperty(CONFIG_CAMPAIGN_ITEMTYPE_MONTHLY_ITEM_ID));
		for(CampaignActionItem actionItem: pendingCampaignActionItems) {
			
			if ( actionItem.getCampaignItemType().getId() == monthlyCampaignItemTypeId ) {
				
				RegularCase regularCaseCasted = (RegularCase) needyCase;
				this.addMonthsToLastNeedycaseMonthBenefit((RegularCase) needyCase, 
						Double.valueOf(actionItem.getQuantity()/regularCaseCasted.getMonthlyAmount()).intValue() );
			}
		}
	}
	
	private void addPendingCampaignActionFaster(User actor, Campaign campaign, Long needyCaseId, int monthlyAmount, Date lastBenefitDate) throws HalawaBusinessException {
		
		int monthlyCampaignItemTypeId = Integer.parseInt(this.configurationService.getProperty(CONFIG_CAMPAIGN_ITEMTYPE_MONTHLY_ITEM_ID));
		for(CampaignItem item: campaign.getCampaignItems()) {
			
			if ( item.getCampaignItemType().isAllowPending() ) {
				
				
				double quantity = calculateCampaignItemQuantityFaster(item.getQuantity(), item.getCampaignItemType(), 
						monthlyAmount);
				logger.info("Adding pending action to needy case with id " + needyCaseId + ",and camapgin id is " + campaign.getId());
				long addedCampaignId = campaignDao.addCampaignActionUsingData(actor, needyCaseId, new Date(), campaign.getId(), CampaignActionStatusType.PENDING);
				campaignDao.addCampaingActionItemUsingData(actor, addedCampaignId, monthlyCampaignItemTypeId, quantity);
			}
			if ( item.getCampaignItemType().getId() == monthlyCampaignItemTypeId ) {
				
				this.addMonthsToLastNeedycaseMonthBenefit(needyCaseId, lastBenefitDate, 
						Double.valueOf((item.getQuantity())).intValue() );
			}
		}
		
		
	}
	
	private boolean checkCampaignBeforeAdd(Campaign campaign) {
		
		List<Long> itemTypes = new ArrayList<Long>();
		for(CampaignItem campaignItem : campaign.getCampaignItems()) {
			
			if (itemTypes.indexOf( Long.valueOf(campaignItem.getCampaignItemType().getId()) ) >= 0) {
				return false;
			}
			itemTypes.add( Long.valueOf(campaignItem.getCampaignItemType().getId()) );
		}
		return true;
	}
}
