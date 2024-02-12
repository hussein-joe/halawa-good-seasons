package com.halawa.goodseasons.dao;

public class PaginationData {

	private int startIndex;
	private int pageSize;
	
	
	public PaginationData(int startIndex, int pageSize) {
		super();
		this.startIndex = startIndex;
		this.pageSize = pageSize;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	
}
