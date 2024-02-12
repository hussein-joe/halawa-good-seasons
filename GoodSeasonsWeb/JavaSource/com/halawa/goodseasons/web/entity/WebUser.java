package com.halawa.goodseasons.web.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.halawa.goodseasons.common.constants.PrivilegeType;
import com.halawa.goodseasons.common.constants.PrivilegesEnum;
import com.halawa.goodseasons.model.entity.Privilege;
import com.halawa.goodseasons.model.entity.User;

public class WebUser {

	private User user;
	private Map<String, Privilege> rightMenuPrivileges;
	private Map<String, Privilege> userPrivileges;
	
	public WebUser() {
		
		this.rightMenuPrivileges = new HashMap<String, Privilege>();
		this.userPrivileges = new HashMap<String, Privilege>();
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setPrivileges(List<Privilege> privileges) {
		
		if ( privileges != null ) {
			
			for(Privilege privilege: privileges) {
				
				if ( privilege.getType() == PrivilegeType.RIGHT_MENU_PRIVILEGE.getType() ) {
					
					this.rightMenuPrivileges.put(privilege.getLocalName(), privilege);
				} else {
					
					this.userPrivileges.put(privilege.getLocalName(), privilege);
				}
			}
		}
	}
	
	public boolean hasPrivilege(PrivilegesEnum privilege) {
		
		if ( privilege.getPrivilegeType() == PrivilegeType.RIGHT_MENU_PRIVILEGE ) {
			
			return this.rightMenuPrivileges.containsKey(privilege.getPrivilegeLocalName());
		} else {
			
			return this.userPrivileges.containsKey(privilege.getPrivilegeLocalName());
		}
	}
	
}
 