package com.halawa.goodseasons.common.constants;

import com.halawa.common.exception.HalawaErrorCode;

public enum CampaignErrorCodes {

	CAMPAIGN_MUST_HAS_ITEMS(new HalawaErrorCode("campaign_campaignMustHasItems")),
	NO_ACTIVE_CAMPAIGN_DEFINED(new HalawaErrorCode("campaign_NoActiveCampaignDefined")),
	ACTION_NEEDYCASE_TAKE_BENEFIT_BEFORE(new HalawaErrorCode("campaign_action_needyCaseTakeBenefitBefore")),
	ACTION_NEEDYCASE_LASTBENEFITDATE_EXCEED_CAMPAIGNDATE(new HalawaErrorCode("campaign_action_needyCaseLastBenefitDateExceedCampaignDate")),
	ADD_CAMPAIGN_ACTIVE_CAMPAIGNS_EXISTS(new HalawaErrorCode("campaign_addCampaign_activeCampaignsExists")),
	ADD_CAMPAIGN_DUPLICATE_ITEM_TYPES(new HalawaErrorCode("campaign_addCampaign_duplicateItemTypes")),
	
	CAMPAIGN_ALREADY_CLOSED(new HalawaErrorCode("campaign_closeCampaign_alreadyClosed")),
	CAMPAIGN_BENEFITS_ADD_NO_ACTIVE_CAMPAIGN_EXIST(new HalawaErrorCode("campaign_addBenefit_noActiveCampaignExist")),
	CAMPAIGN_BENEFITS_ADD_VALUE_NOT_IN_RANGE(new HalawaErrorCode("campaign_addBenefit_valueNotInRange")),
	CAMPAIGN_BENEFITS_ADD_NO_NATIOANAL_AND_OLD_NUMBER_ENTERED(new HalawaErrorCode("campaign_addBenefit_noNationalAndOldNumber"));
	
	
	
	
	private HalawaErrorCode errorCode;
	private CampaignErrorCodes(HalawaErrorCode errorCode) {
		
		this.errorCode = errorCode;
	}
	
	public HalawaErrorCode getErrorCode() {
		
		return this.errorCode;
	}
}
