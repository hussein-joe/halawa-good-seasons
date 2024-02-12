package com.halawa.goodseasons.web.backing.needycase;

import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.PrivilegesEnum;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.model.NeedyCaseSearchCriteria;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.service.NeedyCaseService;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.backing.common.MenuBean;

public class SearchNeedyCaseBean extends AbstractBean {

	public static final String PRIV_VIEW_NEEDYCASE_BENEFITS_REPORT = "";
	public static final String PRIV_CONVERT_TO_REGULAR_NEEDYCASE = "";
	
	private static final HalawaLogger logger = HalawaLogger.getInstance(SearchNeedyCaseBean.class);
	private NeedyCaseSearchCriteria searchCriteria = new NeedyCaseSearchCriteria();
	private List<NeedyCase> needyCases;
	private MenuBean menuBean;
	private NeedyCaseService needyCaseService;
	
	public String initNeedyCaseSearch() {
		
		String dest = this.menuBean.goToDest();
		if ( dest.equals( MenuBean.FORWARD_LOGIN ) ) {
			
			return dest;
		}
		
		this.searchCriteria = new NeedyCaseSearchCriteria();
		return dest;
	}
	public String searchNeedyCase() {
	
		try {
			this.needyCases = this.needyCaseService.searchNeedyCases(searchCriteria);
			return "viewSearchNeedyCasesResult";
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to search for needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to search for needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to search for needy case", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return null;
	}

	public boolean getCanViewNeedyCaseBenefitsReport() {
		
		return this.isUserHasPrivilege(PrivilegesEnum.REPORTS_CAMPAIGN_VIEW_NEEDYCASE_BENEFITS);
	}
	
	public boolean getCanConvertToRegularNeedyCase() {
		
		return this.isUserHasPrivilege(PrivilegesEnum.NEEDYCASE_IRREGULAR_CONVERTTOREGULAR);
	}
	
	public NeedyCaseSearchCriteria getSearchCriteria() {
		return searchCriteria;
	}
	public List<NeedyCase> getNeedyCases() {
		return needyCases;
	}
	public void setMenuBean(MenuBean menuBean) {
		this.menuBean = menuBean;
	}

	public void setNeedyCaseService(NeedyCaseService needyCaseService) {
		this.needyCaseService = needyCaseService;
	}
}
