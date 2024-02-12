package com.halawa.goodseasons.web.backing.needycase;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.NeedyCaseStatusTypes;
import com.halawa.goodseasons.common.constants.PrivilegesEnum;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.GoodSeasonsMessageBundle;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.model.entity.CaseAddress;
import com.halawa.goodseasons.model.entity.IrregularCase;
import com.halawa.goodseasons.model.entity.NeedyCase;
import com.halawa.goodseasons.model.entity.NeedyCaseAgent;
import com.halawa.goodseasons.model.entity.RegularCase;
import com.halawa.goodseasons.service.ConfigurationService;
import com.halawa.goodseasons.service.NeedyCaseService;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.entity.WebDate;
import com.halawa.goodseasons.web.util.BranchUserSession;
import com.halawa.goodseasons.web.util.FacesUtil;
import com.halawa.goodseasons.web.util.WebErrorCodes;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class AddUpdateIrregularNeedyCaseBean extends AbstractBean {

	private static final HalawaLogger logger = HalawaLogger.getInstance(AddUpdateIrregularNeedyCaseBean.class);
	
	private NeedyCaseService needyCaseService;
	private ConfigurationService configurationService;
	private WebDate dateOfBirth;
	private boolean editIrregularCase;
	private IrregularCase irregularCase;
	private CaseAddress caseAddress;
	
	private NeedyCaseAgent caseAgent;
	
	public AddUpdateIrregularNeedyCaseBean() {
		
		this.irregularCase = new IrregularCase();
		this.irregularCase.setFamilyPersons((short) 1);
		this.caseAddress = new CaseAddress();
		this.dateOfBirth = new WebDate();
		
		this.caseAgent = new NeedyCaseAgent();
	}
	
	
	
	
	public void printBarCodeToPDF() {
		
		String nationalId = FacesUtil.getRequestParameter("nationalId");
		try {
			
			NeedyCase v = this.needyCaseService.loadNeedyCaseByNationalId(nationalId);
			Document document = new Document();
			
			
			
			File file = new File("c:/tempGoodSeasonsFolder/" + v.getNationalId() + ".png");
			File pdfFile = new File("c:/tempGoodSeasonsFolder/" + v.getNationalId() + ".pdf");
			
			pdfFile.delete();
			file.delete();
			
			PdfWriter.getInstance(document, new BufferedOutputStream(new FileOutputStream(pdfFile)));
			
			Barcode b = BarcodeFactory.createCode128(nationalId);
			b.setDrawingText(false);
			b.setBarHeight(100);
			BarcodeImageHandler.savePNG(b, file);
			
			document.open();
			
			Image image = Image.getInstance("c:/tempGoodSeasonsFolder/" + v.getNationalId() + ".png");
			image.setAlignment(Element.ALIGN_CENTER);
			image.setAlignment(Image.MIDDLE | Image.TEXTWRAP);
			
			
			BaseFont bf = BaseFont.createFont("c:\\windows\\fonts\\times.ttf", BaseFont.IDENTITY_H, true);
			
			PdfPTable pdfTable = new PdfPTable(1);
			
			pdfTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
			PdfPCell fullNameCell = new PdfPCell( new Phrase(v.getFullName(), 
					new Font(bf, 
							Integer.parseInt(this.configurationService.getProperty("needyCase.regular.printCard.fontSize")), 
							Font.NORMAL, BaseColor.BLUE)) );
			
			fullNameCell.setHorizontalAlignment( Element.ALIGN_CENTER );
			fullNameCell.setVerticalAlignment( Element.ALIGN_CENTER );
			fullNameCell.setPaddingBottom(10);
			
			PdfPCell nationalIdCell = new PdfPCell( new Phrase(v.getNationalId(), 
					new Font(bf, 24, Font.NORMAL, BaseColor.RED)) );
			nationalIdCell.setHorizontalAlignment( Element.ALIGN_CENTER );
			nationalIdCell.setVerticalAlignment( Element.ALIGN_CENTER );
			nationalIdCell.setPaddingBottom(5);
			
			
			pdfTable.addCell(fullNameCell);
			pdfTable.addCell(image);
			pdfTable.addCell(nationalIdCell);
			
			PdfPCell spaceCell = new PdfPCell();
			spaceCell.setFixedHeight(10);
			pdfTable.addCell(spaceCell);
			
			PdfPCell personalPhotoCell = new PdfPCell();
			personalPhotoCell.setFixedHeight(Float.parseFloat(this.configurationService.getProperty("needyCase.printCard.personalPhotoMaxHeight")));
			
			if( Boolean.parseBoolean(this.configurationService.getProperty("needyCase.printCard.printPersonalPhoto")) ) {
				Image casePersonalPhoto = null;
				if ( v.getPersonalPhoto() == null ) {
					casePersonalPhoto = Image.getInstance(this.configurationService.getProperty("default_personal_photo_path"));
					
				} else {
					casePersonalPhoto = Image.getInstance(v.getPersonalPhoto());
					
				}
				
				casePersonalPhoto.scaleToFit(Float.parseFloat(this.configurationService.getProperty("needyCase.printCard.maxWidth")), 
						Float.parseFloat(this.configurationService.getProperty("needyCase.printCard.maxHeight")));
				
				casePersonalPhoto.setRotationDegrees( 180 );
				casePersonalPhoto.setAlignment(Image.ALIGN_CENTER | Image.TEXTWRAP);
				personalPhotoCell.addElement( casePersonalPhoto );
			}
			String smartCardNumber = StringUtil.isEmpty(v.getSmartCardNumber())? "---" : v.getSmartCardNumber();
			String needyCaseTypeStr = "(" + smartCardNumber + ") ";
			needyCaseTypeStr += (v instanceof RegularCase)? 
					GoodSeasonsMessageBundle.getInstance().getMessage("common_constants_needyCaseType_1"):
						GoodSeasonsMessageBundle.getInstance().getMessage("common_constants_needyCaseType_2");
			
			
			
			PdfPCell needyCaseType = new PdfPCell( new Phrase(needyCaseTypeStr, 
					new Font(bf, 24, Font.NORMAL, BaseColor.RED)) );
			needyCaseType.setHorizontalAlignment( Element.ALIGN_CENTER );
			needyCaseType.setVerticalAlignment( Element.ALIGN_CENTER );
			needyCaseType.setPaddingBottom(5);
			
			needyCaseType.setRotation( 180 );
			pdfTable.addCell(needyCaseType);
			pdfTable.addCell( personalPhotoCell );
			
			pdfTable.setWidthPercentage(45);
			document.add(pdfTable);
			
            
			if ( v.getNeedyCaseAgent() != null ) {
			
				document.newPage();
				
				pdfTable = new PdfPTable(1);
				
				pdfTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
				PdfPCell agentFullNameCell = new PdfPCell( new Phrase(v.getNeedyCaseAgent().getFullName(), 
						new Font(bf, 
								Integer.parseInt(this.configurationService.getProperty("needyCase.regular.printCard.fontSize")), 
								Font.NORMAL, BaseColor.BLUE)) );
				
				agentFullNameCell.setHorizontalAlignment( Element.ALIGN_CENTER );
				agentFullNameCell.setVerticalAlignment( Element.ALIGN_CENTER );
				agentFullNameCell.setPaddingBottom(10);
				
				
				
				pdfTable.addCell(agentFullNameCell);
				pdfTable.addCell(image);
				pdfTable.addCell(nationalIdCell);
				
				spaceCell = new PdfPCell();
				spaceCell.setFixedHeight(10);
				pdfTable.addCell(spaceCell);
				
				
				
				
				
				
				
				
				
				
				
				PdfPCell agentPersonalPhotoCell = new PdfPCell();
				agentPersonalPhotoCell.setFixedHeight(Float.parseFloat(this.configurationService.getProperty("needyCase.printCard.personalPhotoMaxHeight")));
				
				if( Boolean.parseBoolean(this.configurationService.getProperty("needyCase.printCard.printPersonalPhoto")) ) {
					Image casePersonalPhoto = null;
					if ( v.getNeedyCaseAgent().getPersonalPhoto() == null ) {
						
						casePersonalPhoto = Image.getInstance(this.configurationService.getProperty("default_personal_photo_path"));
						
					} else {
						casePersonalPhoto = Image.getInstance(v.getNeedyCaseAgent().getPersonalPhoto());
						
					}
					
					casePersonalPhoto.scaleToFit(Float.parseFloat(this.configurationService.getProperty("needyCase.printCard.maxWidth")), 
							Float.parseFloat(this.configurationService.getProperty("needyCase.printCard.maxHeight")));
					
					casePersonalPhoto.setRotationDegrees( 180 );
					casePersonalPhoto.setAlignment(Image.ALIGN_CENTER | Image.TEXTWRAP);
					agentPersonalPhotoCell.addElement( casePersonalPhoto );
				}
				
				
				
				
				needyCaseTypeStr = needyCaseTypeStr + " - " + GoodSeasonsMessageBundle.getInstance().getMessage("needyCase_add_update_regular_agent");
				
				needyCaseType = new PdfPCell( new Phrase(needyCaseTypeStr, 
						new Font(bf, 24, Font.NORMAL, BaseColor.RED)) );
				needyCaseType.setHorizontalAlignment( Element.ALIGN_CENTER );
				needyCaseType.setVerticalAlignment( Element.ALIGN_CENTER );
				needyCaseType.setPaddingBottom(5);
				
				needyCaseType.setRotation( 180 );
				pdfTable.addCell(needyCaseType);
				pdfTable.addCell( agentPersonalPhotoCell );
				
				pdfTable.setWidthPercentage(45);
				document.add(pdfTable);
				
				
			}
			
			
            document.close();
			
			
		} catch(Exception exception) {
			
			logger.error("Failed to write to PDF document", exception);
			super.addMessage("needyCase_manageRegular_FailedToPrintBarCode");
			return;
		}
		
		
		this.writeOutContent((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(), 
				new File("c:/tempGoodSeasonsFolder/" + nationalId + ".pdf"), nationalId + ".pdf");
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	

	public String addUpdateIrregularCase() {
		
		String dest = null;
		try {

			if ( !validateNationaId(this.irregularCase.getNationalId()) ) {
				
				super.addMessage("customValidators_common_nationalIdValidation");
				return null;
			}
			if ( this.editIrregularCase ) {
				
				logger.info("Updating the irregular needy case");
				this.irregularCase.setDateOfBirth( new Date() );
				if ( !StringUtil.isEmpty(this.caseAddress.getHomeNumber()) ) {
					
					this.irregularCase.setCaseAddress(caseAddress);
				}
				
				if ( !StringUtil.isEmpty(this.caseAgent.getFullName()) ) {
					
					this.irregularCase.setNeedyCaseAgent(this.caseAgent);
				}
				
				this.needyCaseService.updateNeedyCase(irregularCase);
				logger.info("The irregular needy case updated successfully");
				
				dest = "rightmenu_needyCase_manageIrregularCase";
			} else {
				
				logger.info("Adding new regular case with national id " + irregularCase.getNationalId());
				this.irregularCase.setDateOfBirth( new Date() );
				this.irregularCase.setId( null );
				if ( !StringUtil.isEmpty(this.caseAddress.getHomeNumber()) ) {
					
					this.irregularCase.setCaseAddress(caseAddress);
					this.irregularCase.getCaseAddress().setId(null);
				}
				
				if ( !StringUtil.isEmpty(this.caseAgent.getFullName()) ) {
					
					this.caseAgent.setId(null);
					this.irregularCase.setNeedyCaseAgent(this.caseAgent);
					this.irregularCase.getNeedyCaseAgent().setId(null);
				}
				
				this.needyCaseService.addIrregularNeedyCase(irregularCase);
				logger.info("The irregular case added successfully");
				
				
				dest = "rightmenu_needyCase_addIrregularCase";
			}
			
			Long needyCaseId = irregularCase.getId();
			byte[] personalPhoto = this.getNeedyCasePersonalPhoto();
			byte[] agentPersonalPhoto = this.getNeedyCaseAgentPersonalPhoto();
			if ( personalPhoto != null ) {
				this.needyCaseService.updateNeedyCasePersonalPhoto(needyCaseId, personalPhoto);
			}
			if ( agentPersonalPhoto != null ) {
				this.needyCaseService.updateNeedyCaseAgentPersonalPhoto(needyCaseId, agentPersonalPhoto);
			}
			
			this.clear();
			return dest;
			
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to add/update the irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to add/update the irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to add/update the irregular needy case", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return null;
	}
	
	public String editIrregullarNeedyCase() {
		
		String selectedIrregularNeedyCaseId = FacesUtil.getRequestParameter("selectedIrregularNeedyCaseId");
		if ( StringUtil.isEmpty( selectedIrregularNeedyCaseId ) ) {
			
			throw new HalawaSystemException(WebErrorCodes.NO_ENTRY_SELECTED.getErrorCode());
		}
		
		try {
		
			this.irregularCase = this.needyCaseService.editIrregularNeedyCase( Long.parseLong( selectedIrregularNeedyCaseId ) );
			if ( this.irregularCase.getCaseAddress() != null ) {
				
				this.caseAddress = this.irregularCase.getCaseAddress();
			}
			
			if ( this.irregularCase.getNeedyCaseAgent() != null ) {
				
				this.caseAgent = this.irregularCase.getNeedyCaseAgent();
			}
			
			this.dateOfBirth = WebDate.generateWebDate( new Date() );
			this.editIrregularCase = true;
			
			return "editIrregularNeedyCase";
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to add/update the irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to add/update the irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to add/update the irregular needy case", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		} 
 		return null;
	}
	
	private void clear() {
		
		this.irregularCase = new IrregularCase();
		this.irregularCase.setCaseAddress(new CaseAddress());
		this.dateOfBirth = new WebDate();
		this.editIrregularCase = false;
		
		this.irregularCase.setNeedyCaseAgent(new NeedyCaseAgent());
	}

	public String undoDeleteIrregularNeedyCase() {
		
		String selectedIrregularNeedyCaseId = FacesUtil.getRequestParameter("selectedNeedyCaseId");
		
		try {
			
			if ( StringUtil.isEmpty( selectedIrregularNeedyCaseId ) ) {
				
				throw new HalawaSystemException(WebErrorCodes.NO_ENTRY_SELECTED.getErrorCode());
			}
			
			this.needyCaseService.updateNeedyCaseStatus(Long.parseLong(selectedIrregularNeedyCaseId), NeedyCaseStatusTypes.ACTIVE);
			
			return "userHome";
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to undo delete irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to undo delete irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to undo delete irregular needy case", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return "userHome";
	}
	
	
	public String deleteIrregularNeedyCase() {
		
		String selectedIrregularNeedyCaseId = FacesUtil.getRequestParameter("selectedNeedyCaseId");
		
		try {
			
			if ( StringUtil.isEmpty( selectedIrregularNeedyCaseId ) ) {
				
				throw new HalawaSystemException(WebErrorCodes.NO_ENTRY_SELECTED.getErrorCode());
			}
			
			this.needyCaseService.updateNeedyCaseStatus(Long.parseLong(selectedIrregularNeedyCaseId), NeedyCaseStatusTypes.DELETED);
			
			return "userHome";
		} catch(HalawaBusinessException exception) {
			
			logger.error("Failed to delete irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to delete irregular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to delete irregular needy case", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return "userHome";
	}
	
	public void needyCasePhotoListner(UploadEvent event) throws Exception {
		
		try {
			
			BranchUserSession.addSessionProperty("ir_needyCasePersonalPhoto", event.getUploadItem());
		} catch(Exception exception) {
			
			logger.error("Failed to upload the image", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
	}
	
	public void needyCaseAgentPhotoListner(UploadEvent event) throws Exception {
		
		try {
			
			BranchUserSession.addSessionProperty("ir_needyCaseAgentPersonalPhoto", event.getUploadItem());
		} catch(Exception exception) {
			
			logger.error("Failed to upload the image", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
	}
	
	 public void paintNeedyCasePersonalPhoto(OutputStream out, Object data) throws IOException{
		 
		 try {
			 Long needyCaseId = (Long) data;
			 byte[] photoBinary = this.needyCaseService.loadNeedyCasePersonalPhoto(needyCaseId);
			 if ( photoBinary != null ) {
				 BufferedImage image = ImageIO.read(new ByteArrayInputStream(photoBinary));
			     ImageIO.write(image, "jpg", out);
			 }
		 } catch(HalawaBusinessException exception) {
				
				logger.error("Failed to add/update the regular needy case", exception);
				super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to add/update the regular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to add/update the regular needy case", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		} 
	}
	
	 public void paintNeedyCaseAgentPersonalPhoto(OutputStream out, Object data) throws IOException{
		 
		 try {
			 Long needyCaseId = (Long) data;
			 byte[] photoBinary = this.needyCaseService.loadNeedyCaseAgentPersonalPhoto(needyCaseId);
			 if ( photoBinary != null ) {
				 BufferedImage image = ImageIO.read(new ByteArrayInputStream(photoBinary));
			     ImageIO.write(image, "jpg", out);
			 }
		 } catch(HalawaBusinessException exception) {
				
				logger.error("Failed to add/update the regular needy case", exception);
				super.addMessage(exception.getErrorCode());
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to add/update the regular needy case", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to add/update the regular needy case", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		} 
	 }
	 
	 

	private byte[] getNeedyCasePersonalPhoto() {
	
		try {
			
			UploadItem uploadItem = (UploadItem) BranchUserSession.getUserSessionProperty("ir_needyCasePersonalPhoto");
			if ( uploadItem == null ) {
				
				return null;
			}
			
			ByteArrayOutputStream b;
			byte[] buf = new byte[1024];
			if (uploadItem.isTempFile()) {
				b = new ByteArrayOutputStream();
				BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(uploadItem.getFile()));
				while ( bufferedInputStream.read(buf) != -1 ) {
					
					b.write(buf);
				}
				
				bufferedInputStream.close();
				uploadItem.getFile().delete();
			} else {
				 b = new ByteArrayOutputStream();
				 b.write(uploadItem.getData());
			}
			
			BranchUserSession.removeSessionProperty("ir_needyCasePersonalPhoto");
        	return b.toByteArray();
		} catch(Exception exception) {
			
			logger.error("Failed to upload the image", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		return null;
	}

	
	private byte[] getNeedyCaseAgentPersonalPhoto() {
		

		try {
			
			UploadItem uploadItem = (UploadItem) BranchUserSession.getUserSessionProperty("ir_needyCaseAgentPersonalPhoto");
			if ( uploadItem == null ) {
				
				return null;
			}
			
			ByteArrayOutputStream b;
			byte[] buf = new byte[1024];
			if (uploadItem.isTempFile()) {
				b = new ByteArrayOutputStream();
				BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(uploadItem.getFile()));
				while ( bufferedInputStream.read(buf) != -1 ) {
					
					b.write(buf);
				}
				
				bufferedInputStream.close();
				uploadItem.getFile().delete();
			} else {
				 b = new ByteArrayOutputStream();
				 b.write(uploadItem.getData());
			}
			
			BranchUserSession.removeSessionProperty("ir_needyCaseAgentPersonalPhoto");
        	return b.toByteArray();
		} catch(Exception exception) {
			
			logger.error("Failed to upload the image", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		return null;
	}


	public boolean getCanUpdateNationalId() {
		
		return this.isUserHasPrivilege(PrivilegesEnum.NEEDYCASE_IRREGULAR_EDIT_NATIONALID);
	}
	
	public boolean getCanUpdateIrregularNeedyCase() {
		
		return true;
	}
	
	public boolean isEditIrregularCase() {
		return editIrregularCase;
	}


	public void setEditIrregularCase(boolean editIrregularCase) {
		this.editIrregularCase = editIrregularCase;
	}


	public WebDate getDateOfBirth() {
		return dateOfBirth;
	}


	public IrregularCase getIrregularCase() {
		return irregularCase;
	}


	public void setNeedyCaseService(NeedyCaseService needyCaseService) {
		this.needyCaseService = needyCaseService;
	}


	public CaseAddress getCaseAddress() {
		return caseAddress;
	}


	public NeedyCaseAgent getCaseAgent() {
		return caseAgent;
	}


	public void setCaseAgent(NeedyCaseAgent caseAgent) {
		this.caseAgent = caseAgent;
	}
	
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
}
