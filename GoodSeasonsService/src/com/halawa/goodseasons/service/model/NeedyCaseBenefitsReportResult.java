package com.halawa.goodseasons.service.model;

import java.util.List;

import com.halawa.goodseasons.model.entity.CampaignAction;
import com.halawa.goodseasons.model.entity.NeedyCase;

public class NeedyCaseBenefitsReportResult {

	private NeedyCase needyCase;
	private List<CampaignAction> needyCaseBenefits;
	
	public NeedyCase getNeedyCase() {
		return needyCase;
	}
	public void setNeedyCase(NeedyCase needyCase) {
		this.needyCase = needyCase;
	}
	public List<CampaignAction> getNeedyCaseBenefits() {
		return needyCaseBenefits;
	}
	public void setNeedyCaseBenefits(List<CampaignAction> needyCaseBenefits) {
		this.needyCaseBenefits = needyCaseBenefits;
	}
}
