package com.halawa.goodseasons.model.entity;

//Generated Sep 14, 2010 1:35:07 PM by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * CampaignActionItem generated by hbm2java
 */
@Entity
@Table(name = "Campaign_Action_Item")
public class CampaignActionItem implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7021293340696305087L;
	private Long id;
	private CampaignAction campaignAction;
	private CampaignItemType campaignItemType;
	private double quantity;
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "campaign_action_id", nullable = false)
	public CampaignAction getCampaignAction() {
		return this.campaignAction;
	}

	public void setCampaignAction(CampaignAction campaignAction) {
		this.campaignAction = campaignAction;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item_type_id", nullable = false)
	public CampaignItemType getCampaignItemType() {
		return this.campaignItemType;
	}

	public void setCampaignItemType(CampaignItemType campaignItemType) {
		this.campaignItemType = campaignItemType;
	}

	@Column(name = "quantity", nullable = false, precision = 53, scale = 0)
	public double getQuantity() {
		return this.quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((campaignItemType == null) ? 0 : campaignItemType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampaignActionItem other = (CampaignActionItem) obj;
		if (campaignItemType == null) {
			if (other.campaignItemType != null)
				return false;
		} else if (!campaignItemType.equals(other.campaignItemType))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
