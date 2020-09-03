package com.techm.pmo.service;

import java.util.List;

import com.techm.pmo.dto.PmrData;
import com.techm.pmo.model.Casum;
import com.techm.pmo.model.CasumData;
import com.techm.pmo.model.Pmrdata;
import com.techm.pmo.model.PrjmasterData;
import com.techm.pmo.model.ProfitAndLossData;
import com.techm.pmo.model.User;

public interface PmrService {

  public User getPmrDataFrUser(User user);

  public List<PmrData> getPmrSmryDataFrUser(String userId);
  public List<ProfitAndLossData> getP_L(String user);
  
  public List<Casum> getCasum(String user);
  
  public List<PrjmasterData> getPrjMasterDataFrUser(String userId);

  public List<Pmrdata> getPmSeriesData(User user);

  public List<ProfitAndLossData> getPnLSummary(User user);

  public List<CasumData> getPoReceived(User user);
  
}
