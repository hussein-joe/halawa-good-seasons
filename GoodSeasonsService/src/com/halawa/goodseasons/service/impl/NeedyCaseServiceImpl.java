package com.halawa.goodseasons.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.CampaignActionStatusType;
import com.halawa.goodseasons.common.constants.CampaignErrorCodes;
import com.halawa.goodseasons.common.constants.CampaignType;
import com.halawa.goodseasons.common.constants.NeedyCaseErrorCodes;
import com.halawa.goodseasons.common.constants.NeedyCaseStatusTypes;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.GoodSeasonsMessageBundle;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.dao.NeedyCaseDao;
import com.halawa.goodseasons.dao.PaginationData;
import com.halawa.goodseasons.model.NeedyCaseSearchCriteria;
import com.halawa.goodseasons.model.entity.Campaign;
import com.halawa.goodseasons.model.entity.CampaignAction;
import com.halawa.goodseasons.model.entity.CampaignItem;
import com.halawa.goodseasons.model.entity.IrregularCase;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.RegularCase;
import com.halawa.goodseasons.service.CampaignService;
import com.halawa.goodseasons.service.ConfigurationService;
import com.halawa.goodseasons.service.NeedyCaseService;
import com.halawa.goodseasons.service.model.NeedyCaseBenefit;

public class NeedyCaseServiceImpl implements NeedyCaseService {

	private static final HalawaLogger logger = HalawaLogger.getInstance(NeedyCaseServiceImpl.class);
	
	private NeedyCaseDao needyCaseDao;
	private CampaignService campaignService;
	private ConfigurationService configurationService;
	
	public NeedyCase loadNeedyCaseByOldNumber(String oldNumber) throws HalawaBusinessException {
		
		NeedyCase needyCase = this.needyCaseDao.loadNeedyCaseByOldNumber(oldNumber);
		if ( needyCase == null ) {
			
			logger.error("No needy case exist with old number " + oldNumber);
			throw new HalawaBusinessException(NeedyCaseErrorCodes.NEEDYCASE_IS_NOT_EXIST.getErrorCode());
		}
		
		return needyCase;
	}

	public NeedyCase loadNeedyCaseByNationalId(String nationalId) throws HalawaBusinessException {
		
		NeedyCase needyCase = this.needyCaseDao.loadNeedyCaseByNationalId(nationalId.trim());
		if ( needyCase == null ) {
			
			logger.error("No needy case exist with national id " + nationalId);
			throw new HalawaBusinessException(NeedyCaseErrorCodes.NEEDYCASE_IS_NOT_EXIST.getErrorCode());
		}
		
		return needyCase;
	}
	
	public NeedyCase loadNeedyCaseById(Long id) throws HalawaBusinessException {
		
		return this.needyCaseDao.loadNeedyCaseById(id);
	}



	public NeedyCaseBenefit loadNeedyCaseForBenefitDeliveryByOldNumber(String oldNumber) throws HalawaBusinessException {
		
		logger.info("Loading the needy case for benefit delivery where old number = " + oldNumber);
		
		NeedyCase needyCase = this.loadNeedyCaseByOldNumber(oldNumber.trim());
		return this.loadBenefitForNeedycase(needyCase);
	}
	
	public NeedyCaseBenefit loadNeedyCaseForBenefitDelivery(String nationalId) throws HalawaBusinessException {
		
		logger.info("Loading the needy case for benefit delivery where national id = " + nationalId);
		
		
		NeedyCase needyCase = this.loadNeedyCaseByNationalId(nationalId);
		
		return this.loadBenefitForNeedycase(needyCase);
		
	}

