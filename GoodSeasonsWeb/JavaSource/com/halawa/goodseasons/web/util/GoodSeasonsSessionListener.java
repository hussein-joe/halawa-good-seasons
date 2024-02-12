package com.halawa.goodseasons.web.util;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class GoodSeasonsSessionListener implements HttpSessionListener {
	
	public void sessionCreated(HttpSessionEvent event) {
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		
		HttpSession session = event.getSession();
		session.invalidate();
	}

}
