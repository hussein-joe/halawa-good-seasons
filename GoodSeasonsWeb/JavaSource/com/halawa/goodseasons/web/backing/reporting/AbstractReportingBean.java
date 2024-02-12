package com.halawa.goodseasons.web.backing.reporting;

import com.halawa.goodseasons.service.ReportingService;
import com.halawa.goodseasons.web.backing.AbstractBean;

public abstract class AbstractReportingBean extends AbstractBean {

	protected ReportingService reportingService;

	public void setReportingService(ReportingService reportingService) {
		this.reportingService = reportingService;
	}
}
