package com.halawa.goodseasons.web.util;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

public class FacesUtil {

	public static String getRequestParameter(String name) {
        return (String) FacesContext.getCurrentInstance().getExternalContext()
            .getRequestParameterMap().get(name);
    }
	
	public static Object getSessionAttribute(String name) {
		
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(name);
    }
	
	public static void setSessionAttribute(String name, Object attributeValue) {
		
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, attributeValue);
    }
	
	public static void removeSessionAttribute(String name) {
		
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(name);
	}
	
	public static String getActionAttribute(ActionEvent event, String name) {
        return (String) event.getComponent().getAttributes().get(name);
    }
}