	private NeedyCaseBenefit loadBenefitForNeedycase(NeedyCase needyCase) throws HalawaBusinessException {
		
		Campaign campaign;
		NeedyCaseBenefit needyCaseBenefit;
		CampaignAction campaignAction;
		
		
		if ( needyCase.getStatus() != NeedyCaseStatusTypes.ACTIVE.getValue() ) {
			
			logger.error("The needy case with national id " + needyCase.getNationalId() + ", has status " + needyCase.getStatus() + 
					" which is not ACTIVE");
			
			throw new HalawaBusinessException(NeedyCaseErrorCodes.NEEDYCASE_IS_NOT_ACTIVE.getErrorCode());
		}
	
		if ( needyCase instanceof RegularCase ) {
			
			campaign = this.campaignService.loadActiveCampaign(NeedyCaseTypes.REGULAR);
		} else {
			
			campaign = this.campaignService.loadActiveCampaign(NeedyCaseTypes.IRREGULAR);
		}
		
		logger.info("The loaded needy case is of type " + NeedyCaseTypes.getNeedyCaseType(needyCase.getType()));
		campaignAction = this.campaignService.loadCampaignAction(campaign, needyCase);
		if ( campaignAction != null && campaignAction.getStatus() == CampaignActionStatusType.DELIVERED_TO_NEEDYCASE.getValue()) {
			
			logger.info("The needy case with national id " + needyCase.getNationalId() + ", took benefit before in current active campaign " + 
					" with id " + campaign.getId() + ", in " + campaignAction.getAddingDate());
			
			HalawaBusinessException exception = new HalawaBusinessException(CampaignErrorCodes.ACTION_NEEDYCASE_TAKE_BENEFIT_BEFORE.getErrorCode());
			exception.setParameters(campaignAction.getAddingDate());
			throw exception;
		}
		/*
		if ( needyCase instanceof RegularCase ) {
			
			logger.debug("Check if the regular needy case with national id " + needyCase.getNationalId() + 
					" take benefit before during current campaign or not");
			
			RegularCase castedNeedyCase = (RegularCase) needyCase;
			if ( castedNeedyCase.getLastBenefitDate() != null ) {
				
				checkRegularCaseLastBenefitDate(castedNeedyCase, campaign);
			}
		}
		*/
		logger.info("Filling the needy case benefit");
		needyCaseBenefit = fillNeedyCaseBenefit(needyCase, campaign);
		return needyCaseBenefit;
	}
	private void checkRegularCaseLastBenefitDate(RegularCase regularCase, Campaign campaign) throws HalawaBusinessException {
		
		
		Calendar lastRegularCaseBenefitDate = Calendar.getInstance();
		lastRegularCaseBenefitDate.setTimeInMillis(regularCase.getLastBenefitDate().getTime());
		
		String lastCampaignMonthStr = this.configurationService.getProperty("campaign.regullarCampaign.lastActiveCampaignMonth");
		String[] d = lastCampaignMonthStr.split("-");
		int lastCampaignMonth = Integer.parseInt(d[0]);
		int lastCampaignYear = Integer.parseInt(d[1]);
		int lastBenefitMonth = lastRegularCaseBenefitDate.get(Calendar.MONTH) + 1;
		int lastBenefitYear = lastRegularCaseBenefitDate.get(Calendar.YEAR);
		
		if ( (lastBenefitYear > lastCampaignYear) ||
				(lastBenefitYear == lastCampaignYear && lastBenefitMonth >= lastCampaignMonth) ) {
			
			logger.info("The needy case with national id " + regularCase.getNationalId() + 
					" has last benefit date which = " + regularCase.getLastBenefitDate() + ", so it will not take benefit during current " + 
					" active campaign");
			HalawaBusinessException exception = new HalawaBusinessException(CampaignErrorCodes.ACTION_NEEDYCASE_LASTBENEFITDATE_EXCEED_CAMPAIGNDATE.getErrorCode());
			exception.setParameters(regularCase.getLastBenefitDate());
			throw exception;
		}
	}

