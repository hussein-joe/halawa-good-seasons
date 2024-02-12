package com.halawa.goodseasons.service.model;

import java.io.Serializable;
import java.util.List;

import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.User;

/**
 * The criteria model which will be used to fill the criteria which will be 
 * used to generate report, this report will be used to define the campaign 
 * actions done by specific user
 * @author Hussein
 *
 */
public class CampaignActionsUserReportCriteria implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8149418841598974758L;
	/**
	 * The user to be used to generate report, it is mandatory field
	 */
	private User selectedUser;
	private List<Campaign> selectedCampaigns;
	/**
	 * The number of days which should be included in the returned report.
	 */
	private int numberOfDays;
	
	public User getSelectedUser() {
		return selectedUser;
	}
	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}
	public List<Campaign> getSelectedCampaigns() {
		return selectedCampaigns;
	}
	public void setSelectedCampaigns(List<Campaign> selectedCampaigns) {
		this.selectedCampaigns = selectedCampaigns;
	}
	public int getNumberOfDays() {
		return numberOfDays;
	}
	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}
	
	
}
