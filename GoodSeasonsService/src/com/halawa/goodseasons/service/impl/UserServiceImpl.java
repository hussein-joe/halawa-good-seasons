package com.halawa.goodseasons.service.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;
import com.halawa.goodseasons.common.constants.UserErrorCodes;
import com.halawa.goodseasons.common.constants.UserStatusTypes;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.common.util.PasswordEncryption;
import com.halawa.goodseasons.common.util.StringUtil;
import com.halawa.goodseasons.dao.UserDao;
import com.halawa.goodseasons.model.entity.Privilege;
import com.halawa.goodseasons.model.entity.User;
import com.halawa.goodseasons.service.ConfigurationService;
import com.halawa.goodseasons.service.UserService;

public class UserServiceImpl implements UserService {
	
private static final HalawaLogger logger = HalawaLogger.getInstance(UserServiceImpl.class);
	
	private UserDao userDao = null;
	private ConfigurationService configurationService;
	
	public User login(String username, String password) throws HalawaBusinessException {
		
		User userObj = null;
		if ( username == null || password == null ) {
			
			logger.error("The passed username or password is null");
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER, "The passed username or password is null");
		}
		try {
			
			userObj = userDao.loadUserByUsername(username);
			
		} catch(DataAccessException dataAccessException) {
			
			logger.error("Failed to read from database");
			throw new HalawaSystemException(dataAccessException);
		}
		
		if ( userObj == null ) {
			
			throw new HalawaBusinessException(UserErrorCodes.INVALID_USER_NAME.getErrorCode());
		}
		String encryptedPassword = PasswordEncryption.encryptPassword(password);
		if ( !userObj.getPassword().equals(encryptedPassword) ) {
			
			throw new HalawaBusinessException(UserErrorCodes.INVALID_PASSWORD.getErrorCode());
		}
		return userObj;
	}

	public List<Privilege> loadUserPrivileges(User user) {
		
		List<Privilege> userPrivileges = null;
		if ( user == null ) {
			
			logger.error("The passed user object is null, so the NOT_LOGGEDIN_USER will be used");
			user = new User();
			user.setId( Long.parseLong(configurationService.getProperty("user.notLoggedInUserId")) );
		}
		try {
			
			userPrivileges = userDao.loadUserPrivileges(user);
			
		} catch(DataAccessException dataAccessException) {
			
			logger.error("Failed to read user privileges from database");
			throw new HalawaSystemException("Failed to load the user privileges", dataAccessException);
		}
		
		return userPrivileges;
	}
	
	public List<Privilege> loadUserMenuPrivileges(User user) {
		
		List<Privilege> userMenuPrivileges = null;
		if ( user == null ) {
			
			logger.info("The passed user object is null, so the NOTLOGGEDIN_USER will be used");
			user = new User();
			user.setId( Long.parseLong(configurationService.getProperty("user.notLoggedInUserId")) );
		}
		
		try {
			
			userMenuPrivileges = userDao.loadUserMenuPrivileges(user);
			
		} catch(DataAccessException dataAccessException) {
			
			logger.error("Failed to read user menu privileges from database");
			throw new HalawaSystemException("Failed to load the user menu privileges", dataAccessException);
		}
		
		return userMenuPrivileges;
	}
	
	

	public List<User> loadActiveUsers() {
		
		return userDao.loadActiveUsers();
	}

	public List<User> loadActiveUsersExcept(User loggedInUser) {
		
		return null;
	}
	
	public User loadActiveUserById(Long userId) throws HalawaBusinessException {
		
		User loadedUser;
		logger.info("Loading the active user with id " + userId);
		loadedUser = this.userDao.loadUserById(userId);
		if ( loadedUser == null ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		if ( loadedUser.getStatus().shortValue() != UserStatusTypes.ACTIVE.getValue() ) {
			
			logger.error("The user with id " + userId + " is not active");
			throw new HalawaBusinessException(UserErrorCodes.USER_IS_INACTIVE.getErrorCode());
		}
		return loadedUser;
	}
	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void changePassword(User user, String newPassword) {
		
		if ( user == null || StringUtil.isEmpty(newPassword) ) {
			
			throw new HalawaSystemException(HalawaErrorCode.INVALID_PARAMETER);
		}
		
		logger.info("Changing the password for user with id " + user.getId());
		String encryptedPassword = PasswordEncryption.encryptPassword(newPassword);
		this.userDao.changePassword(user, encryptedPassword);
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
}
