package com.halawa.goodseasons.web.backing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.NeedyCaseTypes;
import com.halawa.goodseasons.common.constants.PrivilegesEnum;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.model.entity.CampaignItemType;
import com.halawa.goodseasons.model.entity.User;
import com.halawa.goodseasons.service.CampaignService;
import com.halawa.goodseasons.service.UserService;
import com.halawa.goodseasons.web.entity.WebUser;
import com.halawa.goodseasons.web.util.CommonValidators;
import com.halawa.goodseasons.web.util.FacesUtil;
import com.sun.faces.util.MessageFactory;

public abstract class AbstractBean extends CommonValidators {
	private static final HalawaLogger logger = HalawaLogger
			.getInstance(AbstractBean.class);
	public static final String SESSION_USER_OBJECT = "ARABIAN_GURU_USER";
	public static final String MESSAGE_PARAMETERS_DATE_FORMAT = "d-M-yyyy";
	private static final SimpleDateFormat ERRORMESSAGE_DATEFORMATTER = new SimpleDateFormat(
			MESSAGE_PARAMETERS_DATE_FORMAT, new Locale("ar", "EG"));
	private static final SimpleDateFormat ERRORMESSAGE_DATETIMEFORMATTER = new SimpleDateFormat(
			"d-M-yyyy h:m a", new Locale("ar", "EG"));

	protected UserService userService;
	protected CampaignService campaignService;
	private List<SelectItem> activeUsersList;
	protected List<SelectItem> regullarCampaignItemTypes;
	protected List<SelectItem> irregullarCampaignItemTypes;
	private Map<Integer, String> campaignItemTypeUnitsMap;

	@SuppressWarnings("unchecked")
	public List<SelectItem> getActiveUsersList() {

		this.activeUsersList = (List<SelectItem>) FacesContext
				.getCurrentInstance().getExternalContext().getApplicationMap()
				.get("activeUsersItemList");
		if (this.activeUsersList == null) {

			this.activeUsersList = new ArrayList<SelectItem>();
			List<User> activeUsers = userService.loadActiveUsers();
			for (User user : activeUsers) {

				SelectItem selectItem = new SelectItem();
				selectItem.setValue(user.getId());
				selectItem.setLabel(user.getUsername());

				this.activeUsersList.add(selectItem);
			}
			FacesContext.getCurrentInstance().getExternalContext()
					.getApplicationMap().put("activeUsersItemList",
							this.activeUsersList);
		}
		return activeUsersList;
	}

	public List<SelectItem> getRegullarCampaignItemTypesSelect() {

		if (this.regullarCampaignItemTypes == null) {

			this.regullarCampaignItemTypes = new ArrayList<SelectItem>();
			List<CampaignItemType> temp = this.campaignService
					.loadCampaignItemTypes(NeedyCaseTypes.REGULAR,
							NeedyCaseTypes.BOTH);
			for (CampaignItemType itemType : temp) {

				SelectItem item = new SelectItem();
				item.setValue(itemType.getId());
				item.setLabel(itemType.getName());
				this.regullarCampaignItemTypes.add(item);
			}
		}

		return this.regullarCampaignItemTypes;
	}

	public List<SelectItem> getIrregullarCampaignItemTypesSelect() {

		if (this.irregullarCampaignItemTypes == null) {

			this.irregullarCampaignItemTypes = new ArrayList<SelectItem>();
			List<CampaignItemType> temp = this.campaignService
					.loadCampaignItemTypes(NeedyCaseTypes.IRREGULAR,
							NeedyCaseTypes.BOTH);
			for (CampaignItemType itemType : temp) {

				SelectItem item = new SelectItem();
				item.setValue(itemType.getId());
				item.setLabel(itemType.getName());
				this.irregullarCampaignItemTypes.add(item);
			}
		}

		return this.irregullarCampaignItemTypes;
	}

	public Map<Integer, String> getCampaignItemTypeUnitsMap() {

		if (this.campaignItemTypeUnitsMap == null) {

			this.campaignItemTypeUnitsMap = new HashMap<Integer, String>();
			List<CampaignItemType> campaignItemTypes = this.campaignService
					.loadCampaignItemTypes(NeedyCaseTypes.values());
			for (CampaignItemType itemType : campaignItemTypes) {

				if (itemType.isMonthlyBased()) {

					campaignItemTypeUnitsMap.put(itemType.getId(), getMessage(
							"common_constants_campaignItemType_month")
							.getSummary());
				} else {

					campaignItemTypeUnitsMap.put(itemType.getId(), itemType
							.getUnitName());
				}
			}
		}
		return campaignItemTypeUnitsMap;
	}

