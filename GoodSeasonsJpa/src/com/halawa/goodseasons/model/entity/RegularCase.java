package com.halawa.goodseasons.model.entity;

// Generated Jul 25, 2010 11:25:21 AM by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * RegularCase generated by hbm2java
 */
@Entity
@Table(name = "Regular_Case")
@DiscriminatorValue("1")
public class RegularCase extends NeedyCase {
	
	public RegularCase() {
		
		super.setType((short) 1);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2411861308460297760L;
	private int monthlyAmount;
	/**
	 * The date of the last benefit added to this needy case.
	 * for example, if last campaign has 2 months, and this campaign started in 2-9-2010
	 * then when this needy case take its benefit, this column should be updated to become
	 * 2-10-2010, so any monthly campaign in month 10-2010, this needy case can't take benefit. 
	 */
	private Date lastBenefitDate;
	
	@Column(name = "monthly_amount", nullable = false, precision = 53, scale = 0)
	public int getMonthlyAmount() {
		return this.monthlyAmount;
	}

	public void setMonthlyAmount(int monthlyAmount) {
		this.monthlyAmount = monthlyAmount;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "last_benefit_date", nullable = true)
	public Date getLastBenefitDate() {
		return lastBenefitDate;
	}

	public void setLastBenefitDate(Date lastBenefitDate) {
		this.lastBenefitDate = lastBenefitDate;
	}
	
	
}