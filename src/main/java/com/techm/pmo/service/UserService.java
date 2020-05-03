package com.techm.pmo.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.techm.pmo.model.User;

public interface UserService {

  void registerUser(@Valid User managedUserVM, String password);

  User createUser(@Valid User user);

  User getUserWithRoleByUsername(String username);
  
  public User isValidUser(@Valid User user);

  User userDetails(User usr);

}
