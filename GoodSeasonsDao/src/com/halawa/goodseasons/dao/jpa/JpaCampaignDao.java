package com.halawa.goodseasons.dao.jpa;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.halawa.goodseasons.common.constants.CampaignActionStatusType;
import com.halawa.goodseasons.common.constants.CampaignType;
import com.halawa.goodseasons.common.constants.NeedyCaseStatusTypes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.dao.BaseDao;
import com.halawa.goodseasons.dao.CampaignDao;
import com.halawa.goodseasons.dao.PaginationData;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignAction;
import com.halawa.goodseasons.model.entity.CampaignItemType;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.User;

public class JpaCampaignDao extends BaseDao implements CampaignDao {

	public void addCampaign(Campaign campaign) {
		
		this.entityManager.persist(campaign);
	}

	@SuppressWarnings("unchecked")
	public List<Campaign> loadActiveCampiagns(CampaignType campaignType, NeedyCaseTypes... caseTypes) {
		
		Query query = this.entityManager.createNamedQuery("loadCampiagnsWithStatusAndCaseType");
		query.setParameter("caseTypes", getNeedyCaseTypeValues(caseTypes));
		query.setParameter("campaignStatus", ((short) 1) );
		query.setParameter("campaignType", campaignType.getType());
		
		return query.getResultList();
	}
	
	
	
	public List<Campaign> loadActiveCampiagns(NeedyCaseTypes... caseTypes) {
		
		return this.loadActiveCampiagns(CampaignType.REGULAR_CAMPAIGN, caseTypes);
	}

