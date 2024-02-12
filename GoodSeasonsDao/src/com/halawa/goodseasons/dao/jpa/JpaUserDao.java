package com.halawa.goodseasons.dao.jpa;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.halawa.goodseasons.common.constants.Status2Values;
import com.halawa.goodseasons.common.logger.HalawaLogger;
import com.halawa.goodseasons.dao.BaseDao;
import com.halawa.goodseasons.dao.UserDao;
import com.halawa.goodseasons.model.entity.Privilege;
import com.halawa.goodseasons.model.entity.User;

public class JpaUserDao extends BaseDao implements UserDao {
	
	private static final HalawaLogger logger = HalawaLogger.getInstance(JpaUserDao.class);
	
	public User loadUserById(Long userId) {
		
		return (User) entityManager.find(User.class, userId);
	}

	public User loadUserByUsername(String username) {
		
		User loadedUser = null;
		
		try {
			Query query = entityManager.createNamedQuery("findUserByUsername");
			query.setParameter("required_username", username.toLowerCase());
			loadedUser = (User) query.getSingleResult();
		} catch(NoResultException noResultException) {
			
			logger.error("No user exist with name " + username, noResultException);
		}
		
		return loadedUser;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Privilege> loadUserPrivileges(User user) {
		
		Query query = entityManager.createNamedQuery("loadUserPrivileges");
		query.setParameter("userObj", user);
		query.setParameter("privilegeStatus", Status2Values.ACTIVE.getValue());
		List<Privilege> userPrivilges = query.getResultList();
		return userPrivilges;
	}
	
	@SuppressWarnings("unchecked")
	public List<Privilege> loadUserMenuPrivileges(User user) {
		
		Query query = entityManager.createNamedQuery("loadMenuUserPrivileges");
		query.setParameter("userObj", user);
		query.setParameter("privilegeStatus", Status2Values.ACTIVE.getValue());
		List<Privilege> userPrivilges = query.getResultList();
		return userPrivilges;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> loadActiveUsers() {
		
		Query query = entityManager.createNamedQuery("loadActiveUsers");
		query.setParameter("activeUserStatus", Status2Values.ACTIVE.getValue());
		return (List<User>) query.getResultList();
	}

	public void changePassword(User user, String newPassword) {
		
		Query query = this.entityManager.createNamedQuery("changeUserPassword");
		query.setParameter("userId", user.getId());
		query.setParameter("newPassword", newPassword);
		query.executeUpdate();
	}
}
