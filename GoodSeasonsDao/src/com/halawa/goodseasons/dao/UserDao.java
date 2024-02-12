package com.halawa.goodseasons.dao;

import java.util.List;

import com.halawa.goodseasons.model.entity.Privilege;
import com.halawa.goodseasons.model.entity.User;

public interface UserDao {

	User loadUserByUsername(String username);
	User loadUserById(Long userId);
	/**
	 * Load all ACTIVE user privileges.
	 * This method load all type of privileges.
	 * @param user
	 * @return
	 */
	List<Privilege> loadUserPrivileges(User user);
	/**
	 * Load all ACTIVE user privileges which associated with right menus.
	 * This method loads only the privileges of type right menu.
	 * @param user
	 * @return
	 */
	List<Privilege> loadUserMenuPrivileges(User user);
	List<User> loadActiveUsers();
	void changePassword(User user, String newPassword);
}
