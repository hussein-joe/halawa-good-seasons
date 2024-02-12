package com.halawa.goodseasons.web.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.logger.HalawaLogger;

public class ResourceBundleUtil {

	private static final HalawaLogger logger = HalawaLogger.getInstance(ResourceBundleUtil.class);
	private Map<String, ResourceBundle> branchResourceBundles;
	private static final ResourceBundleUtil instance = new ResourceBundleUtil();
	
	private ResourceBundleUtil() {
		
		this.branchResourceBundles = new HashMap<String, ResourceBundle>();
	}
	
	public static final ResourceBundleUtil getInstance() {
		
		return instance;
	}
	
	public ResourceBundle loadResourceBundle(String baseName) {
		
		ResourceBundle resourceBundle = this.branchResourceBundles.get(baseName);
		if ( resourceBundle == null ) {
			
			try {
				logger.info("Loading the resource bundle for the passed base name " + baseName);
				resourceBundle = ResourceBundle.getBundle(baseName);
				this.branchResourceBundles.put(baseName, resourceBundle);
				logger.info("The resource bundle for the passed base name loaded successfully");
			} catch(Exception exception) {
				
				logger.fatal("Failed to load the resource bundle for the passed base name " + baseName, exception);
				throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, 
							"Failed to load the resource bundle for the passed base name " + baseName);
			}
		}
		
		return resourceBundle;
	}
}
