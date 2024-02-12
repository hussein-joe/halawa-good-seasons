package com.halawa.goodseasons.common.constants;

public enum PrivilegesEnum {

	NEEDYCASE_IRREGULAR_DELETE_NEEDYCASE("needyCases_irregular_delete", PrivilegeType.USER_PRIVILEGE),
	NEEDYCASE_REGULAR_DELETE_NEEDYCASE("needyCases_regular_delete", PrivilegeType.USER_PRIVILEGE),
	
	NEEDYCASE_REGULAR_EDIT_MONTHLYAMOUNT("needyCases_regular_editMonthlyAmount", PrivilegeType.USER_PRIVILEGE),
	NEEDYCASE_IRREGULAR_EDIT_NATIONALID("needyCases_irregular_editNationalId", PrivilegeType.USER_PRIVILEGE),
	NEEDYCASE_IRREGULAR_CONVERTTOREGULAR("needyCases_irregular_convertToRegular", PrivilegeType.USER_PRIVILEGE),
	CAMPAIGN_BENEFITS_DELIVERPENDINGACTIONS("campaign_benefits_deliverPendingBenefits", PrivilegeType.USER_PRIVILEGE),
	REPORTS_CAMPAIGN_VIEW_NEEDYCASE_BENEFITS("reports_campaign_viewNeedyCaseBenefits", PrivilegeType.USER_PRIVILEGE);
	
	private String privilegeLocalName;
	private PrivilegeType privilegeType;
	
	private PrivilegesEnum(String localName, PrivilegeType privilegeType) {
		
		this.privilegeLocalName = localName;
		this.privilegeType = privilegeType;
	}
	
	public String getPrivilegeLocalName() {
		return privilegeLocalName;
	}
	public PrivilegeType getPrivilegeType() {
		return privilegeType;
	}
}
