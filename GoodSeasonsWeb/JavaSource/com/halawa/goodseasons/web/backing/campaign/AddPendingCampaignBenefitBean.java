package com.halawa.goodseasons.web.backing.campaign;

import javax.faces.application.FacesMessage;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.CampaignErrorCodes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.common.constants.PrivilegesEnum;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.RegularCase;
import com.halawa.goodseasons.service.model.PendingNeedyCaseBenefit;
import com.halawa.goodseasons.web.util.WebErrorCodes;

public class AddPendingCampaignBenefitBean extends AddCampaignBenefitBean {
	
	private static final HalawaLogger logger = HalawaLogger.getInstance(AddPendingCampaignBenefitBean.class);
	private PendingNeedyCaseBenefit pendingNeedyCaseBenefit;
	
	@Override
	public String initializeAddingCampaignBenefit() {
	
		this.pendingNeedyCaseBenefit = null;
		return super.initializeAddingCampaignBenefit();
	}

	@Override
	public String searchForNeedyCase() {
	
		String dest = null;
		NeedyCase needyCase;
		try {
			
			logger.info("Searching for needy case with national id " + this.getNeedyCaseNationalId());
			
			if ( !StringUtil.isEmpty( this.getNeedyCaseNationalId() ) && !validateNationalId_14(this.getNeedyCaseNationalId()) ) {
				
				super.addMessage("customValidators_common_nationalIdValidation_7_or_14");
				return null;
			}
			
			
			
			needyCase = null;
			if ( !StringUtil.isEmpty( this.getNeedyCaseNationalId() ) ) {
				
				needyCase = this.needyCaseService.loadNeedyCaseByNationalId(this.getNeedyCaseNationalId());
				
			} else if ( !StringUtil.isEmpty( this.getNeedyCaseOldNumber() ) ) {
				
				needyCase = this.needyCaseService.loadNeedyCaseByOldNumber(this.getNeedyCaseOldNumber());
			} else {
				
				logger.warn("The user is trying to deliver benefit without typing either national number nor old number");
				throw new HalawaSystemException(CampaignErrorCodes.CAMPAIGN_BENEFITS_ADD_NO_NATIOANAL_AND_OLD_NUMBER_ENTERED.getErrorCode());
			}
			
			if ( needyCase instanceof RegularCase ) {
				this.campaignService.loadActiveCampaign(NeedyCaseTypes.REGULAR);
				this.pendingNeedyCaseBenefit = this.campaignService.loadNeedyCasePendingBenefits(needyCase);
				if ( this.pendingNeedyCaseBenefit == null ) {
					
					dest = super.searchForNeedyCase();
					return dest;
				} 
				
				if ( !this.getCanDeliverPendingNeedyCaseBenefits() ) {
					
					logger.info("The regular needy case with national id " + needyCase.getNationalId() + ", has pending benefits, " + 
							", but logged in user has no privilege to deliver those pending benefits");
					
					super.addMessage("campaign_action_pending_userHasNoPrivilege");
					return null;
				}
			} else {
				
				dest = super.searchForNeedyCase();
				return dest;
			}
			
			super.addMessage("campaign_pendingBenefit_needyCaseHasPendingBenefitWarnning", FacesMessage.SEVERITY_INFO);
			
			if ( this.pendingNeedyCaseBenefit.getPendingCampaignActions().size() == 1 ) {
				
				return "viewNeedycasePendingOneBenefit";
			} else {
				
				return "viewNeedycasePendingOneBenefit";
			}
			
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return null;
	}

	public String refuseAllPendingCampaignActions() {
		
		try {
			
			logger.info("Refusing all pending campaign actions");
			this.campaignService.refuseAllPendingCampaignActions(this.pendingNeedyCaseBenefit.getNeedyCase());
			logger.info("All pending campaign actions refused, now searching for needy case for new benefit");
			super.addMessage("campaign_pendingActions_actionsRefusedSuccessfully", FacesMessage.SEVERITY_INFO);
			return super.searchForNeedyCase();
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return "defineCampaignBenefit";
	}
	
	public String deliverPendingCampaignBenefits() {
		
		try {
		
			logger.info("Delivering the pending campaign actions");
			this.campaignService.deliverPendingCampaignBenefit(super.getLoggedInWebUser().getUser(), pendingNeedyCaseBenefit);
			logger.info("The pending campaign actions delivered successfully");
			super.addMessage("campaign_pendingActions_actionsDeliveredSuccessfully", FacesMessage.SEVERITY_INFO);
			return super.searchForNeedyCase();
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(exception.getErrorCode().getErrorCode(), exception.getParameters());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return "defineCampaignBenefit";
	}
	
	public PendingNeedyCaseBenefit getPendingNeedyCaseBenefit() {
		return pendingNeedyCaseBenefit;
	}
	
	public boolean getCanDeliverPendingNeedyCaseBenefits() {
		
		return super.isUserHasPrivilege(PrivilegesEnum.CAMPAIGN_BENEFITS_DELIVERPENDINGACTIONS);
	}
}
