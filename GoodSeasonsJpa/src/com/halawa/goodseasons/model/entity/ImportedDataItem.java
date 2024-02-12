package com.halawa.goodseasons.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The entity which will be used to import data into needy case table
 * @author Hussein
 *
 */
@Entity
@Table(name = "RodElFarag")
public class ImportedDataItem {

	private Long id;
	private int familyPersons;
	private String name;
	private String smartCardNumber;
	private String nationalId;
	private int sector;
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "family_persons")
	public int getFamilyPersons() {
		return familyPersons;
	}
	public void setFamilyPersons(int familyPersons) {
		this.familyPersons = familyPersons;
	}
	
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "smart_card_num")
	public String getSmartCardNumber() {
		return smartCardNumber;
	}
	public void setSmartCardNumber(String smartCardNumber) {
		this.smartCardNumber = smartCardNumber;
	}
	
	@Column(name = "national_id")
	public String getNationalId() {
		return nationalId;
	}
	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}
	
	@Column(name = "sector")
	public int getSector() {
		return sector;
	}
	public void setSector(int sector) {
		this.sector = sector;
	}
	
	
}
