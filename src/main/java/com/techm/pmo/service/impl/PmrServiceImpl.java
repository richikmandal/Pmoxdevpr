package com.techm.pmo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.pmo.dao.PmrprojDao;
import com.techm.pmo.dto.PmrData;
import com.techm.pmo.model.Casum;
import com.techm.pmo.model.PrjmasterData;
import com.techm.pmo.model.ProfitAndLossData;
import com.techm.pmo.model.User;
import com.techm.pmo.service.PmrService;

/**
 * Service class for managing users.
 */
@Service("pmrService")
public class PmrServiceImpl implements PmrService {
  
  
	@Autowired
	private PmrprojDao pmrprojdao;
	@Override
	  public User getPmrDataFrUser(User user) {
	    // TODO Auto-generated method stub
	  
	  user = pmrprojdao.getPmrDataFrUser(user);
	 // if(user.getRoleName().equals("PGM")||user.getRoleName().equals("PM")) {
	    user = pmrprojdao.getProjectMasterDataForPGM(user);
	    user = pmrprojdao.getResourceDataForPGM(user);
	  //}
	  
	    return user;
	  }

	  @Override
	  public List<PmrData> getPmrSmryDataFrUser(String userId) {
	    // TODO Auto-generated method stub
	    return pmrprojdao.getPmrSmryDataFrUser(userId);
	  }
	  
      @Override
      public List<ProfitAndLossData> getP_L(String user) {
      	// TODO Auto-generated method stub
      	return pmrprojdao.getP_L(user);
      }

	@Override
	public List<Casum> getCasum(String user) {
		// TODO Auto-generated method stub
		return pmrprojdao.getCasum(user);
	}

	@Override
	public List<PrjmasterData> getPrjMasterDataFrUser(String userId) {
		// TODO Auto-generated method stub
		return pmrprojdao.getPrjMasterDataFrUser(userId);
	}




}
