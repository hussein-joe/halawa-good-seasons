package com.halawa.goodseasons.service;

import java.util.Date;
import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.goodseasons.common.constants.CampaignType;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignAction;
import com.halawa.goodseasons.model.entity.CampaignActionItem;
import com.halawa.goodseasons.model.entity.CampaignItemType;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.User;
import com.halawa.goodseasons.service.model.CampaignFinalReport;
import com.halawa.goodseasons.service.model.PendingNeedyCaseBenefit;

public interface CampaignService {

	String PARAMS_LAST_ACTIVE_CAMPAIGN_MONTH = "campaign.regullarCampaign.lastActiveCampaignMonth";
	String CONFIG_ADVANCED_MONTHS_CAMPAIGN_ITEM_ID	= "campaign.exceptionalCampaign.campaignItemType.advancedMonths.id";
	String CONFIG_CAMPAIGN_ITEMTYPE_MONTHLY_ITEM_ID = "campaign.regularCampaign.campaignItemType.monthlyCampaignItemId";
	
	List<CampaignItemType> loadCampaignItemTypes(NeedyCaseTypes...caseTypes);
	Campaign loadActiveCampaign(NeedyCaseTypes caseType) throws HalawaBusinessException;
	Campaign loadActiveCampaign(CampaignType campaignType, NeedyCaseTypes needyCaseType) throws HalawaBusinessException;
	CampaignAction loadCampaignAction(Campaign campaign, NeedyCase needyCase);
	CampaignAction loadCampaignActionFetchItemsById(Long campaignActionId);
	void addRegullarCampaign(Campaign campaign) throws HalawaBusinessException;
	void updateCampaign(Campaign campaign) throws HalawaBusinessException;
	void addIrregullarCampaign(Campaign campaign) throws HalawaBusinessException;
	List<Campaign> loadActiveCampaigns() throws HalawaBusinessException;
	List<Campaign> loadOldCampaigns();
	/**
	 * This method will return the date of the last month which is covered in last campaign.
	 * @return
	 */
	Date loadLastRegularCampaignMonth();
	CampaignFinalReport generateCampaignFinalReport(Long campaignId) throws HalawaBusinessException;
	CampaignFinalReport generateCampaignFinalReportFaster(Long campaignId) throws HalawaBusinessException;
	
	void closeCampaign(User actor, Campaign campaign) throws HalawaBusinessException;
	void resetExceptionalCampaign(User actor, Campaign campaign) throws HalawaBusinessException;
	Campaign loadCampaignWithItemsById(long campaignId)throws HalawaBusinessException;
	void addRegularCampaignBenefit(User actor, String nationalId) throws HalawaBusinessException;
	void addRegularCampaignBenefit(User actor, String nationalId, List<CampaignActionItem> campaignActionItems) throws HalawaBusinessException;
	void addIrregularCampaignBenefit(User actor, String nationalId, List<CampaignActionItem> campaignActionItems) throws HalawaBusinessException;
	void addExceptionalCampaignBenefit(User actor, String nationalId, List<CampaignActionItem> campaignActionItems, String note) throws HalawaBusinessException;
	PendingNeedyCaseBenefit loadNeedyCasePendingBenefits(NeedyCase needyCase) throws HalawaBusinessException;
	void refuseAllPendingCampaignActions(NeedyCase needyCase) throws HalawaBusinessException;
	void deliverPendingCampaignBenefit(User actor, PendingNeedyCaseBenefit pendingNeedyCaseBenefit) throws HalawaBusinessException;
}
