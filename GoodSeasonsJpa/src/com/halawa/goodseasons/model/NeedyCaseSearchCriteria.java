package com.halawa.goodseasons.model;

public class NeedyCaseSearchCriteria {

	private String nationalId;
	private short maritalStatus;
	private short type;
	private int minMonthlyAmount;
	private int maxMonthlyAmount;
	private short minFamilyPersons;
	private short maxFamilyPersons;
	private String name;
	private String oldCardNumber;
	
	public String getOldCardNumber() {
		return oldCardNumber;
	}
	public void setOldCardNumber(String oldCardNumber) {
		this.oldCardNumber = oldCardNumber;
	}
	public String getNationalId() {
		return nationalId;
	}
	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}
	public short getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(short maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public int getMinMonthlyAmount() {
		return minMonthlyAmount;
	}
	public void setMinMonthlyAmount(int minMonthlyAmount) {
		this.minMonthlyAmount = minMonthlyAmount;
	}
	public int getMaxMonthlyAmount() {
		return maxMonthlyAmount;
	}
	public void setMaxMonthlyAmount(int maxMonthlyAmount) {
		this.maxMonthlyAmount = maxMonthlyAmount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public short getMinFamilyPersons() {
		return minFamilyPersons;
	}
	public void setMinFamilyPersons(short minFamilyPersons) {
		this.minFamilyPersons = minFamilyPersons;
	}
	public short getMaxFamilyPersons() {
		return maxFamilyPersons;
	}
	public void setMaxFamilyPersons(short maxFamilyPersons) {
		this.maxFamilyPersons = maxFamilyPersons;
	}
	@Override
	public String toString() {
		return "NeedyCaseSearchCriteria [Name=" + name
				+ ", maritalStatus=" + maritalStatus
				+ ", maxFamilyPersons=" + maxFamilyPersons
				+ ", maxMonthlyAmount=" + maxMonthlyAmount
				+ ", minFamilyPersons=" + minFamilyPersons
				+ ", minMonthlyAmount=" + minMonthlyAmount + ", nationalId="
				+ nationalId + ", type=" + type + "]";
	}
}
