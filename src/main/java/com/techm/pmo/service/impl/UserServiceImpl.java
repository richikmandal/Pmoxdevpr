package com.techm.pmo.service.impl;

import java.util.Optional;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.techm.pmo.dao.UserDao;
import com.techm.pmo.model.User;
import com.techm.pmo.service.UserService;

/**
 * Service class for managing users.
 */
@Service("userService")
public class UserServiceImpl implements UserService {


	@Resource
	private UserDao userDao;


  @Override
  public User isValidUser(@Valid User user) {
    // TODO Auto-generated method stub
    return userDao.isValidUser(user);
  }




  @Override
  public void registerUser(@Valid User managedUserVM, String password) {
    // TODO Auto-generated method stub
    
  }




  @Override
  public User createUser(@Valid User user) {
    // TODO Auto-generated method stub
    return null;
  }




  @Override
  public User getUserWithRoleByUsername(String username) {
    // TODO Auto-generated method stub
    return userDao.getUserDtls(username);
  }
  

@Override
public User userDetails(User usr) {
    // TODO Auto-generated method stub
      return userDao.getUserDtls(usr);
}


}
