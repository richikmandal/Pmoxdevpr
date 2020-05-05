package com.techm.pmo.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.techm.pmo.dao.UserDao;
import com.techm.pmo.model.User;

@Service("UserDao")
public class UserDaoImpl implements UserDao {
  
  @Autowired
  @Qualifier("jdbcMysql")
  private JdbcTemplate jdbcMysql;
  
  
  private String getPmrSmryDataFrUser = "select count(projectid) projectcount,sum(Onsite_Active_Head_Count) onactivheadcnt ,sum(Offshore_Active_Head_Count) offactivheadcnt from t_pmr where PM_Delegate_ID= ? ";
  
  private String getAllDtls = "SELECT GID,NAME,ROLE FROM T_USER WHERE GID = ? AND PWD = ?";
  
  private String getUserDtls = "select GID,NAME,listagg(ROLE, ', ') within group (order by ROLE) AS ROLE from PMOX.T_USER WHERE GID = ? AND PWD = ? GROUP BY GID,NAME";
  
  private String getAssociateCount = "with rws as ( select ON_OFF from PMOX.T_RESOURCE_BASE ) select * from rws pivot ( count(*) for ON_OFF in ('ONSITE' ONSITE, 'OFFSHORE' OFFSHORE ) )";
  
  
  @Override
  public User registerUser(@Valid User managedUserVM, String password) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User createUser(@Valid User user) {
    // TODO Auto-generated method stub
    return null;
  }

 
  @Override
  public User getUserWithRoleByUsername(String username) {
    final RowMapper<User> mapper = new ProjectRowMapper();
    
    //User usr = jdbcMysql.queryForObject(getPmrSmryDataFrUser, mapper, user.getUsername());
    List<User> usr = jdbcMysql.query(getPmrSmryDataFrUser,new PreparedStatementSetter() {
           
        public void setValues(PreparedStatement preparedStatement) throws SQLException {
           //preparedStatement.setString(1, user.getUsername());
        }
     }, mapper);
    
    return usr.get(0);

}

public class ProjectRowMapper implements RowMapper {
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User usr = new User();
        // SELECT tcid, tcname,tcqryone,tcqrytwo,tcdsone,tcdstwo,active FROM
        // USER_TESTCASES
    //  usr.setProjectCount(rs.getInt(1));
     // usr.setOnAscCount(rs.getInt(2));
     // usr.setOffAscCount(rs.getInt(3));
        return usr;
    }

}
  @Override
  public User findOneWithRoleByUsername(String username) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User isValidUser(@Valid User user) {

 final RowMapper<User> mapper = new UserRowMapper();
 //user.setUsername("224001");
 //user.setPassword("lalit@123");
    //User usr = jdbcMysql.queryForObject(getPmrSmryDataFrUser, mapper, user.getUsername());
    List<User> usr = jdbcMysql.query(getUserDtls,new PreparedStatementSetter() {
     
        public void setValues(PreparedStatement preparedStatement) throws SQLException {
           preparedStatement.setString(1, user.getUsername());
           preparedStatement.setString(2, user.getPassword());
        }
     }, mapper);
    
    return usr.get(0);
    //return user;
  }
  


public class UserRowMapper implements RowMapper {
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User usr = new User();
        // SELECT tcid, tcname,tcqryone,tcqrytwo,tcdsone,tcdstwo,active FROM
        // USER_TESTCASES
      usr.setUsername(rs.getString(1));
      usr.setName(rs.getString(2));
      usr.setRoleName(rs.getString(3));
        return usr;
    }

}

@Override
public User getUserDtls(String username) {
  // TODO Auto-generated method stub
  final RowMapper<User> mapper = new UserDtlRowMapper();
  
  //User usr = jdbcMysql.queryForObject(getPmrSmryDataFrUser, mapper, user.getUsername());
  List<User> usr = jdbcMysql.query(getUserDtls,new PreparedStatementSetter() {
         
      public void setValues(PreparedStatement preparedStatement) throws SQLException {
         preparedStatement.setString(1, username);
         //preparedStatement.setString(2, user.getPassword());
      }
   }, mapper);
  
  return usr.get(0);
  //return user;
}



public class UserDtlRowMapper implements RowMapper {
  public User mapRow(ResultSet rs, int rowNum) throws SQLException {
    User usr = new User();
      // SELECT tcid, tcname,tcqryone,tcqrytwo,tcdsone,tcdstwo,active FROM
      // USER_TESTCASES
    usr.setUsername(rs.getString(1));
    usr.setName(rs.getString(2));
    usr.setRoleName(rs.getString(3));
      return usr;
  }

}
@Override
public User getUserDtls(User user) {
	// TODO Auto-generated method stub
	 final RowMapper<User> mapper = new UserDtlRowMapper();
	 List<User> usr = jdbcMysql.query(getAllDtls,new PreparedStatementSetter() {
	     
	        public void setValues(PreparedStatement preparedStatement) throws SQLException {
	           preparedStatement.setString(1, user.getUsername());
	          
	        }
	     }, mapper);
	 
	return usr.get(0);
}

}
