package com.halawa.goodseasons.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.dao.DataImporterDao;
import com.halawa.goodseasons.dao.NeedyCaseDao;
import com.halawa.goodseasons.model.entity.ImportedDataItem;
import com.halawa.goodseasons.model.entity.IrregularCase;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.service.ImportDataService;

public class ImportDataServiceImpl implements ImportDataService {

	private static final HalawaLogger logger = HalawaLogger.getInstance(ImportDataServiceImpl.class);
	private DataImporterDao dataImporterDao;
	private NeedyCaseDao needyCaseDao;
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void importData() throws Exception{
		
		int startIndex = 0;
		int pageSize = 1000;
		Calendar defaultDate;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
			defaultDate = Calendar.getInstance();
			defaultDate.set(Calendar.YEAR, 1990);
			defaultDate.set(Calendar.MONTH, 0);
			defaultDate.set(Calendar.DAY_OF_MONTH, 1);
			logger.info("Start importingggggggggggggggggggggggggggggg");
			
			while (true) {
				
				List<ImportedDataItem> dataItems = this.dataImporterDao.loadNextItems(startIndex, pageSize);
				if ( dataItems == null || dataItems.isEmpty() || startIndex > 2000) 
					break;
				for(ImportedDataItem item: dataItems) {
					
					NeedyCase needyCase = this.generateNeedyCase(item, dateFormat, defaultDate);
					this.needyCaseDao.addNeedyCase(needyCase);
				}
				
				startIndex += pageSize;
			}
		} catch(Exception exception) {
			
			logger.error("Failed to import data", exception);
			throw exception;
		}
		logger.info("Finished importing dataaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
	}

	private NeedyCase generateNeedyCase(ImportedDataItem dataItem, SimpleDateFormat dateFormat, Calendar defaultDate) throws Exception{
		
		String nationalId = dataItem.getNationalId();
		NeedyCase needyCase = new IrregularCase();
		needyCase.setFamilyPersons( Integer.valueOf(dataItem.getFamilyPersons()).shortValue() );
		needyCase.setFullName( dataItem.getName() );
		needyCase.setMaritalStatus("1");
		needyCase.setFullNationalId(nationalId);
		needyCase.setSectorNumber( dataItem.getSector() );
		needyCase.setSmartCardNumber( dataItem.getSmartCardNumber() );
		if ( nationalId.startsWith("2") ) {
			
			needyCase.setDateOfBirth( dateFormat.parse( nationalId.substring(1, 7) ) );
			needyCase.setNationalId( nationalId.substring(7) );
		} else {
			
			needyCase.setDateOfBirth( defaultDate.getTime() );
			needyCase.setNationalId( nationalId );
		}
		
		return needyCase;
	}

	public void setDataImporterDao(DataImporterDao dataImporterDao) {
		this.dataImporterDao = dataImporterDao;
	}

	public void setNeedyCaseDao(NeedyCaseDao needyCaseDao) {
		this.needyCaseDao = needyCaseDao;
	}
	
	
}