	public NeedyCaseBenefit loadNeedyCaseForExceptionalBenefitDeliveryByOldNumber(String oldNumber) throws HalawaBusinessException {
		
		NeedyCase needyCase;
		Campaign campaign;
		NeedyCaseBenefit needyCaseBenefit;
		
		needyCase = this.loadNeedyCaseByOldNumber(oldNumber);
		if ( needyCase.getStatus() != NeedyCaseStatusTypes.ACTIVE.getValue() ) {
			
			logger.error("The needy case with smart card id " + oldNumber + ", has status " + needyCase.getStatus() + 
					" which is not ACTIVE");
			throw new HalawaBusinessException(NeedyCaseErrorCodes.NEEDYCASE_IS_NOT_ACTIVE.getErrorCode());
		}
	
		campaign = this.campaignService.loadActiveCampaign(CampaignType.EXCEPTIONAL_CAMPAIGN, 
				(needyCase instanceof RegularCase)?NeedyCaseTypes.REGULAR:NeedyCaseTypes.IRREGULAR );
		
		logger.info("Filling the needy case benefit");
		needyCaseBenefit = fillNeedyCaseBenefit(needyCase, campaign);
		return needyCaseBenefit;
	}
	
	public NeedyCaseBenefit loadNeedyCaseForExceptionalBenefitDelivery(String nationalId) throws HalawaBusinessException {
		
		NeedyCase needyCase;
		Campaign campaign;
		NeedyCaseBenefit needyCaseBenefit;
		
		needyCase = this.loadNeedyCaseByNationalId(nationalId);
		if ( needyCase.getStatus() != NeedyCaseStatusTypes.ACTIVE.getValue() ) {
			
			logger.error("The needy case with national id " + nationalId + ", has status " + needyCase.getStatus() + 
					" which is not ACTIVE");
			throw new HalawaBusinessException(NeedyCaseErrorCodes.NEEDYCASE_IS_NOT_ACTIVE.getErrorCode());
		}
	
		campaign = this.campaignService.loadActiveCampaign(CampaignType.EXCEPTIONAL_CAMPAIGN, 
				(needyCase instanceof RegularCase)?NeedyCaseTypes.REGULAR:NeedyCaseTypes.IRREGULAR );
		
		logger.info("Filling the needy case benefit");
		needyCaseBenefit = fillNeedyCaseBenefit(needyCase, campaign);
		return needyCaseBenefit;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void addRegularNeedyCase(RegularCase regularCase) throws HalawaBusinessException {
		
		if ( regularCase == null ) {
			
			logger.error("The passed regular case is null");
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		NeedyCase needyCase = this.needyCaseDao.loadNeedyCaseByNationalId(regularCase.getNationalId());
		if ( needyCase != null ) {
			
			HalawaErrorCode errorCode;
			if ( needyCase instanceof RegularCase ) {
				
				errorCode = NeedyCaseErrorCodes.ADD_NEEDYCASE_REGULAR_ALREADY_EXIST_AS_REGULAR.getErrorCode();
			} else {
				
				errorCode = NeedyCaseErrorCodes.ADD_NEEDYCASE_REGULAR_ALREADY_EXIST_AS_IRREGULAR.getErrorCode();
			}
			throw new HalawaBusinessException(errorCode, "Needy case already exist with national id " + needyCase.getNationalId() + 
					", which is same as the passed one");
		}
		if ( regularCase.getCaseAddress() != null ) {
			
			regularCase.getCaseAddress().addNeedyCase(regularCase);
		}
		regularCase.setNationalId( regularCase.getNationalId().trim() );
		this.needyCaseDao.addNeedyCase(regularCase);
	}

	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateNeedyCasePersonalPhoto(Long needyCaseId, byte[] photo)
			throws HalawaBusinessException {
		this.needyCaseDao.updateNeedyCasePersonalPhoto(needyCaseId, photo);
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateNeedyCaseAgentPersonalPhoto(Long needyCaseId, byte[] photo)
			throws HalawaBusinessException {
		
		this.needyCaseDao.updateNeedyCaseAgentPersonalPhoto(needyCaseId, photo);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void addIrregularNeedyCase(IrregularCase irregularCase) throws HalawaBusinessException {
		
		if ( irregularCase == null ) {
			
			logger.error("The passed irregular case is null");
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		NeedyCase needyCase = this.needyCaseDao.loadNeedyCaseByNationalId(irregularCase.getNationalId());
		if ( needyCase != null ) {
			
			HalawaErrorCode errorCode;
			if ( needyCase instanceof RegularCase ) {
				
				errorCode = NeedyCaseErrorCodes.ADD_NEEDYCASE_IRREGULAR_ALREADY_EXIST_AS_REGULAR.getErrorCode();
			} else {
				
				errorCode = NeedyCaseErrorCodes.ADD_NEEDYCASE_IRREGULAR_ALREADY_EXIST_AS_IRREGULAR.getErrorCode();
			}
			throw new HalawaBusinessException(errorCode, "Needy case already exist with national id " + needyCase.getNationalId() + 
					", which is same as the passed one");
		}
		
		if ( irregularCase.getCaseAddress() != null ) {
			
			irregularCase.getCaseAddress().addNeedyCase(irregularCase);
		}
		irregularCase.setNationalId( irregularCase.getNationalId().trim() );
		this.needyCaseDao.addNeedyCase(irregularCase);
	}
	
	public RegularCase editRegularNeedyCase(Long needyCaseId) throws HalawaBusinessException {
		
		NeedyCase needyCase;
		
		needyCase = this.needyCaseDao.editNeedyCase(needyCaseId);
		if ( needyCase == null ) {
			
			logger.fatal("No needy case exist with the passed id " + needyCaseId);
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		
		if ( !(needyCase instanceof RegularCase) ) {
			
			logger.fatal("The needy case with id " + needyCaseId + ", is not regular case while you are trying to load it as regular case");
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return (RegularCase) needyCase;
	}

	public IrregularCase editIrregularNeedyCase(Long needyCaseId) throws HalawaBusinessException {
		
		NeedyCase needyCase;
		
		needyCase = this.needyCaseDao.editNeedyCase(needyCaseId);
		if ( needyCase == null ) {
			
			logger.fatal("No needy case exist with the passed id " + needyCaseId);
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		
		if ( !(needyCase instanceof IrregularCase) ) {
			
			logger.fatal("The needy case with id " + needyCaseId + ", is not irregular case while you are trying to load it as irregular case");
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}

		return (IrregularCase) needyCase;
	}
	
	public List<NeedyCase> loadNeedyCases(NeedyCaseTypes needyCaseType, PaginationData paginationData, NeedyCaseStatusTypes... needyCaseStatusTypes) {
		
		return this.needyCaseDao.loadNeedyCases(needyCaseType, paginationData, needyCaseStatusTypes);
	}

	public int countNeedyCases(NeedyCaseTypes needyCaseType, NeedyCaseStatusTypes... caseStatusTypes) {
		
		return this.needyCaseDao.countNeedyCases(needyCaseType, caseStatusTypes);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateNeedyCase(NeedyCase needyCase) throws HalawaBusinessException {
		
		logger.info("Updating the needy case with national id " + needyCase.getNationalId());
		NeedyCase oldNeedyCase = this.needyCaseDao.loadNeedyCaseByNationalId(needyCase.getNationalId());
		if ( oldNeedyCase != null  && (!oldNeedyCase.getId().equals(needyCase.getId())) ) {
			
			HalawaErrorCode errorCode;
			errorCode = NeedyCaseErrorCodes.UPDATE_NEEDYCASE_NATIONAL_ID_ALREADY_EXIST.getErrorCode();
			
			throw new HalawaBusinessException(errorCode, "Needy case already exist with national id " + needyCase.getNationalId() + 
					", which is same as the passed one");
		}
		
		if ( needyCase.getNeedyCaseAgent() == null || StringUtil.isEmpty(needyCase.getNeedyCaseAgent().getFullName())) {
			
			logger.info("You are passing needy case agent with empty name, so it will be deleted, where the needycase national id is " + needyCase.getNationalId());
			this.needyCaseDao.deleteNeedyCaseAgent(needyCase.getId());
		}
		needyCase.setNationalId( needyCase.getNationalId().trim() );
		this.needyCaseDao.updateNeedyCase(needyCase);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void convertIrregularCaseAsRegular(String nationalId, RegularCase regularCase) throws HalawaBusinessException {
		
		NeedyCase needyCase;
		RegularCase createdRegularCase;
		
		logger.info("Converting the irregular case to become regular, for case with national id " + nationalId);
		
		if ( StringUtil.isEmpty(nationalId) || regularCase == null ) {
			
			logger.fatal("The passed parameters are not valid");
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		
		needyCase = this.needyCaseDao.loadIrregularNeedyCaseForConverting(nationalId);
		if ( needyCase == null ) {
			
			logger.error("No irregular needy case exist with national id " + nationalId);
			throw new HalawaBusinessException(NeedyCaseErrorCodes.CONVERT_IRREGULAR_NEEDYCASE_IS_NOT_EXIST.getErrorCode());
		}
		
		createdRegularCase = new RegularCase();
		createdRegularCase.setMonthlyAmount( regularCase.getMonthlyAmount() );
		
		
		BeanUtils.copyProperties(needyCase, createdRegularCase, new String[]{"id", "campaignActions", "type"});
		createdRegularCase.setSmartCardNumber( regularCase.getSmartCardNumber() );
		
		logger.info("Updating the status of irregular needy case with id " + needyCase.getId() + ", to become " + 
				NeedyCaseStatusTypes.CONVERTED_AS_REGULAR );
		needyCase.setStatus( NeedyCaseStatusTypes.CONVERTED_AS_REGULAR.getValue() );
		this.needyCaseDao.updateNeedyCase(needyCase);
		
		
		logger.info("Adding the new created regular needy case to database");
		if ( createdRegularCase.getCaseAddress() != null ) {
			
			createdRegularCase.getCaseAddress().getNeedyCase().add(createdRegularCase);
		}
		this.needyCaseDao.addNeedyCase(createdRegularCase);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void updateNeedyCaseStatus(Long needyCaseId, NeedyCaseStatusTypes newStatus) throws HalawaBusinessException {
		
		logger.info("Deleting the needy case with id " + needyCaseId);
		
		if ( needyCaseId == null || needyCaseId.longValue() == 0 ) {
			
			logger.fatal("The passed parameters are not valid");
			throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR);
		}
		
		try {
			
			this.needyCaseDao.updateNeedCaseStatus(needyCaseId, newStatus);
			
		} catch(HalawaSystemException exception) {
			
			throw exception;
		} catch(Exception exception) {
			
			logger.error("Failed to delete the needy case", exception);
			throw new HalawaSystemException(exception);
		}
		
	}
	
	public List<NeedyCase> searchNeedyCases(NeedyCaseSearchCriteria searchCriteria) throws HalawaBusinessException {
		
		if ( searchCriteria == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		logger.info("Searching for needy cases using the search criteria " + searchCriteria);
		return this.needyCaseDao.searchNeedyCases(searchCriteria);
	}
	
	public void exportNeedyCases(File destFile, NeedyCaseTypes needyCaseType, NeedyCaseStatusTypes... needyCaseStatusTypes)
			throws HalawaBusinessException {
		
		Workbook workbook = new HSSFWorkbook();
		PaginationData paginationData = new PaginationData(0, 100);
		int excelStartRowIndex = 1;
		
		logger.info("Exporting needy cases to excel sheet");
		if ( destFile == null || needyCaseType == null || needyCaseStatusTypes == null || 
				needyCaseStatusTypes.length == 0) {
		
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		try {
			
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destFile));
			Sheet needyCaseSheet = workbook.createSheet(GoodSeasonsMessageBundle.getInstance().getMessage("needyCase_export_sheetName"));
			fillExportedExcelHeaderRow(workbook, needyCaseSheet, needyCaseType);
			while (true) {
				
				List<NeedyCase> needyCases = this.needyCaseDao.loadNeedyCases(needyCaseType, paginationData, needyCaseStatusTypes);
				if ( needyCases == null || needyCases.isEmpty() ) {
					
					break;
				}
				
				excelStartRowIndex = this.fillExportedExcelRows(workbook, needyCaseSheet, excelStartRowIndex, needyCaseType, needyCases);
				paginationData.setStartIndex( paginationData.getStartIndex() + paginationData.getPageSize() );
			}
			workbook.write(outputStream);
			outputStream.close();
			
		} catch(HalawaSystemException exception) {
			
			throw exception;
		} catch(Exception exception) {
			
			logger.error("Failed to export the needy cases to excel file", exception);
			throw new HalawaSystemException(exception);
		}
	}

	
	public byte[] loadNeedyCasePersonalPhoto(Long needyCaseId)
			throws HalawaBusinessException {
		
		NeedyCase needyCase = this.loadNeedyCaseById(needyCaseId);
		
		if ( needyCase == null ) {
			
			logger.fatal("The passed needy case id is not exist or null, I can't load its personal photo");
			return null;
		}
		
		byte[] photoBinary = needyCase.getPersonalPhoto();
		if ( photoBinary == null ) {
			
			 String defaultPhotoPath = this.configurationService.getProperty("default_personal_photo_path");
			 if (defaultPhotoPath != null && defaultPhotoPath.trim().length() > 0) {
				 
				 try {
					 
					 byte[] temp = new byte[2*1024];
					 BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(defaultPhotoPath));
					 ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
					 while ( inputStream.read(temp) != -1 ) {
						 
						 arrayOutputStream.write(temp);
					 }
					 
					 photoBinary = arrayOutputStream.toByteArray();
					 
				 } catch(Exception exception) {
					 
					 logger.fatal("Failed to read the default personal photo from default path", exception);
				 }
			 }
		}
		
		return photoBinary;
	}

	public byte[] loadNeedyCaseAgentPersonalPhoto(Long needyCaseId)
			throws HalawaBusinessException {
		
		NeedyCase needyCase = this.loadNeedyCaseById(needyCaseId);
		if ( needyCase != null && needyCase.getNeedyCaseAgent() != null ) {
			
			byte[] photoBinary = needyCase.getNeedyCaseAgent().getPersonalPhoto();
			if ( photoBinary == null ) {
				
				 String defaultPhotoPath = this.configurationService.getProperty("default_personal_photo_path");
				 if (defaultPhotoPath != null && defaultPhotoPath.trim().length() > 0) {
					 
					 try {
						 
						 byte[] temp = new byte[2*1024];
						 BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(defaultPhotoPath));
						 ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
						 while ( inputStream.read(temp) != -1 ) {
							 
							 arrayOutputStream.write(temp);
						 }
						 
						 photoBinary = arrayOutputStream.toByteArray();
					 } catch(Exception exception) {
						 
						 logger.fatal("Failed to read the default personal photo from default path", exception);
					 }
				 }
			}
			return photoBinary;
		}
		
		return null;
	}

	private int fillExportedExcelRows(Workbook workbook, Sheet sheet, int startRowIndex,NeedyCaseTypes needyCaseType, List<NeedyCase> needyCases) {
		
		int endRowIndex = startRowIndex;
		for(NeedyCase needyCase: needyCases) {
			
			Row row = sheet.createRow(endRowIndex); endRowIndex++;
			Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
			cell.setCellValue(needyCase.getNationalId());
			
			cell = row.createCell(1, Cell.CELL_TYPE_STRING);
			cell.setCellValue(needyCase.getFullName());
			
			cell = row.createCell(2, Cell.CELL_TYPE_STRING);
			cell.setCellValue( GoodSeasonsMessageBundle.getInstance().getMessage("common_constants_maritalStatus_" + (needyCase.getMaritalStatus()) ));
			
			cell = row.createCell(3, Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(needyCase.getFamilyPersons());
			
			if ( needyCaseType == NeedyCaseTypes.REGULAR ) {
				
				cell = row.createCell(4, Cell.CELL_TYPE_STRING);
				if ( needyCase instanceof RegularCase ) {
					cell.setCellValue( ((RegularCase) needyCase).getMonthlyAmount() );
				} else {
					cell.setCellValue( 0 );
				}
			}
		}
		
		return endRowIndex;
	}
	
	private void fillExportedExcelHeaderRow(Workbook workbook, Sheet sheet, NeedyCaseTypes caseType) {
		
		logger.info("Creating header row for needy case of type " + caseType);
		Row headerRow = sheet.createRow(0);
		Font headerCellsFont = workbook.createFont();
		CellStyle headerCellsStyle = workbook.createCellStyle();
		
		headerCellsFont.setFontName("Times New Roman");
		headerCellsFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerCellsFont.setColor(Font.COLOR_NORMAL);
		headerCellsStyle.setFont(headerCellsFont);
		headerCellsStyle.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
		
		Cell cell = headerRow.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellStyle(headerCellsStyle);
		cell.setCellValue(GoodSeasonsMessageBundle.getInstance().getMessage("needyCase_export_header_common_nationalId"));
		
		cell = headerRow.createCell(1, Cell.CELL_TYPE_STRING);
		cell.setCellStyle(headerCellsStyle);
		cell.setCellValue(GoodSeasonsMessageBundle.getInstance().getMessage("needyCase_export_header_common_name"));
		
		cell = headerRow.createCell(2, Cell.CELL_TYPE_STRING);
		cell.setCellStyle(headerCellsStyle);
		cell.setCellValue(GoodSeasonsMessageBundle.getInstance().getMessage("needyCase_export_header_common_maritalStatus"));
		
		cell = headerRow.createCell(3, Cell.CELL_TYPE_NUMERIC);
		cell.setCellStyle(headerCellsStyle);
		cell.setCellValue(GoodSeasonsMessageBundle.getInstance().getMessage("needyCase_export_header_common_familyPersons"));
		
		if ( caseType == NeedyCaseTypes.REGULAR ) {
			
			cell = headerRow.createCell(4, Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(headerCellsStyle);
			cell.setCellValue(GoodSeasonsMessageBundle.getInstance().getMessage("needyCase_export_header_common_monthlyAmount"));
		}
	}
	private NeedyCaseBenefit fillNeedyCaseBenefit(NeedyCase needyCase, Campaign campaign) throws HalawaBusinessException {
		
		NeedyCaseBenefit needyCaseBenefit = new NeedyCaseBenefit();
		List<NeedyCaseBenefit.CalculatedCampaignItem> calculatedCampaignItems;
		
		needyCaseBenefit.setNeedyCase(needyCase);
		calculatedCampaignItems = new ArrayList<NeedyCaseBenefit.CalculatedCampaignItem>();
		for(CampaignItem campaignItem: campaign.getCampaignItems()) {
			
			double calculatedValue = 0;
			NeedyCaseBenefit.CalculatedCampaignItem calculatedCampaignItem = new NeedyCaseBenefit.CalculatedCampaignItem();
			calculatedCampaignItem.setCampaignItem(campaignItem);
			if ( needyCase instanceof RegularCase ) {
				
				if ( campaignItem.getCampaignItemType().isMonthlyBased() ) {
				
					RegularCase regularCase = (RegularCase) needyCase;
					calculatedValue = campaignItem.getQuantity() * regularCase.getMonthlyAmount();
					calculatedValue = (calculatedValue * campaignItem.getCampaignItemType().getPercentage()) / 100;
					
					calculatedCampaignItem.setCalculatedValue( Double.valueOf(calculatedValue).floatValue() );
				} else {
					
					calculatedCampaignItem.setCalculatedValue( Double.valueOf(campaignItem.getQuantity()).floatValue() );
				}
			} else {
				
				calculatedCampaignItem.setCalculatedValue( Double.valueOf(campaignItem.getQuantity()).floatValue() );
			}
			
			calculatedCampaignItems.add(calculatedCampaignItem);
		}
		
		needyCaseBenefit.setCalculatedCampaignItems(calculatedCampaignItems);
		return needyCaseBenefit;
	}
	
	public void setNeedyCaseDao(NeedyCaseDao needyCaseDao) {
		this.needyCaseDao = needyCaseDao;
	}

	
	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	
}
