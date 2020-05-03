package com.techm.pmo.conroller;

import java.util.HashMap;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.sun.javafx.collections.MappingChange.Map;
import com.techm.pmo.error.EmailAlreadyUsedException;
import com.techm.pmo.error.InvalidPasswordException;
import com.techm.pmo.error.LoginAlreadyUsedException;
import com.techm.pmo.model.User;
import com.techm.pmo.service.UserService;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class UserController {

  private final Logger log = LoggerFactory.getLogger(UserController.class);

  @Resource
  private UserService  userService;


  /**
   * POST /register : register the user.
   *
   * @param managedUserVM the managed user View Model
   * @throws InvalidPasswordException 400 (Bad Request) if the password is incorrect
   * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
   * @throws LoginAlreadyUsedException 400 (Bad Request) if the login is already used
   */
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void registerAccount(@Valid @RequestBody User managedUserVM) {
    userService.registerUser(managedUserVM, managedUserVM.getPassword());
  }

  /**
   * GET /authenticate : check if the user is authenticated, and return its login.
   *
   * @param request the HTTP request
   * @return the login if the user is authenticated
   */
  @PostMapping("/authenticate")
  public String isAuthenticated(@RequestBody User usr, HttpServletRequest request) {
    log.debug("REST request to check if the current user is authenticated");
    log.info("user details" + usr.getName());
    Gson gsonRes = new Gson();
    HashMap<String, User> mp = new HashMap<String, User>();
    mp.put("user", userService.isValidUser(usr));
    return gsonRes.toJson(mp);
  }

  /**
   * POST /users : Creates a new user.
   * <p>
   * Creates a new user if the login and email are not already used, and sends an mail with an
   * activation link. The user needs to be activated on creation.
   *
   * @param userDTO the user to create
   * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status
   *         400 (Bad Request) if the login or email is already in use
   * @throws URISyntaxException if the Location URI syntax is incorrect
   * @throws BadRequestAlertException 400 (Bad Request) if the login or email is already in use
   */
  
   
  /*
   * @PostMapping("/users")
   * 
   * @PreAuthorize("hasRole(\"" + AuthoritiesConstants.AM + "\")") public ResponseEntity<User>
   * createUser(@Valid @RequestBody User user) throws URISyntaxException {
   * log.debug("REST request to save User : {}", user);
   * 
   * if (user.getUsername() != null) { throw new
   * BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
   * // Lowercase the user login before comparing with database } else if
   * (userRepository.findOneByUsername(user.getUsername().toLowerCase()).isPresent()) { throw new
   * LoginAlreadyUsedException(); } else { User newUser = userService.createUser(user); return
   * ResponseEntity.created(new URI("/api/users/" + newUser.getUsername()))
   * .headers(HeaderUtil.createAlert("userManagement.created",
   * newUser.getUsername())).body(newUser); } }
   */
   

  /**
   * @return a string list of the all of the roles
   */
  /*
   * @GetMapping("/users/authorities")
   * 
   * @PreAuthorize("hasRole(\"" + AuthoritiesConstants.AM + "\")") public List<String>
   * getAuthorities() { return userService.getRoles(); }
   */

  /**
   * GET /users/:login : get the "login" user.
   *
   * @param login the login of the user to find
   * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status
   *         404 (Not Found)
   */
  @GetMapping("/users/{username}")
  public ResponseEntity<User> getUser(@PathVariable String username) {
    log.debug("REST request to get User : {}", username);
    User usr = new User();
    usr.setUsername(username);
    return new ResponseEntity<User>(userService.userDetails(usr), HttpStatus.OK);
  }

}
