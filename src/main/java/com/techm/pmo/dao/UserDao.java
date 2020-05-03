package com.techm.pmo.dao;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.techm.pmo.model.User;

public interface UserDao {
  
  public User isValidUser(@Valid User user) ;
  
  public User registerUser(@Valid User managedUserVM, String password);

  public User createUser(@Valid User user);


  public User getUserWithRoleByUsername(String username);
  
  public User findOneWithRoleByUsername(String username);

  public User getUserDtls(String username);
  
  public User getUserDtls(User usr);

}
