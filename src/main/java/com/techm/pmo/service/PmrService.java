package com.techm.pmo.service;

import java.util.List;

import com.techm.pmo.dto.PmrData;
import com.techm.pmo.model.Casum;
import com.techm.pmo.model.PrjmasterData;
import com.techm.pmo.model.Profit_Loss;
import com.techm.pmo.model.User;

public interface PmrService {

  public User getPmrDataFrUser(User user);

  public List<PmrData> getPmrSmryDataFrUser(String userId);

  public List<Profit_Loss> getP_L(String user);
  
  public List<Casum> getCasum(String user);
  
  public List<PrjmasterData> getPrjMasterDataFrUser(String userId);
  
}
