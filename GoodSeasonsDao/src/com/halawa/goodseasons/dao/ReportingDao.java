package com.halawa.goodseasons.dao;

import java.util.List;

import com.halawa.goodseasons.common.constants.CampaignActionStatusType;
import com.halawa.goodseasons.dao.model.CampaignItemTotal;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignAction;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.User;

public interface ReportingDao {

	List<Campaign> getUserCampaignsInPeriod(User user, int numberOfDays);
	List<Campaign> getUserCampaignsInPeriod(List<Campaign> selectedCampaigns, User user, int numberOfDays);
	int countUserCampaignActions(User user, Campaign campaign, int numberOfDays, CampaignActionStatusType... actionStatusTypes);
	List<CampaignItemTotal> getUserCampaignActionItems(User user, Campaign campaign, int numberOfDays, CampaignActionStatusType... actionStatusTypes);
	List<NeedyCase> loadNeedyCasesWithNationalId(String nationalId);
	List<CampaignAction> loadNeedyCasesCampaignActions(List<NeedyCase> needyCases);
}
