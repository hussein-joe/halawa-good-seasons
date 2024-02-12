package com.halawa.goodseasons.web.backing.needycase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.ajax4jsf.model.SerializableDataModel;

public class NeedyCaseDataModel extends SerializableDataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1407301676176470172L;
	private NeedyCaseProvider needyCaseProvider;
	private Long currentNeedyCaseKey;
	private Map<Long, WebNeedyCase> wrappedNeedyCasesData = new HashMap<Long, WebNeedyCase>();
	private List<Long> wrappedKeys;
	private Integer rowCount;
	
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getRowKey() {
		
		return this.currentNeedyCaseKey;
	}

	@Override
	public void setRowKey(Object arg0) {
		
		this.currentNeedyCaseKey = (Long) arg0;
	}

	@Override
	public void walk(FacesContext arg0, DataVisitor visitoe, Range range, Object arg3) throws IOException {
		
		int firstRow = ((SequenceRange)range).getFirstRow();
		int numberOfRows = ((SequenceRange)range).getRows();
		
		this.wrappedKeys = new ArrayList<Long>();
		
		List<WebNeedyCase> webNeedyCases = this.needyCaseProvider.getItemsByRange(firstRow, numberOfRows);
		for(WebNeedyCase webNeedyCase: webNeedyCases) {
			
			wrappedKeys.add( webNeedyCase.getId() );
			wrappedNeedyCasesData.put(webNeedyCase.getId(), webNeedyCase);
			visitoe.process(arg0, webNeedyCase.getId(), arg3);
		}
	}

	@Override
	public int getRowCount() {

		
		if ( this.rowCount == null ) {
			
			rowCount = new Integer(this.needyCaseProvider.getRowCount());
		}
		return rowCount.intValue();
	}

	@Override
	public Object getRowData() {
		
		if ( this.currentNeedyCaseKey == null )
			return null;
		
		WebNeedyCase webNeedyCase = this.wrappedNeedyCasesData.get(this.currentNeedyCaseKey);
		if ( webNeedyCase == null ) {
			
			
			webNeedyCase = this.needyCaseProvider.getItemByKey(this.currentNeedyCaseKey);
			if ( webNeedyCase == null ) {
				
				return null;
			}
		}
		
		return webNeedyCase;
	}

	@Override
	public int getRowIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getWrappedData() {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRowAvailable() {
		
		if ( this.currentNeedyCaseKey == null )
			return false;
		
		
		return this.needyCaseProvider.getItemByKey(currentNeedyCaseKey) != null;
	}

	@Override
	public void setRowIndex(int arg0) {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public void setWrappedData(Object arg0) {
		
		throw new UnsupportedOperationException();
	}

	public  SerializableDataModel getSerializableModel(Range range) {
		
		if ( this.wrappedKeys != null ) {
			
			return this;
		} else {
			
			return null;
		}
	}

	public void setNeedyCaseProvider(NeedyCaseProvider needyCaseProvider) {
		this.needyCaseProvider = needyCaseProvider;
	}
}
