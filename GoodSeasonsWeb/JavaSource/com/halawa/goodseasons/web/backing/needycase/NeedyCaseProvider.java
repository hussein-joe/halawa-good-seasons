package com.halawa.goodseasons.web.backing.needycase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.richfaces.model.DataProvider;
import org.springframework.beans.BeanUtils;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.goodseasons.common.constants.NeedyCaseStatusTypes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.dao.PaginationData;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.service.NeedyCaseService;

public class NeedyCaseProvider implements DataProvider<WebNeedyCase> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3133695169656416950L;

	private static final HalawaLogger logger = HalawaLogger.getInstance(NeedyCaseProvider.class);
	private NeedyCaseTypes needyCaseType;
	private NeedyCaseStatusTypes[] needyCaseStatusTypes;
	
	public NeedyCaseProvider(NeedyCaseTypes caseType, NeedyCaseStatusTypes...caseStatusTypes) {
		
		this.needyCaseType = caseType;
		this.needyCaseStatusTypes = caseStatusTypes;
	}
	
	public WebNeedyCase getItemByKey(Object arg0) {
		
		try {
			NeedyCase needyCase = this.getNeedyCaseService().loadNeedyCaseById( (Long) arg0 );
			WebNeedyCase webNeedyCase = new WebNeedyCase();
			BeanUtils.copyProperties(needyCase, webNeedyCase);
			return webNeedyCase;
		} catch(HalawaBusinessException exception) {
			
			logger.error("No needy case exist with national id " + arg0, exception);
		} catch(Exception exception) {
			
			logger.error("Failed to load the needy case with national id " + arg0);
		}
		return null;
	}
	
	public List<WebNeedyCase> getItemsByRange(int arg0, int arg1) {

		logger.info("Loading the needy cases starting from " + arg0 + ", where page size = " + arg1);
		List<WebNeedyCase> webNeedyCases = new ArrayList<WebNeedyCase>();
		List<? extends NeedyCase> needyCases = this.getNeedyCaseService().
			loadNeedyCases(needyCaseType, new PaginationData(arg0, arg1), needyCaseStatusTypes);
		
		for(NeedyCase needyCase: needyCases) {
			
			WebNeedyCase webNeedyCase = new WebNeedyCase();
			BeanUtils.copyProperties(needyCase, webNeedyCase, new String[]{"campaignActions", "caseAddress"});
			webNeedyCases.add(webNeedyCase);
		}
		
		return webNeedyCases; 
	}

	public Object getKey(WebNeedyCase arg0) {
		
		return arg0.getId();
	}

	public int getRowCount() {
		
		return this.getNeedyCaseService().countNeedyCases(needyCaseType, needyCaseStatusTypes);
	}
	
	public NeedyCaseService getNeedyCaseService() {
		
		ManageNeedyCasesBean manageNeedyCasesBean = (ManageNeedyCasesBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("manageNeedyCasesBean");
		
		return manageNeedyCasesBean.getNeedyCaseService();
	}
	
}
