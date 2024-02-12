package com.halawa.goodseasons.web.backing.reporting;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.service.ImportDataService;
import com.halawa.goodseasons.service.model.CampaignFinalReport;
import com.halawa.goodseasons.service.model.CampaignSummaryReportCriteria;
import com.halawa.goodseasons.web.backing.campaign.CampaignAbstractBean;
import com.halawa.goodseasons.web.backing.common.MenuBean;

public class CampaignSummaryReportBean extends CampaignAbstractBean {

	private static final HalawaLogger logger = HalawaLogger.getInstance(CampaignSummaryReportBean.class);
	private CampaignSummaryReportCriteria campaignSummaryReportCriteria = new CampaignSummaryReportCriteria();
	private MenuBean menuBean;
	private CampaignFinalReport campaignFinalReport;
	@SuppressWarnings("unused")
	private ImportDataService importDataService = null;
	
	public String initCampaignSummaryReport() {
		
		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
	
		/*
		try {
			this.importDataService.importData();
		} catch (Exception e) {
			logger.error("Failed");
		}*/
		this.campaignSummaryReportCriteria = new CampaignSummaryReportCriteria();
		return dest;
	}
	
	public String generateCampaignSummaryReport() {
		
		try {
		
			logger.info("Generating the campaign summary report");
			this.campaignFinalReport = this.campaignService.generateCampaignFinalReportFaster(this.getCampaignSummaryReportCriteria().getCampaignId());
			return "viewCampaignSummaryReportResult";
			
		}  catch(HalawaBusinessException exception) {
			
			logger.error("Failed to generate campaign summary report", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to generate campaign summary report", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to generate campaign summary report", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		return null;
	}
	
	public CampaignSummaryReportCriteria getCampaignSummaryReportCriteria() {
		return campaignSummaryReportCriteria;
	}
	
	public CampaignFinalReport getCampaignFinalReport() {
		return campaignFinalReport;
	}

	public void setMenuBean(MenuBean menuBean) {
		this.menuBean = menuBean;
	}

	public void setImportDataService(ImportDataService importDataService) {
		this.importDataService = importDataService;
	}
	
	
}
