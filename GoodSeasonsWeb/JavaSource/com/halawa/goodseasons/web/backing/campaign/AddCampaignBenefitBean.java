package com.halawa.goodseasons.web.backing.campaign;

import java.util.ArrayList;
import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.CampaignErrorCodes;
import com.halawa.goodseasons.common.constants.NeedyCaseErrorCodes;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.model.entity.CampaignActionItem;
import com.halawa.goodseasons.model.entity.IrregularCase;
import com.halawa.goodseasons.service.NeedyCaseService;
import com.halawa.goodseasons.service.model.NeedyCaseBenefit;
import com.halawa.goodseasons.service.model.NeedyCaseBenefit.CalculatedCampaignItem;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.backing.common.MenuBean;

public class AddCampaignBenefitBean extends AbstractBean {

	private static final HalawaLogger logger = HalawaLogger.getInstance(AddCampaignBenefitBean.class);
	
	private NeedyCaseBenefit needyCaseBenefit;
	private String needyCaseNationalId;
	private String needyCaseOldNumber;
	protected NeedyCaseService needyCaseService;
	private double totalMonyAmount = 0;
	protected MenuBean menuBean;
	
	public String initializeAddingCampaignBenefit() {
		
		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
		
		this.needyCaseNationalId = null;
		this.needyCaseOldNumber = null;
		this.needyCaseBenefit = null;
		this.totalMonyAmount = 0;
		return "rightmenu_needyCaseBenefit_addBenefit";
	}
	public String searchForNeedyCase() {
		
		try {
			
			logger.info("Searching for needy case with national id " + this.getNeedyCaseNationalId() + " to take " + 
					" benefit from current active campaign if open");
			
			if ( !StringUtil.isEmpty( this.needyCaseNationalId ) ) {
				
				this.needyCaseBenefit = this.needyCaseService.loadNeedyCaseForBenefitDelivery(this.needyCaseNationalId);
			} else if ( !StringUtil.isEmpty( this.needyCaseOldNumber ) ) {
				
				this.needyCaseBenefit = this.needyCaseService.loadNeedyCaseForBenefitDeliveryByOldNumber(this.needyCaseOldNumber);
			} else {
				
				logger.warn("The user is trying to deliver benefit without typing either national number nor old number");
				throw new HalawaSystemException(CampaignErrorCodes.CAMPAIGN_BENEFITS_ADD_NO_NATIOANAL_AND_OLD_NUMBER_ENTERED.getErrorCode());
			}
			
			if ( this.needyCaseBenefit.getNeedyCase() instanceof IrregularCase ) {
				
				logger.debug("The needy case is irregular");
				return "addIrregularCampaignBenefit";
			} else {
			
				logger.debug("The needy case is regular");
				this.totalMonyAmount = 0;
				for(CalculatedCampaignItem item:this.needyCaseBenefit.getCalculatedCampaignItems()) {
					
					if (item.getCampaignItem().getCampaignItemType().getAccumulationType() == 1) {
						
						this.totalMonyAmount += item.getCalculatedValue();
					}
				}
				return "addCampaignBenefit";
			}
			
			
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

	public String deliverBenefitToNeedyCase() {
		
		try {

			if ( this.needyCaseBenefit.getNeedyCase() instanceof IrregularCase ) {
				
				logger.info("Delivering benefit to irregular needy case with national id " + 
						this.needyCaseNationalId);
				this.campaignService.addIrregularCampaignBenefit(this.getLoggedInWebUser().getUser(), needyCaseNationalId, getCampaignActionItems());
			} else {
				
				logger.info("Delivering benefit to regular needy case with national id " + 
						this.needyCaseNationalId);
				this.campaignService.addRegularCampaignBenefit(this.getLoggedInWebUser().getUser(), needyCaseNationalId, getCampaignActionItems());
			}
			this.clear();
			return "defineCampaignBenefit";
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
	
	public String cancel() {
		
		this.needyCaseNationalId = null;
		return "defineCampaignBenefit";
	}
	
	private void clear() {
		
		this.needyCaseBenefit = null;
		this.needyCaseNationalId = null;
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

	public void setNeedyCaseService(NeedyCaseService needyCaseService) {
		this.needyCaseService = needyCaseService;
	}

	public double getTotalMonyAmount() {
		return totalMonyAmount;
	}
	public void setMenuBean(MenuBean menuBean) {
		this.menuBean = menuBean;
	}
	public String getNeedyCaseOldNumber() {
		return needyCaseOldNumber;
	}
	public void setNeedyCaseOldNumber(String needyCaseOldNumber) {
		this.needyCaseOldNumber = needyCaseOldNumber;
	}
	
	
}
