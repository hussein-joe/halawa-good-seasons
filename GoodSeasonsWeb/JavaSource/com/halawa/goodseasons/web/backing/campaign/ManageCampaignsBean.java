package com.halawa.goodseasons.web.backing.campaign;

import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.service.CampaignService;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.backing.common.MenuBean;

public class ManageCampaignsBean extends AbstractBean {

	private static final HalawaLogger logger = HalawaLogger.getInstance(ManageCampaignsBean.class);
	private CampaignService campaignService;
	private List<Campaign> campaigns;
	private short selectedCaseType = (short) 0;
	private MenuBean menuBean;
	
	public String loadActiveCampaigns() {
		
		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
		this.campaigns = null;
		this.getActiveCampaigns();
		return "rightmenu_campaign_manageActiveCampaigns";
	}
	
	public String loadClosedCampaigns() {
		
		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
		this.campaigns = null;
		this.getClosedCampaigns();
		return "rightmenu_campaign_manageClosedCampaigns";
	}
	
	public List<Campaign> getActiveCampaigns() {
		
		try {
			
			if ( this.campaigns == null ) {
		
				logger.info("Loading the active campaigns");
				this.campaigns = this.campaignService.loadActiveCampaigns();
			}
			
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to load the active campaigns", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to load the active campaigns", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to load the active campaigns", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return this.campaigns;
	}
	
	public List<Campaign> getClosedCampaigns() {
		
		try {
			
			if ( this.campaigns == null ) {
				logger.info("Loading the closed campaigns");
				this.campaigns = this.campaignService.loadOldCampaigns();
			}
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to load the closed campaigns", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to load the closed campaigns", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return this.campaigns;
	}

	public short getSelectedCaseType() {
		return selectedCaseType;
	}

	public void setSelectedCaseType(short selectedCaseType) {
		this.selectedCaseType = selectedCaseType;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public void setMenuBean(MenuBean menuBean) {
		this.menuBean = menuBean;
	}
	
}
