package com.halawa.goodseasons.model.entity;

// Generated Jul 25, 2010 11:25:21 AM by Hibernate Tools 3.2.5.Beta

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.halawa.goodseasons.common.constants.CampaignType;

/**
 * Campaign generated by hbm2java
 */
@Entity
@Table(name = "Campaign")
public class Campaign implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7475572758474231898L;
	private Long id;
	private String name;
	private Date addingDate;
	private Date startDate;
	private Date endDate;
	private Integer maxCases;
	private short caseType;
	private short campaignType = CampaignType.REGULAR_CAMPAIGN.getType();
	private short status = 1;
	private Set<CampaignAction> campaignActions = new HashSet<CampaignAction>(0);
	private Set<CampaignItem> campaignItems = new HashSet<CampaignItem>(0);

	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false, length = 200)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	@Column(name = "adding_date", length = 23, insertable=false, updatable=false)
	public Date getAddingDate() {
		return this.addingDate;
	}

	public void setAddingDate(Date addingDate) {
		this.addingDate = addingDate;
	}

	
	@Column(name = "start_date", length = 23)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	
	@Column(name = "end_date", length = 23)
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "max_cases")
	public Integer getMaxCases() {
		return this.maxCases;
	}

	public void setMaxCases(Integer maxCases) {
		this.maxCases = maxCases;
	}

	@Column(name = "case_type", nullable = false)
	public short getCaseType() {
		return this.caseType;
	}

	public void setCaseType(short caseType) {
		this.caseType = caseType;
	}
	
	
	@Column(name = "campaign_type", nullable = false)
	public short getCampaignType() {
		return campaignType;
	}

	public void setCampaignType(short campaignType) {
		this.campaignType = campaignType;
	}

	@Column(name = "status", insertable=false, updatable=true)
	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "campaign")
	public Set<CampaignAction> getCampaignActions() {
		return this.campaignActions;
	}

	public void setCampaignActions(Set<CampaignAction> campaignActions) {
		this.campaignActions = campaignActions;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "campaign", cascade=CascadeType.ALL)
	public Set<CampaignItem> getCampaignItems() {
		return this.campaignItems;
	}

	public void setCampaignItems(Set<CampaignItem> campaignItems) {
		this.campaignItems = campaignItems;
	}

}
