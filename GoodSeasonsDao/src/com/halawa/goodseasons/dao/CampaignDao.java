package com.halawa.goodseasons.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.halawa.goodseasons.common.constants.CampaignActionStatusType;
import com.halawa.goodseasons.common.constants.CampaignType;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignAction;
import com.halawa.goodseasons.model.entity.CampaignItemType;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.User;

public interface CampaignDao {

	List<CampaignItemType> loadCampaignItemTypes(NeedyCaseTypes... caseTypes);
	List<CampaignItemType> loadDefaultCampaignItemTypes(NeedyCaseTypes... caseTypes);
	//List<CampaignItemType> loadCampaignItemTypes(CampaignType campaignType, NeedyCaseTypes... caseTypes);
	//List<CampaignItemType> loadDefaultCampaignItemTypes(CampaignType campaignType, NeedyCaseTypes... caseTypes);
	
	void addCampaign(Campaign campaign);
	List<Campaign> loadActiveCampiagns(CampaignType campaignType, NeedyCaseTypes...caseTypes);
	List<Campaign> loadActiveCampiagns(NeedyCaseTypes...caseTypes);
	List<Campaign> loadActiveCampiagns();
	List<Campaign> loadClosedCampaigns(PaginationData paginationData, NeedyCaseTypes...caseTypes);
	CampaignAction loadNeedyCaseCampaignAction(Campaign campaign, NeedyCase needyCase);
	CampaignAction loadNeedyCaseCampaignAction(Campaign campaign, NeedyCase needyCase, CampaignActionStatusType actionStatusType);
	CampaignAction loadCampaignActionFetchItemsById(Long campaignActionId);
	Campaign loadCampaignWithItemsById(Long campaignId);
	List<CampaignAction> loadCampaignActions(Campaign campaign, PaginationData paginationData);
	List<CampaignAction> loadCampaignActionsWithItems(Campaign campaign, PaginationData paginationData, 
			CampaignActionStatusType... actionStatusTypes);
	
	Map<CampaignItemType, Double> countCampaignActionsWithItems(Campaign campaign, 
			CampaignActionStatusType... actionStatusTypes);
	
	void updateCampaign(Campaign campaign);
	void addCampaignAction(CampaignAction campaignAction);
	int countCampaignActions(Campaign campaign, CampaignActionStatusType...actionStatusTypes);
	List<BigInteger> loadCampaignActionsNeedyCasesIds(Campaign campaign, CampaignActionStatusType... actionStatusTypes);
	List<CampaignAction> loadNeedyCaseActionsFetchCampaignAndActionItems(NeedyCase needyCase, CampaignActionStatusType actionStatusType);
	void updateAllPendingCampaignActions(NeedyCase needyCase, CampaignActionStatusType newActionStatus);
	void updatePendingCampaignActionsForCampaigns(NeedyCase needyCase, List<Campaign> campaigns, CampaignActionStatusType newActionStatus);
	
	
	List<Object[]> loadCampaingPendingNeedyCasesIds(Campaign campaign);
	long addCampaignActionUsingData(User actor, long needCaseId, Date addingDate, long campaignId, CampaignActionStatusType statusType);
	void addCampaingActionItemUsingData(User actor, long campaingActionId, long itemTypeId, double quantity);
	void updateRegularNeedyCaseLastBenefitDate(long needyCaseId, Date lastBenefitDate);
	
	void forgetExceptionalCampaignActions(User actor, Campaign campaign);
}
