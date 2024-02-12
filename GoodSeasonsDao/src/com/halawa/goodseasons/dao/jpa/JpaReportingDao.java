package com.halawa.goodseasons.dao.jpa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.halawa.goodseasons.common.constants.CampaignActionStatusType;
import com.halawa.goodseasons.dao.BaseDao;
import com.halawa.goodseasons.dao.ReportingDao;
import com.halawa.goodseasons.dao.model.CampaignItemTotal;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignAction;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.User;

public class JpaReportingDao extends BaseDao implements ReportingDao {

	public int countUserCampaignActions(User user, Campaign campaign, int numberOfDays, CampaignActionStatusType... actionStatusTypes) {
		
		Query query;
		if ( numberOfDays > 0 ) {
			Calendar calendar = this.getStartDate();
			query = this.entityManager.createNamedQuery("countUserCampaignActionsInPeriod");
			query.setParameter("endDate", calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, -1 * numberOfDays);
			query.setParameter("startDate", calendar.getTime());
		} else {
			
			query = this.entityManager.createNamedQuery("countUserCampaignActions");
		}
		
		query.setParameter("actionStatusValues", getCampaignActionStatusValues(actionStatusTypes));
		query.setParameter("user", user);
		query.setParameter("campaign", campaign);
		
		Number count = (Number) query.getSingleResult();
		return count.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<CampaignItemTotal> getUserCampaignActionItems(User user,
			Campaign campaign, int numberOfDays, CampaignActionStatusType... actionStatusTypes) {

		Query query;
		if ( numberOfDays > 0 ) {
			
			Calendar calendar = this.getStartDate();
			query = this.entityManager.createNamedQuery("getUserCampaignActionItemsInPeriod");
			query.setParameter("endDate", calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, -1 * numberOfDays);
			query.setParameter("startDate", calendar.getTime());
		} else {
			
			query = this.entityManager.createNamedQuery("getUserCampaignActionItems");
		}
		
		query.setParameter("actionStatusValues", getCampaignActionStatusValues(actionStatusTypes));
		query.setParameter("user", user);
		query.setParameter("campaign", campaign);
		
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Campaign> getUserCampaignsInPeriod(User user, int numberOfDays) {
		
		Query query;
		if ( numberOfDays > 0 ) {
			Calendar calendar = this.getStartDate();
			query = this.entityManager.createNamedQuery("getUserCampaignsInPeriod");
			query.setParameter("endDate", calendar.getTime(), TemporalType.DATE);
			calendar.add(Calendar.DAY_OF_MONTH, -1 * numberOfDays);
			System.out.println("Start date = " + calendar.getTime());
			query.setParameter("startDate", calendar.getTime(), TemporalType.DATE);
		} else {
			
			query = this.entityManager.createNamedQuery("getUserCampaigns");
			
		}
		
		query.setParameter("user", user);
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Campaign> getUserCampaignsInPeriod(
			List<Campaign> selectedCampaigns, User user, int numberOfDays) {
		
		Query query;
		if ( numberOfDays <= 0 ) {
			
			query = this.entityManager.createNamedQuery("getUserCampaignsUsingSelectedCampaigns");
		} else {
		
			Calendar calendar = this.getStartDate();
			query = this.entityManager.createNamedQuery("getUserCampaignsInPeriodUsingSelectedCampaigns");
			query.setParameter("endDate", calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, -1 * numberOfDays);
			query.setParameter("startDate", calendar.getTime());
		}
		
		query.setParameter("user", user);
		query.setParameter("selectedCampaigns", selectedCampaigns);
		
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<NeedyCase> loadNeedyCasesWithNationalId(String nationalId) {
		
		Query query = this.entityManager.createNamedQuery("loadNeedyCasesByNationalId_Report");
		query.setParameter("nationalId", nationalId);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignAction> loadNeedyCasesCampaignActions(List<NeedyCase> needyCases) {
		
		Query query = this.entityManager.createNamedQuery("loadNeedyCasesCampaignActions_Report");
		query.setParameter("needyCases", needyCases);
		
		return query.getResultList();
	}

	private Calendar getStartDate() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar;
	}
	private List<Short> getCampaignActionStatusValues(CampaignActionStatusType...actionStatusTypes) {
		
		List<Short> campaignActionStatusValues = new ArrayList<Short>();
		for(CampaignActionStatusType statusType: actionStatusTypes) {
			
			campaignActionStatusValues.add( statusType.getValue() );
		}
		
		return campaignActionStatusValues;
	}
}
