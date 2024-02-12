package com.halawa.goodseasons.web.backing.reporting;

import java.util.ArrayList;
import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.User;
import com.halawa.goodseasons.service.ReportingService;
import com.halawa.goodseasons.service.model.CampaignActionsUserReportCriteria;
import com.halawa.goodseasons.service.model.CampaignActionsUserReportResult;
import com.halawa.goodseasons.web.backing.campaign.CampaignAbstractBean;
import com.halawa.goodseasons.web.backing.common.MenuBean;

public class ReportingBean extends CampaignAbstractBean {

	private static final HalawaLogger logger = HalawaLogger.getInstance(ReportingBean.class);
	private ReportingService reportingService;
	
	private CampaignActionsUserReportResult campaignActionsUserReportResult;
	private CampaignActionsUserReportCriteria reportCriteria = new CampaignActionsUserReportCriteria();
	private Long selectedUser;
	private String[] selectedCampaignIds;
	private MenuBean menuBean;

	public String initReportCriteria() {

		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
		
		this.campaignActionsUserReportResult = null;
		this.reportCriteria = new CampaignActionsUserReportCriteria();
		this.selectedUser = null;
		this.selectedCampaignIds = null;
		
		return dest;
	}
	
	public String generateReport() {
		
		
		try {
			
			User user = new User();
			user.setId( this.selectedUser );
			this.reportCriteria.setSelectedUser(user);
			
			/*if ( reportCriteria.getSelectedUser() == null ) {
				
				logger.error("The actor didn't select any user");
				throw new HalawaSystemException(new HalawaErrorCode("web.reporting.campaignUserActions.userNotSelected"));
			}*/
			if ( this.selectedCampaignIds != null && this.selectedCampaignIds.length > 0 ) {
				
				List<Campaign> campaigns = new ArrayList<Campaign>();
				for(String campaignId: selectedCampaignIds) {
					Campaign campaign = new Campaign();
					campaign.setId( Long.valueOf(campaignId) );
					campaigns.add(campaign);
				}
				this.reportCriteria.setSelectedCampaigns(campaigns);
			} else {
				
				this.reportCriteria.setSelectedCampaigns(null);
			}
			
			this.campaignActionsUserReportResult = this.reportingService.generateUserCampaignActionsReport(reportCriteria);
			
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to add/update the irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to add/update the irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to add/update the irregular needy case", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		return "showUserCampaignActionsReportResult";
	}

	public CampaignActionsUserReportResult getCampaignActionsUserReportResult() {
		return campaignActionsUserReportResult;
	}

	public CampaignActionsUserReportCriteria getReportCriteria() {
		return reportCriteria;
	}

	public void setReportingService(ReportingService reportingService) {
		this.reportingService = reportingService;
	}

	public Long getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(Long selectedUser) {
		this.selectedUser = selectedUser;
	}

	public String[] getSelectedCampaignIds() {
		return selectedCampaignIds;
	}

	public void setSelectedCampaignIds(String[] selectedCampaignIds) {
		this.selectedCampaignIds = selectedCampaignIds;
	}

	public void setMenuBean(MenuBean menuBean) {
		this.menuBean = menuBean;
	}
	
}
