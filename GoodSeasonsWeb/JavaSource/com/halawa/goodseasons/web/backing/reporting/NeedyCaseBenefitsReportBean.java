package com.halawa.goodseasons.web.backing.reporting;

import java.util.ArrayList;
import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.model.entity.CampaignAction;
import com.halawa.goodseasons.model.entity.CampaignActionItem;
import com.halawa.goodseasons.service.model.NeedyCaseBenefitsReportCriteria;
import com.halawa.goodseasons.service.model.NeedyCaseBenefitsReportResult;
import com.halawa.goodseasons.web.util.FacesUtil;

public class NeedyCaseBenefitsReportBean extends AbstractReportingBean {
	
	private static final HalawaLogger logger = HalawaLogger.getInstance(NeedyCaseBenefitsReportBean.class);
	
	private NeedyCaseBenefitsReportResult needyCaseBenefitsReportResult;
	private NeedyCaseBenefitsReportCriteria needyCaseBenefitsReportCriteria;
	private CampaignAction selectedCampaignAction;
	private List<CampaignActionItem> selectedCampaignActionItems;
	private String selectedCampaignActionId;
	
	
	
	public NeedyCaseBenefitsReportBean() {
		
		this.needyCaseBenefitsReportResult = null;
		this.needyCaseBenefitsReportCriteria = new NeedyCaseBenefitsReportCriteria();
		this.selectedCampaignAction = new CampaignAction();
		this.selectedCampaignAction.setCampaignActionItems(null);
	}

	public String generateNeedyCaseBenefitsReport() {
		
		String nationalId = FacesUtil.getRequestParameter("selectedNationalId");
		this.needyCaseBenefitsReportCriteria.setNationalId(nationalId);
		return this.generateReport();
	}

	public String generateReport() {
		
		try {
			
			logger.info("Generating needy case benefits report");
			this.needyCaseBenefitsReportResult = this.reportingService.generateNeedyCaseBenefitReport(needyCaseBenefitsReportCriteria);
			return "viewNeedycaseBenefitsReport";
			
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

	public void loadSelectedCampaignAction() {
		
		if ( this.selectedCampaignAction != null ) {
			
			return;
		}
		try {
		
			//selectedCampaignActionId = "149";
			logger.info("Loading the selected campaign action items, where selected campaign id = " + selectedCampaignActionId);
			this.selectedCampaignAction = this.campaignService.loadCampaignActionFetchItemsById(Long.parseLong(selectedCampaignActionId));
			this.selectedCampaignActionItems = new ArrayList<CampaignActionItem>();
			this.selectedCampaignActionItems.addAll(this.selectedCampaignAction.getCampaignActionItems());
			logger.info("The loaded items size is " + this.selectedCampaignAction.getCampaignActionItems().size());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to generate campaign summary report", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to generate campaign summary report", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
	}
	
	public NeedyCaseBenefitsReportResult getNeedyCaseBenefitsReportResult() {
		return needyCaseBenefitsReportResult;
	}
	
	public NeedyCaseBenefitsReportCriteria getNeedyCaseBenefitsReportCriteria() {
		return needyCaseBenefitsReportCriteria;
	}

	public CampaignAction getSelectedCampaignAction() {
		
		this.loadSelectedCampaignAction();
		return selectedCampaignAction;
	}

	public String getSelectedCampaignActionId() {
		return selectedCampaignActionId;
	}

	public void setSelectedCampaignActionId(String selectedCampaignActionId) {
		
		this.selectedCampaignAction = null;
		logger.info("Setting selected campaignAction id to " + selectedCampaignActionId);
		this.selectedCampaignActionId = selectedCampaignActionId;
	}

	public List<CampaignActionItem> getSelectedCampaignActionItems() {
		
		this.loadSelectedCampaignAction();
		return selectedCampaignActionItems;
	}
}
