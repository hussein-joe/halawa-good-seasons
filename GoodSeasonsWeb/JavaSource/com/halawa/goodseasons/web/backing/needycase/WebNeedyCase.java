package com.halawa.goodseasons.web.backing.needycase;

import java.io.Serializable;

public class WebNeedyCase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7494052237819554954L;
	private Long id;
	private String fullName;
	private String nationalId;
	private String maritalStatus;
	private short familyPersons;
	private short status;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getNationalId() {
		return nationalId;
	}
	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public short getFamilyPersons() {
		return familyPersons;
	}
	public void setFamilyPersons(short familyPersons) {
		this.familyPersons = familyPersons;
	}
	public short getStatus() {
		return status;
	}
	public void setStatus(short status) {
		this.status = status;
	}
}
