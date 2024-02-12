package com.halawa.goodseasons.service;

import java.util.List;

import com.halawa.common.exception.HalawaBusinessException;
import com.halawa.goodseasons.model.entity.Privilege;
import com.halawa.goodseasons.model.entity.User;

public interface UserService {

	User login(String username, String password) throws HalawaBusinessException;
	List<Privilege> loadUserPrivileges(User user);
	List<Privilege> loadUserMenuPrivileges(User user);
	List<User> loadActiveUsers();
	List<User> loadActiveUsersExcept(User loggedInUser);
	User loadActiveUserById(Long userId) throws HalawaBusinessException;
	void changePassword(User user, String newPassword) throws HalawaBusinessException;
}