	protected WebUser getLoggedInWebUser() {

		HttpSession session = (HttpSession) context().getExternalContext()
				.getSession(true);
		WebUser sessionWebUser = (WebUser) session
				.getAttribute(SESSION_USER_OBJECT);
		if (sessionWebUser == null) {

			sessionWebUser = new WebUser();
			session.setAttribute(SESSION_USER_OBJECT, sessionWebUser);
		}

		return sessionWebUser;
	}

	protected boolean isUserHasPrivilege(PrivilegesEnum privilege) {

		if (privilege == null)
			return false;
		return this.getLoggedInWebUser().hasPrivilege(privilege);
	}

	protected void removeLoggedInUserFromSession() {

		FacesUtil.removeSessionAttribute(SESSION_USER_OBJECT);
	}

	protected void clearSession() {

		HttpSession session = (HttpSession) context().getExternalContext()
				.getSession(true);
		session.invalidate();
	}

	protected void setLoggedInWebUser(WebUser loggedInWebUser) {

		HttpSession session = (HttpSession) context().getExternalContext()
				.getSession(true);
		session.setAttribute(SESSION_USER_OBJECT, loggedInWebUser);
	}

	protected void addMessage(HalawaErrorCode errorCode) {

		if (errorCode != null) {
			addMessage(null, errorCode.getErrorCode());
		} else {

			addMessage(null, HalawaErrorCode.GENERIC_ERROR.getErrorCode());
		}
	}

	protected void addMessage(String clientId, String key) {

		addMessage(clientId, key, null, null);
	}

	protected void addMessage(String clientId, String key,
			FacesMessage.Severity severity, Object[] params) {

		FacesMessage message = null;
		if (severity != null) {
			message = getMessage(key, severity, params);
		} else {

			message = getMessage(key, FacesMessage.SEVERITY_ERROR, params);
		}
		context().addMessage(clientId, message);
	}

	protected void addMessage(String key, Object[] params) {

		addMessage(null, key, null, params);
	}

	protected void addMessage(String key, FacesMessage.Severity severity,
			Object... params) {

		addMessage(null, key, severity, params);
	}

	protected void addMessage(String key) {

		addMessage(null, key, null, null);
	}

	protected static FacesContext context() {
		return (FacesContext.getCurrentInstance());
	}

	public static FacesMessage getMessage(String key,
			FacesMessage.Severity messageSeverity, Object... params) {

		FacesMessage message = null;

		try {
			message = MessageFactory.getMessage(key, messageSeverity,
					formatParameters(params));

		} catch (Exception exception) {

			logger.fatal("Failed to load message with key " + key, exception);
		}
		if (message == null) {

			message = new FacesMessage("???" + key + "???");
		}
		return message;
	}

	public static FacesMessage getMessage(String key, Object... params) {

		FacesMessage message = null;

		try {
			message = MessageFactory.getMessage(key,
					FacesMessage.SEVERITY_ERROR, params);

		} catch (Exception exception) {

			logger.fatal("Failed to load message with key " + key, exception);
		}
		if (message == null) {

			message = new FacesMessage("???" + key + "???");
		}
		return message;
	}

	public int getPageSize() {

		return 10;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	protected SelectItem createEmptySelectItem() {

		SelectItem selectItem = new SelectItem();
		selectItem.setValue(null);
		selectItem.setLabel("-------");

		return selectItem;
	}

	protected void writeOutContent(final HttpServletResponse res, final File content,
			final String theFilename) {
		if (content == null)
			return;
		try {

			res.setContentType("application/force-download");

			res.addHeader("Content-Disposition", "attachment; filename=\""
					+ theFilename + "\"");
			res.setHeader("Pragma", "no-cache");
			res.setDateHeader("Expires", 0);
			res.setContentType("application/pdf");
			fastChannelCopy(Channels.newChannel(new FileInputStream(content)),
					Channels.newChannel(res.getOutputStream()));
		} catch (final IOException e) {

			throw new HalawaSystemException("Failed to copy the files", e);
		}
	}

	protected void fastChannelCopy(final ReadableByteChannel src,
			final WritableByteChannel dest) throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
		while (src.read(buffer) != -1) {
			buffer.flip();
			dest.write(buffer);
			buffer.compact();
		}
		buffer.flip();
		while (buffer.hasRemaining()) {
			dest.write(buffer);
		}
	}

	private static Object[] formatParameters(Object[] params) {

		if (params == null)
			return null;
		Object[] formattedParameters = new Object[params.length];
		for (int i = 0; i < params.length; i++) {

			Object param = params[i];
			if (param instanceof java.util.Date) {

				formattedParameters[i] = ERRORMESSAGE_DATETIMEFORMATTER
						.format((java.util.Date) param);
			} else {

				formattedParameters[i] = param;
			}
		}
		return formattedParameters;
	}
}