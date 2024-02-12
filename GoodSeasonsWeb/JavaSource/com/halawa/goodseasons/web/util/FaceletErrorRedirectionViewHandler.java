package com.halawa.goodseasons.web.util;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.sun.facelets.FaceletViewHandler;

public class FaceletErrorRedirectionViewHandler extends FaceletViewHandler {

	private static final HalawaLogger logger = HalawaLogger.getInstance(FaceletErrorRedirectionViewHandler.class);
	
	public FaceletErrorRedirectionViewHandler(ViewHandler parent) {
		
		super(parent);
	}

	@Override
    protected void handleRenderException(FacesContext context, Exception ex) throws IOException, ELException,
        FacesException {
        try {
            if (context.getViewRoot().getViewId().equals("/pages/common/genericErrorPage.jsf")) {
                /*
                 * This is to protect from infinite redirects if the error
                 * page itself is updated in the future and has an error
                 */
                logger.error("Redirected back to ourselves, there must be a problem with the error.xhtml page", ex);
                return;
            }
            
            logger.error("An error occured while rendering the page " + context.getViewRoot().getViewId(), ex);
            FacesUtil.setSessionAttribute("GLOBAL_RENDER_ERROR", ex);
            
            HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
            response.sendRedirect( ((HttpServletRequest) context.getExternalContext().getRequest()).getContextPath() + "/pages/common/genericErrorPage.jsf");
            
            
        } catch (IOException ioe) {
            logger.fatal("Could not process redirect to handle application error", ioe);
        }
    }
}