	@SuppressWarnings("unchecked")
	public List<Campaign> loadActiveCampiagns() {
		
		Query query = this.entityManager.createNamedQuery("loadCampiagnsWithStatus");
		query.setParameter("campaignStatus", ((short) 1) );
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CampaignItemType> loadCampaignItemTypes(NeedyCaseTypes... caseTypes) {
		
		Query query = this.entityManager.createNamedQuery("loadCampaignItemTypes");
		query.setParameter("campaignItemTypes", getNeedyCaseTypeValues(caseTypes));
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignItemType> loadDefaultCampaignItemTypes(NeedyCaseTypes... caseTypes) {
		
		Query query = this.entityManager.createNamedQuery("loadDefaultCampaignItemTypes");
		query.setParameter("campaignItemTypes", getNeedyCaseTypeValues(caseTypes));
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Campaign> loadClosedCampaigns(PaginationData paginationData, NeedyCaseTypes...caseTypes) {
		
		Query query = this.entityManager.createNamedQuery("loadClosedCampiagns");
		
		query.setParameter("campaignStatus", ((short) 2) );
		
		if ( paginationData != null ) {
			
			query.setFirstResult(paginationData.getStartIndex());
			if ( paginationData.getPageSize() > 0 ) {
				
				query.setMaxResults(paginationData.getPageSize());
			}
		}
		
		return query.getResultList();
	}

	public CampaignAction loadNeedyCaseCampaignAction(Campaign campaign, NeedyCase needyCase) {
		
		Query query = this.entityManager.createNamedQuery("loadNeedyCaseCampaignAction");
		query.setParameter("needyCase", needyCase);
		query.setParameter("campaign", campaign);
		
		try {
			return (CampaignAction) query.getSingleResult();
		} catch(NoResultException exception) {
			
			return null;
		}
	}
	
	public CampaignAction loadNeedyCaseCampaignAction(Campaign campaign, NeedyCase needyCase, CampaignActionStatusType actionStatusType) {
		
		
		Query query = this.entityManager.createNamedQuery("loadNeedyCaseCampaignActionWithStatus");
		query.setParameter("needyCase", needyCase);
		query.setParameter("campaign", campaign);
		query.setParameter("actionStatusTypeValue", actionStatusType.getValue());
		
		
		try {
			return (CampaignAction) query.getSingleResult();
		} catch(NoResultException exception) {
			
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<CampaignAction> loadCampaignActions(Campaign campaign, PaginationData paginationData) {
		
		Query query = this.entityManager.createNamedQuery("loadCampaignActions");
		query.setParameter("campaign", campaign);
		
		if ( paginationData != null ) {
			
			query.setFirstResult(paginationData.getStartIndex());
			if ( paginationData.getPageSize() > 0 ) {
				
				query.setMaxResults(paginationData.getPageSize());
			}
		}
		
		return query.getResultList();
	}

	
	@SuppressWarnings("unchecked")
	public Map<CampaignItemType, Double> countCampaignActionsWithItems(Campaign campaign, 
			CampaignActionStatusType... actionStatusTypes) {
		
		Map<CampaignItemType, Double> result = new HashMap<CampaignItemType, Double>();
		String actionStatusString = String.valueOf(actionStatusTypes[0].getValue());
		for(int i=1; i<actionStatusTypes.length;i++) {
			actionStatusString += "," + String.valueOf(actionStatusTypes[i].getValue());
		}
		Query query = this.entityManager.createNativeQuery("SELECT camp_action_item.item_type_id, SUM(camp_action_item.quantity) FROM Campaign_Action_Item as camp_action_item, Campaign_Action as camp_action " +
				" WHERE camp_action_item.campaign_action_id=camp_action.id AND camp_action.campaign_id=" + campaign.getId() + 
				" AND camp_action.status IN (" + actionStatusString + ")" + 
				" GROUP BY camp_action_item.item_type_id");
		
		List<Object[]> queryResult = query.getResultList();
		for(Object[] recordResult: queryResult) {
			result.put(entityManager.find(CampaignItemType.class, recordResult[0]), Double.valueOf(recordResult[1].toString()));
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignAction> loadCampaignActionsWithItems(Campaign campaign, PaginationData paginationData, 
			CampaignActionStatusType... actionStatusTypes) {
		
		Query query = this.entityManager.createNamedQuery("loadCampaignActionsWithItems");
		query.setParameter("campaign", campaign);
		query.setParameter("campaignActionStatusValues", getCampaignActionsStatusValues(actionStatusTypes));
		
		if ( paginationData != null ) {
			
			query.setFirstResult(paginationData.getStartIndex());
			if ( paginationData.getPageSize() > 0 ) {
				
				query.setMaxResults(paginationData.getPageSize());
			}
		}
		
		return query.getResultList();
	}

	public Campaign loadCampaignWithItemsById(Long campaignId) {
		
		
		Query query = this.entityManager.createNamedQuery("loadCampaignWithItemsById");
		query.setParameter("campaignId", campaignId);
		
		try {
			return (Campaign) query.getSingleResult();
		} catch(NoResultException exception) {
			
			return null;
		}
	}

	public void updateCampaign(Campaign campaign) {
		
		this.entityManager.merge(campaign);
	}

	public void addCampaignAction(CampaignAction campaignAction) {
		
		this.entityManager.persist(campaignAction);
	}

	public int countCampaignActions(Campaign campaign, CampaignActionStatusType... actionStatusTypes) {
		
		
		
		Query query = this.entityManager.createNamedQuery("countCampaignActionsWithStatusType");
		query.setParameter("campaign", campaign);
		query.setParameter("campaignActionStatusValues", getCampaignActionsStatusValues(actionStatusTypes));
		
		return ((Number) query.getSingleResult()).intValue();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Object[]> loadCampaingPendingNeedyCasesIds(Campaign campaign) {
		
		Query query = this.entityManager.createNativeQuery("SELECT nc.id, rnc.monthly_amount, rnc.last_benefit_date FROM "+
				" Needy_Case AS nc inner join Regular_Case as rnc on rnc.id=nc.id AND nc.status=" + NeedyCaseStatusTypes.ACTIVE.getValue() + " AND nc.id not in "+
				"(SELECT case_id FROM Campaign_Action WHERE campaign_id=" + campaign.getId() + ") AND nc.type=1 ORDER BY nc.id");
		
		return (List<Object[]>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<BigInteger> loadCampaignActionsNeedyCasesIds(Campaign campaign, CampaignActionStatusType... actionStatusTypes) {
		
		String campaignActionStatusValues = getCampaignActionsStatusValues(actionStatusTypes).toString();
		campaignActionStatusValues = campaignActionStatusValues.substring(1, campaignActionStatusValues.length()-1);
		
		Query query = this.entityManager.createNativeQuery("SELECT case_id FROM Campaign_Action WHERE campaign_id=" + campaign.getId() 
				+ " AND status IN(" + campaignActionStatusValues + ") ORDER BY case_id");
		
		return (List<BigInteger>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignAction> loadNeedyCaseActionsFetchCampaignAndActionItems(NeedyCase needyCase, CampaignActionStatusType actionStatusType) {
		
		Query query = this.entityManager.createNamedQuery("loadNeedyCaseActionsFetchCampaignAndActionItems");
		query.setParameter("needyCase", needyCase);
		query.setParameter("actionStatusValue", actionStatusType.getValue());
		
		return query.getResultList();
	}
	
	public void updateAllPendingCampaignActions(NeedyCase needyCase, CampaignActionStatusType newActionStatus) {
		
		Query query = this.entityManager.createNamedQuery("refuseAllPendingCampaignActions");
		query.setParameter("needyCase", needyCase);
		query.setParameter("newActionStatusValue", newActionStatus.getValue());
		query.setParameter("pendingActionStatusValue", CampaignActionStatusType.PENDING.getValue());
		
		query.executeUpdate();
	}
	
	public void updatePendingCampaignActionsForCampaigns(NeedyCase needyCase, List<Campaign> campaigns, CampaignActionStatusType newActionStatus) {
		
		Query query = this.entityManager.createNamedQuery("updatePendingCampaignActionsForCampaigns");
		query.setParameter("needyCase", needyCase);
		query.setParameter("newActionStatusValue", newActionStatus.getValue());
		query.setParameter("pendingActionStatusValue", CampaignActionStatusType.PENDING.getValue());
		query.setParameter("campaigns", campaigns);
		
		query.executeUpdate();
	}
	
	public CampaignAction loadCampaignActionFetchItemsById(Long campaignActionId) {
		
		Query query = this.entityManager.createNamedQuery("loadCampaignActionFetchItemsById");
		query.setParameter("campaignActionId", campaignActionId);
		
		try {
			return (CampaignAction) query.getSingleResult();
		} catch(NoResultException exp) {
			
			return null;
		}
	}

	public long addCampaignActionUsingData(User actor, long needCaseId, Date addingDate, long campaignId, CampaignActionStatusType statusType) {
		
		Query query = this.entityManager.createNativeQuery("INSERT INTO Campaign_Action(case_id, adding_date, campaign_id, user_id, status) VALUES(?,getdate(),?,?,?)");
		query.setParameter(1, needCaseId);
		query.setParameter(2, campaignId);
		query.setParameter(3, actor.getId());
		query.setParameter(4, statusType.getValue());
		
		query.executeUpdate();
		query = this.entityManager.createNativeQuery("SELECT id FROM Campaign_Action WHERE case_id=? AND campaign_id=? and status=? AND user_id=?");
		query.setParameter(1, needCaseId);
		query.setParameter(2, campaignId);
		query.setParameter(3, statusType.getValue());
		query.setParameter(4, actor.getId());
		List result = query.getResultList();
		long last_id = -1;
		if (result.size() >= 2) {
			last_id = Long.parseLong(result.get(0).toString());
		} else {
			last_id = Long.parseLong(result.get(0).toString());
		}
		return last_id;
	}
	public void addCampaingActionItemUsingData(User actor, long campaingActionId, long itemTypeId, double quantity){
		Query query = this.entityManager.createNativeQuery("INSERT INTO Campaign_Action_Item(campaign_action_id, item_type_id, quantity) VALUES(?,?,?)");
		query.setParameter(1, campaingActionId);
		query.setParameter(2, itemTypeId);
		query.setParameter(3, quantity);
		
		query.executeUpdate();
	}
	
	public void forgetExceptionalCampaignActions(User actor, Campaign campaign){
		Query query = this.entityManager.createNativeQuery("UPDATE Campaign_Action SET status=? where campaign_id=?");
		query.setParameter(1, CampaignActionStatusType.EXCEPTIONAL_CAMPAIGN_FORGOT_ACTION.getValue());
		query.setParameter(2, campaign.getId());
		
		query.executeUpdate();
		
		query = this.entityManager.createNativeQuery("UPDATE Campaign SET start_date=? where id=?");
		query.setParameter(1, new Date());
		query.setParameter(2, campaign.getId());
		
		query.executeUpdate();
	}
	
	
	private List<Short> getCampaignActionsStatusValues(CampaignActionStatusType...actionStatusTypes) {
		
		List<Short> actionStatusTypeValues = new ArrayList<Short>();
		for(CampaignActionStatusType statusType: actionStatusTypes) {
			
			actionStatusTypeValues.add( statusType.getValue() );
		}
		
		return actionStatusTypeValues;
	}
	
	public void updateRegularNeedyCaseLastBenefitDate(long needyCaseId, Date lastBenefitDate) {
		
		Query query = this.entityManager.createNativeQuery("UPDATE Regular_Case SET last_benefit_date=? where id=?");
		query.setParameter(1, lastBenefitDate);
		query.setParameter(2, needyCaseId);
		
		query.executeUpdate();
	}
}
