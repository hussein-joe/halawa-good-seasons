package com.halawa.goodseasons.web.backing.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.model.entity.Privilege;
import com.halawa.goodseasons.service.UserService;
import com.halawa.goodseasons.web.backing.AbstractBean;
import com.halawa.goodseasons.web.entity.WebUser;
import com.halawa.goodseasons.web.util.FacesUtil;

public class MenuBean extends AbstractBean {
	
	private static final HalawaLogger logger = HalawaLogger.getInstance(MenuBean.class);
	public static final String FORWARD_LOGIN = "login";
	
	private UserService userService;
	private Map<String, Privilege> userPrivilegesMap;
	private String lastSelectedMenu;
	
	public MenuBean() {
		
		this.userPrivilegesMap = null;
	}
	
	public String goToDest() {
		
		String dest = FacesUtil.getRequestParameter("dest");
		String privilegeName = FacesUtil.getRequestParameter("pName");
		
		if ( privilegeName != null && privilegeName.trim().length() > 0 ) {
			
			boolean hasPrivilege = this.hasPrivilege(privilegeName);
			if ( !hasPrivilege ) {
				
				logger.error("The user is trying to click on menu with privilege " + privilegeName + 
						" and he is not logged in or has no privilege to do that");
				this.removeLoadedUserMenu();
				super.removeLoggedInUserFromSession();
				dest = "login";
			}
		}
		lastSelectedMenu = dest;
		return dest;
	}

	public boolean isUserLoggedIn() {
		
		WebUser webUser = super.getLoggedInWebUser();
		if ( webUser == null || webUser.getUser() == null ) {
			
			return false;
		}
		return true;
	}
	
	public void loadLoggedInUserMenus() {
		
		try {
			this.userPrivilegesMap = new HashMap<String, Privilege>();
			List<Privilege> userPrivileges = userService.loadUserMenuPrivileges(super.getLoggedInWebUser().getUser());
			fillUserPrivilegesMap(userPrivileges);
		} catch(HalawaSystemException halawaSystemException) {
			
			logger.fatal("Failed to load the user right menus ", halawaSystemException);
			super.addMessage(halawaSystemException.getErrorCode());
		} catch(Exception exception) {
			
			logger.fatal("Unknow error while loading the user right menus ", exception);
			super.addMessage(HalawaErrorCode.GENERIC_ERROR);
		}
	}

	public boolean hasPrivilege() {
		
		return hasPrivilege( FacesUtil.getRequestParameter("pName") );
	}
	
	public boolean hasPrivilege(String menuPrivilege) {
		
		Privilege privilege = this.userPrivilegesMap.get(menuPrivilege);
		if ( privilege == null ) {
			
			return false;
		}
		return true;
	}
	
	public Map<String, Privilege> getUserPrivilegesMap() {
	
		if ( this.userPrivilegesMap == null ) {
		
			try {
				this.userPrivilegesMap = new HashMap<String, Privilege>();
				List<Privilege> userPrivileges = userService.loadUserMenuPrivileges(null);
				fillUserPrivilegesMap(userPrivileges);
			} catch(HalawaSystemException halawaSystemException) {
				
				logger.fatal("Failed to load the default right menus for not-loggedin user ", halawaSystemException);
				super.addMessage(halawaSystemException.getErrorCode());
			} catch(Exception exception) {
				
				logger.fatal("Unknow error while loading the not-loggedin user right menus", exception);
				super.addMessage(HalawaErrorCode.GENERIC_ERROR);
			}
		}
		return userPrivilegesMap;
	}
	
	protected void removeLoadedUserMenu() {
		
		if ( this.userPrivilegesMap != null ) {
			
			this.userPrivilegesMap.clear();
			this.userPrivilegesMap = null;
		}
		this.lastSelectedMenu = null;
	}
	
	private void fillUserPrivilegesMap(List<Privilege> userPrivileges) {
		
		if ( userPrivileges != null ) {
			
			for(Privilege priv:userPrivileges) {
				
				this.userPrivilegesMap.put(priv.getLocalName(), priv);
			}
		}
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public String getLastSelectedMenu() {
		return lastSelectedMenu;
	}
}
