package com.halawa.goodseasons.web.backing.campaign;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.CampaignErrorCodes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignItem;
import com.halawa.goodseasons.model.entity.CampaignItemType;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.backing.common.MenuBean;
import com.halawa.goodseasons.web.util.FacesUtil;
import com.halawa.goodseasons.web.util.WebErrorCodes;

public class AddUpdateCampaignBean extends AbstractBean {

	private static final HalawaLogger logger = HalawaLogger.getInstance(AddUpdateCampaignBean.class);
	private MenuBean menuBean;
	private Campaign campaign;
	private List<CampaignItem> campaignItems;
	private int selectedCampaignItemIndex = -1;
	private String maxCampaignsActions = null;
	
	public String prepareForAddingRegullarCampaign() {
		
		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
		
		try {
			
			maxCampaignsActions = null;
			this.campaign = new Campaign();
			this.campaignItems = new ArrayList<CampaignItem>();
			
			CampaignItem campaignItem = new CampaignItem();
			CampaignItemType campaignItemType = new CampaignItemType();
			campaignItemType.setId( 1 );
			campaignItem.setQuantity(1);
			campaignItem.setCampaignItemType(campaignItemType);
			
			this.campaignItems.add(campaignItem);
			SimpleDateFormat dateFormat = new SimpleDateFormat("M-yyyy", new Locale("ar"));
			String lastMonth = dateFormat.format( this.campaignService.loadLastRegularCampaignMonth() );
			this.campaign.setName(super.getMessage("campaign_addCampaign_regullarCampaign_defaultCampaignName", lastMonth).getSummary());
			
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to save the campaign", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to save the campaign", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		return dest;
	}
	
	public String prepareForAddingIrregullarCampaign() {
		
		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
	
		try {
			
			maxCampaignsActions = null;
			this.campaignItems = new ArrayList<CampaignItem>();
			this.campaign = new Campaign();
			CampaignItem campaignItem = new CampaignItem();
			CampaignItemType campaignItemType = new CampaignItemType();
			campaignItemType.setId( 6 );
			campaignItem.setQuantity(1);
			campaignItem.setCampaignItemType(campaignItemType);
			
			this.campaignItems.add(campaignItem);
			SimpleDateFormat dateFormat = new SimpleDateFormat("M-yyyy", new Locale("ar"));
			String lastMonth = dateFormat.format( new Date() );
			this.campaign.setName(super.getMessage("campaign_addCampaign_irregullarCampaign_defaultCampaignName", lastMonth).getSummary());
			
		} catch(HalawaSystemException exception) {
		
			logger.error("Failed to save the campaign", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
		
			logger.error("Failed to save the campaign", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		return dest;
	}
	
	public String saveRegullarCampaign() {
		
		logger.info("Saving the campaign with id " + this.campaign.getId());
		
		try {
			
			if ( StringUtil.isEmpty( this.campaign.getName() ) ) {
				
				super.addMessage("xxxxxxxxxxxxxx");
				return null;
			}
			
			if ( this.campaignItems == null || this.campaignItems.isEmpty() ) {
				
				logger.error("The user didn't select any item to campaign");
				throw new HalawaBusinessException(CampaignErrorCodes.CAMPAIGN_MUST_HAS_ITEMS.getErrorCode());
			}
			if ( !StringUtil.isEmpty(maxCampaignsActions) ) {
				
				this.campaign.setMaxCases( Integer.valueOf(this.maxCampaignsActions) );
			}
			for( CampaignItem item:this.campaignItems ) {
				
				this.campaign.getCampaignItems().add(item);
				item.setCampaign(this.campaign);
			}
			if ( this.campaign.getId() == null ) {
				
				logger.info("Adding new regular campaign");
				this.campaignService.addRegullarCampaign(campaign);
			} else {
				logger.info("Updating the selected regular campaign");
				this.campaignService.updateCampaign(campaign);
				//TODO update campaign
			}
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to save the campaign", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to save the campaign", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to save the campaign", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return "userHome";
	}
	
	public String saveIrregullarCampaign() {
		
		logger.info("Saving the campaign with id " + this.campaign.getId());
		
		try {
			if ( this.campaignItems == null || this.campaignItems.isEmpty() ) {
				
				logger.error("The user didn't select any item to campaign");
				throw new HalawaBusinessException(CampaignErrorCodes.CAMPAIGN_MUST_HAS_ITEMS.getErrorCode());
			}
			if ( !StringUtil.isEmpty(maxCampaignsActions) ) {
				
				this.campaign.setMaxCases( Integer.valueOf(this.maxCampaignsActions) );
			}
			for( CampaignItem item:this.campaignItems ) {
				
				this.campaign.getCampaignItems().add(item);
				item.setCampaign(this.campaign);
			}
			if ( this.campaign.getId() == null ) {
				
				logger.info("Adding irregular campaign");
				this.campaignService.addIrregullarCampaign(campaign);
			} else {
				logger.info("Updating the selected irregular camapaign");
				this.campaignService.updateCampaign(campaign);
				//TODO update campaign
			}
			
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to save the campaign", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to save the campaign", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to save the campaign", exception);
			super.addMessage(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		return "userHome";
	}

	public String editCampaign() {
		
		String selectedCampaignId = FacesUtil.getRequestParameter("selectedCampaignId");
		if ( StringUtil.isEmpty( selectedCampaignId ) ) {
			
			throw new HalawaSystemException(WebErrorCodes.NO_ENTRY_SELECTED.getErrorCode());
		}
		
		try {
		
			this.campaign = this.campaignService.loadCampaignWithItemsById( Long.parseLong( selectedCampaignId ) );
			this.campaignItems = new ArrayList<CampaignItem>();
			for(CampaignItem campaignItem: this.campaign.getCampaignItems()) {
				
				this.campaignItems.add(campaignItem);
			}
			if( this.campaign.getCaseType() == NeedyCaseTypes.REGULAR.getType() ) {
				return "editRegularCampaign";
			} else if ( this.campaign.getCaseType() == NeedyCaseTypes.IRREGULAR.getType() ) {
				return "editIrregularCampaign";
			}
			
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to add/update the campaign", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to add/update the campaign", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to add/update the campaign", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		} 
 		return null;
	}	
	public void addCampaignItem() {
		
		try {
			CampaignItem campaignItem = new CampaignItem();
			campaignItem.setCampaignItemType( new CampaignItemType() );
			campaignItem.setQuantity(1);
			
			this.campaignItems.add(campaignItem);
		} catch(Exception exception) {
			
			logger.error("Failed to add new campaign item", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
	}
	
	public void deleteCampaignItem() {
		
		try {
			logger.info("Deleting campaign item with index " + this.selectedCampaignItemIndex);
			if ( this.selectedCampaignItemIndex < 0 ) {
				
				//TODO throw exception
			}
			this.campaignItems.remove( this.selectedCampaignItemIndex );
		} catch(Exception exception) {
			
			logger.error("Failed to delete the campaign items", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR.getErrorCode());
		}
	}
	
	public boolean getCanUpdateCampaign() {
		
		return this.campaign == null || this.campaign.getStatus() != 2;
	}
	
	public Campaign getCampaign() {
		return campaign;
	}

	public void setMenuBean(MenuBean menuBean) {
		this.menuBean = menuBean;
	}

	public int getSelectedCampaignItemIndex() {
		return selectedCampaignItemIndex;
	}
	
	public void setSelectedCampaignItemIndex(int selectedCampaignItemIndex) {
		this.selectedCampaignItemIndex = selectedCampaignItemIndex;
	}

	public List<CampaignItem> getCampaignItems() {
		return campaignItems;
	}

	public String getMaxCampaignsActions() {
		return maxCampaignsActions;
	}

	public void setMaxCampaignsActions(String maxCampaignsActions) {
		this.maxCampaignsActions = maxCampaignsActions;
	}
	
}
