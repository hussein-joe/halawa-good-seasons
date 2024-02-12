package com.halawa.goodseasons.web.backing.campaign;

import java.util.ArrayList;
import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.CampaignErrorCodes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.model.entity.CampaignActionItem;
import com.halawa.goodseasons.model.entity.RegularCase;
import com.halawa.goodseasons.service.NeedyCaseService;
import com.halawa.goodseasons.service.model.NeedyCaseBenefit;
import com.halawa.goodseasons.service.model.NeedyCaseBenefit.CalculatedCampaignItem;
import com.halawa.goodseasons.web.backing.common.MenuBean;

public class AddExceptionalCampaignBenefit extends CampaignAbstractBean {

	private static final HalawaLogger logger = HalawaLogger.getInstance(AddExceptionalCampaignBenefit.class);
	private NeedyCaseBenefit needyCaseBenefit;
	private String needyCaseNationalId;
	private String needyCaseOldNumber;
	private String note;
	private NeedyCaseService needyCaseService;
	private double totalMonyAmount = 0;
	private MenuBean menuBean;
	
	public String initializeAddingExceptionalCampaignBenefit() {
		
		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
		this.clear();
		return dest;
	}
	
	public String searchForNeedyCase() {
		
		try {
			
			logger.info("Searching for needy case with national id " + this.needyCaseNationalId + " to take exceptional benefit");
			
			if ( !StringUtil.isEmpty( this.getNeedyCaseNationalId() ) && !validateNationalId_14(this.getNeedyCaseNationalId()) ) {
				
				super.addMessage("customValidators_common_nationalIdValidation_7_or_14");
				return null;
			}
			
			if ( !StringUtil.isEmpty( this.getNeedyCaseNationalId() ) ) {
				this.needyCaseBenefit = this.needyCaseService.loadNeedyCaseForExceptionalBenefitDelivery(this.needyCaseNationalId);
			} else if ( !StringUtil.isEmpty( this.getNeedyCaseOldNumber() ) ) {
				
				this.needyCaseBenefit = this.needyCaseService.loadNeedyCaseForExceptionalBenefitDeliveryByOldNumber(this.needyCaseOldNumber);
			} else {
				
				logger.warn("The user is trying to deliver benefit without typing either national number nor old number");
				throw new HalawaSystemException(CampaignErrorCodes.CAMPAIGN_BENEFITS_ADD_NO_NATIOANAL_AND_OLD_NUMBER_ENTERED.getErrorCode());
			}
			
			return "addExceptionalCampaignBenefit";
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to search about needy case for exceptional campaign benefit", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to search about needy case for exceptional campaign benefit", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to search about needy case for exceptional campaign benefit", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		return null;
	}
	
	public String deliverBenefitToNeedyCase() {
		
		try {

			logger.info("Delivering exceptional benefit for needy case with national id " + this.needyCaseNationalId);
			this.campaignService.addExceptionalCampaignBenefit(this.getLoggedInWebUser().getUser(), needyCaseNationalId, 
					getCampaignActionItems(), this.getNote());
			this.clear();
			return "userHome";
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(exception.getErrorCode());
		}  catch(HalawaSystemException exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to search about needy case for campaign benefit", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		return null;
	}

	private List<CampaignActionItem> getCampaignActionItems() {
		
		List<CampaignActionItem> campaignActionItems = new ArrayList<CampaignActionItem>();
		for(CalculatedCampaignItem calculatedCampaignItem: this.needyCaseBenefit.getCalculatedCampaignItems()) {
			
			CampaignActionItem campaignActionItem = new CampaignActionItem();
			campaignActionItem.setQuantity( calculatedCampaignItem.getCalculatedValue() );
			campaignActionItem.setCampaignItemType( calculatedCampaignItem.getCampaignItem().getCampaignItemType() );
			
			campaignActionItems.add(campaignActionItem);
		}
		
		return campaignActionItems;
	}

	private void clear() {
		
		this.needyCaseBenefit = null;
		this.needyCaseNationalId = null;
		this.totalMonyAmount = 0;
	}
	public String getNeedyCaseNationalId() {
		return needyCaseNationalId;
	}
	public void setNeedyCaseNationalId(String needyCaseNationalId) {
		this.needyCaseNationalId = needyCaseNationalId;
	}
	public NeedyCaseBenefit getNeedyCaseBenefit() {
		return needyCaseBenefit;
	}
	public double getTotalMonyAmount() {
		return totalMonyAmount;
	}
	public void setNeedyCaseService(NeedyCaseService needyCaseService) {
		this.needyCaseService = needyCaseService;
	}
	public void setMenuBean(MenuBean menuBean) {
		this.menuBean = menuBean;
	}
	public short getLoadedNeedyCaseType() {
		
		return (this.needyCaseBenefit.getNeedyCase() instanceof RegularCase)? NeedyCaseTypes.REGULAR.getType(): NeedyCaseTypes.IRREGULAR.getType();
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	public String getNeedyCaseOldNumber() {
		return needyCaseOldNumber;
	}
	public void setNeedyCaseOldNumber(String needyCaseOldNumber) {
		this.needyCaseOldNumber = needyCaseOldNumber;
	}
	
}
