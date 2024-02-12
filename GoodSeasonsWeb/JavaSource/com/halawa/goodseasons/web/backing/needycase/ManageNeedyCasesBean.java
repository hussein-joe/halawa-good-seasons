package com.halawa.goodseasons.web.backing.needycase;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.richfaces.model.selection.SimpleSelection;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.NeedyCaseStatusTypes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.common.constants.PrivilegesEnum;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.service.NeedyCaseService;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.backing.common.MenuBean;
import com.halawa.goodseasons.web.entity.NeedyCaseExportModel;

public class ManageNeedyCasesBean extends AbstractBean{

	public static final String PRIV_CONVERT_TO_REGULAR_NEEDYCASE = "";
	private static final HalawaLogger logger = HalawaLogger.getInstance(ManageNeedyCasesBean.class);
	
	private NeedyCaseService needyCaseService;
	private SimpleSelection selection = new SimpleSelection();
	private List<WebNeedyCase> selectedNeedyCases;
	private String tableState;
	private NeedyCaseDataModel needyCaseDataModel;
	private MenuBean menuBean;
	private NeedyCaseExportModel needyCaseExportModel;
	
	public String manageNeedyCases() {
		
		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
		
		this.needyCaseDataModel = null;
		return dest;
	}
	
	public NeedyCaseDataModel getRegularNeedyCaseDataModel() {
		
		if ( this.needyCaseDataModel == null ) {
		
			logger.info("Loading the regular needy cases");
			this.needyCaseDataModel = new NeedyCaseDataModel();
			this.needyCaseDataModel.setNeedyCaseProvider(new NeedyCaseProvider( 
					NeedyCaseTypes.REGULAR, NeedyCaseStatusTypes.ACTIVE, NeedyCaseStatusTypes.BLOCKED));
			this.needyCaseExportModel = new NeedyCaseExportModel();
			this.needyCaseExportModel.setNeedyCaseTypeValue(NeedyCaseTypes.REGULAR.getType());
		}
		
		return this.needyCaseDataModel;
	}
	
	public NeedyCaseDataModel getIrregularNeedyCaseDataModel() {
		
		if ( this.needyCaseDataModel == null ) {
		
			logger.info("Loading the irregular needy cases");
			this.needyCaseDataModel = new NeedyCaseDataModel();
			this.needyCaseDataModel.setNeedyCaseProvider(new NeedyCaseProvider( 
					NeedyCaseTypes.IRREGULAR, NeedyCaseStatusTypes.ACTIVE, NeedyCaseStatusTypes.BLOCKED));
			this.needyCaseExportModel = new NeedyCaseExportModel();
			this.needyCaseExportModel.setNeedyCaseTypeValue(NeedyCaseTypes.IRREGULAR.getType());
		}
		
		return this.needyCaseDataModel;
	}

	public void exportNeedyCases() {
		
		File destFile = null;
		try {
		
			logger.info("Exporting the needy cases to excel file");
		
			destFile = new File("c:/tempGoodSeasonsFolder", "exportedExcelFile.xls");
			destFile.delete();
			this.needyCaseService.exportNeedyCases(destFile, this.needyCaseExportModel.getNeedyCaseType(), NeedyCaseStatusTypes.ACTIVE);
			logger.info("The needy cases exported successfully");
			super.addMessage("needyCase_export_exportFinishedSuccessfully", FacesMessage.SEVERITY_INFO);
			
			this.writeOutContent((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(), 
					destFile, destFile.getName());
			FacesContext.getCurrentInstance().responseComplete();
			
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to add/update the regular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to add/update the regular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to add/update the regular needy case", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
	}
	
	public SimpleSelection getSelection() {
		return selection;
	}

	public void setSelection(SimpleSelection selection) {
		this.selection = selection;
	}

	public List<WebNeedyCase> getSelectedNeedyCases() {
		
		if ( this.selectedNeedyCases == null ) {
			
			this.selectedNeedyCases = new ArrayList<WebNeedyCase>();
		}
		
		return this.selectedNeedyCases;
	}

	public void setSelectedNeedyCases(List<WebNeedyCase> selectedNeedyCases) {
		this.selectedNeedyCases = selectedNeedyCases;
	}

	public String takeSelection() {
		getSelectedNeedyCases().clear();
		Iterator<Object> iterator = getSelection().getKeys();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			this.needyCaseDataModel.setRowKey(key);
			
			selectedNeedyCases.add((WebNeedyCase) this.needyCaseDataModel.getRowData());
		}
		return null;
	}
	
	public String getTableState() {
		if (tableState == null) {
			// try to get state from cookies
			Cookie[] cookies = ((HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest())
					.getCookies();
			if (cookies != null) {
				for (Cookie c : cookies) {
					if (c.getName().equals("extdtSampleTabelState")) {
						tableState = c.getValue();
						break;
					}
				}
			}
		}
		return tableState;
	}

	public void setTableState(String tableState) {
		
		this.tableState = tableState;
		// save state in cookies
		Cookie stateCookie = new Cookie("extdtSampleTabelState",
				this.tableState);
		stateCookie.setMaxAge(30 * 24 * 60 * 60);
		((HttpServletResponse) FacesContext.getCurrentInstance()
				.getExternalContext().getResponse()).addCookie(stateCookie);
	}

	public boolean getCanConvertToRegularNeedyCase() {
		
		return super.isUserHasPrivilege(PrivilegesEnum.NEEDYCASE_IRREGULAR_CONVERTTOREGULAR);
	}
	
	public boolean getCanDeleteIrregularNeedyCase() {
		
		return super.isUserHasPrivilege(PrivilegesEnum.NEEDYCASE_IRREGULAR_DELETE_NEEDYCASE);
	}
	
	public boolean getCanDeleteRegularNeedyCase() {
		
		return super.isUserHasPrivilege(PrivilegesEnum.NEEDYCASE_REGULAR_DELETE_NEEDYCASE);
	}
	
	public NeedyCaseService getNeedyCaseService() {
		return needyCaseService;
	}
	
	public void setNeedyCaseService(NeedyCaseService needyCaseService) {
		this.needyCaseService = needyCaseService;
	}

	@Override
	public int getPageSize() {
		
		return 10;
	}

	public void setMenuBean(MenuBean menuBean) {
		this.menuBean = menuBean;
	}

	public NeedyCaseExportModel getNeedyCaseExportModel() {
		return needyCaseExportModel;
	}

	
}
