package com.halawa.goodseasons.service.model;

import java.util.ArrayList;
import java.util.List;

public class PendingNeedyCaseBenefit extends NeedyCaseBenefit {

	private List<PendingCampaignAction> pendingCampaignActions = new ArrayList<PendingCampaignAction>();

	public List<PendingCampaignAction> getPendingCampaignActions() {
		return pendingCampaignActions;
	}

	public void setPendingCampaignActions(
			List<PendingCampaignAction> pendingCampaignActions) {
		this.pendingCampaignActions = pendingCampaignActions;
	}
	
	public void addPendingCampaignAction(PendingCampaignAction pendingCampaignAction) {
		
		this.pendingCampaignActions.add(pendingCampaignAction);
	}
}
