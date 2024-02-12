package com.halawa.goodseasons.web.backing.campaign;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.CampaignType;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.service.model.CampaignFinalReport;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.util.FacesUtil;
import com.halawa.goodseasons.web.util.WebErrorCodes;

public class CloseCampaignBean extends AbstractBean {

	private static final HalawaLogger logger = HalawaLogger.getInstance(CloseCampaignBean.class);
	private Campaign campaign;
	private CampaignFinalReport campaignFinalReport;
	private short needyCaseType;
	
	public CloseCampaignBean() {
		
		this.campaign = new Campaign();
	}
	
	public String prepareCampaignForClosing() {
		
		try {
		
			String selectedCampaignId = FacesUtil.getRequestParameter("selectedCampaignId");
			if ( StringUtil.isEmpty( selectedCampaignId ) ) {
				
				throw new HalawaSystemException(WebErrorCodes.NO_ENTRY_SELECTED.getErrorCode());
			}
			
			logger.info("Loading the campaign with id " + selectedCampaignId + ", to be closed");
			this.campaignFinalReport = this.campaignService.generateCampaignFinalReportFaster(Long.parseLong(selectedCampaignId));
			this.campaign = this.campaignFinalReport.getCampaign();
			this.needyCaseType = this.campaign.getCaseType();
			return "closeActiveCampaign";
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to load the closed campaigns", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to load the closed campaigns", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to load the closed campaigns", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return null;
	}

	public String closeCampaign() {
		
		try {
			Campaign reloadedCampaign = this.campaignService.loadCampaignWithItemsById(campaign.getId());
			if ( reloadedCampaign.getCampaignType() == CampaignType.EXCEPTIONAL_CAMPAIGN.getType() ) {
				this.campaignService.resetExceptionalCampaign(super.getLoggedInWebUser().getUser(), reloadedCampaign);
			} else {
				this.campaignService.closeCampaign(super.getLoggedInWebUser().getUser(), campaign);
			}
			return "manageActiveCampaigns";
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to load the closed campaigns", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to load the closed campaigns", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to load the closed campaigns", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		return null;
	}
	public CampaignFinalReport getCampaignFinalReport() {
		return campaignFinalReport;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public short getNeedyCaseType() {
		return needyCaseType;
	}
}
