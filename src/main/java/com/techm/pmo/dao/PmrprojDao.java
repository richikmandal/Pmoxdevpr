package com.techm.pmo.dao;

import java.util.List;

import com.techm.pmo.dto.PmrData;
import com.techm.pmo.model.Casum;
import com.techm.pmo.model.PrjmasterData;
import com.techm.pmo.model.Profit_Loss;
import com.techm.pmo.model.User;

public interface PmrprojDao {
  
  public List<Profit_Loss> getP_L(String user);
  
  public User getPmrDataFrUser(User user);

  public List<PmrData> getPmrSmryDataFrUser(String userId);
  
  public List<Casum> getCasum(String user);
  
  public List<PrjmasterData> getPrjMasterDataFrUser(String userId);
  
  public User getProjectMasterDataForPGM(User user);
  
  public User getResourceDataForPGM(User user);

}
