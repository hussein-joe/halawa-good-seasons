package com.halawa.goodseasons.web.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class BranchUserSession {

	
	public static Object getUserSessionProperty(String propertyName) {
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		return session.getAttribute(propertyName);
	}
	
	public static void addSessionProperty(String propertyName, Object propertyValue) {
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.setAttribute(propertyName, propertyValue);
	}
	
	public static void removeSessionProperty(String propertyName) {
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.removeAttribute(propertyName);
	}
}
