package com.halawa.goodseasons.web.backing.common;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.model.entity.User;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.entity.WebUser;

public class LoginBean extends AbstractBean{

	private static final HalawaLogger logger = HalawaLogger.getInstance(LoginBean.class);
	private String username;
	private String password;
	private String confirmedPassword;
	private MenuBean menuBean;
	private List<SelectItem> activeUserNames;
	
	public String getUsername() {
		
		this.username = (String) this.activeUserNames.get(0).getValue();
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<SelectItem> getActiveUserNames() {
		
		if ( this.activeUserNames == null ) {
			
			List<SelectItem> activeUsers = super.getActiveUsersList();
			this.activeUserNames = new ArrayList<SelectItem>();
			for( SelectItem user: activeUsers ) {
				
				SelectItem userNameItem = new SelectItem(user.getLabel(), user.getLabel());
				this.activeUserNames.add( userNameItem );
			}
		}
		
		return this.activeUserNames;
	}
	
	public String login() {
		
		logger.info("Loggin user " + this.username);
		WebUser loggedInWebUser = null;
		
		try {
			
			this.removeLoggedInUserFromSession();
			this.menuBean.removeLoadedUserMenu();
			
			User loggedInUser = userService.login(username, password);
			loggedInWebUser = new WebUser();
			loggedInWebUser.setUser(loggedInUser);
			loggedInWebUser.setPrivileges( userService.loadUserPrivileges(loggedInUser) );
			super.setLoggedInWebUser(loggedInWebUser);
			this.menuBean.loadLoggedInUserMenus();
			
			super.addMessage("login_welcomeMessage", FacesMessage.SEVERITY_INFO,super.getLoggedInWebUser().getUser().getUsername());
		} catch(HalawaBusinessException halawaBusinessException) {
			
			logger.error("Failed to login", halawaBusinessException);
			addMessage(halawaBusinessException.getErrorCode());
			return null;
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to login", exception);
			addMessage(exception.getErrorCode());
			return null;
		} catch(Exception exception) {
			
			logger.error("Failed to login", exception);
			addMessage(HalawaErrorCode.GENERIC_ERROR);
			return null;
		}
		
		return "userHome";
	}

	public String logout() {
		
		super.removeLoggedInUserFromSession();
		this.menuBean.removeLoadedUserMenu();
		ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
	    HttpSession session = (HttpSession)ectx.getSession(false);
	    session.invalidate();
	    
	    return "login";
	}
	
	public String changePassword() {
		
		try {
			if ( !StringUtil.isEmpty(this.password) && !StringUtil.isEmpty(confirmedPassword) && 
					this.password.equals(confirmedPassword)) {
				
				this.userService.changePassword(this.getLoggedInWebUser().getUser(), this.password);
			} else {
				
				throw new HalawaSystemException(new HalawaErrorCode("user_changePassword_passwordNotSameAsConfirmation"));
			}
		} catch(HalawaSystemException exception) {
			
			logger.error("Failed to change the password", exception);
			super.addMessage(exception.getErrorCode());
		} catch(Exception exception) {
			
			logger.error("Failed to change the password", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
		
		return "userHome";
	}
	
	
	public void setMenuBean(MenuBean menuBean) {
		this.menuBean = menuBean;
	}
	public String getConfirmedPassword() {
		return confirmedPassword;
	}
	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}
}
