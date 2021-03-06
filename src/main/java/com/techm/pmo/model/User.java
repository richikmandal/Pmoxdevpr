package com.techm.pmo.model;
// Generated Jul 29, 2019 8:35:46 PM by Hibernate Tools 4.3.5.Final

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class User implements java.io.Serializable {


  private static final long serialVersionUID = 6740709927694633749L;
  
  private String    username;
  private String    password;
 // private boolean   enabled;
  private String    name;
 private String roleName;
 private int totalProjectCount =0;
 private int totalOffShoreCount =0;
 private int totalOnShoreCount =0;
 private String totalRevenue;
 private String totalEbidta;
 private String projectSelected;
 private List<PrjmasterData> projMasterData = new ArrayList<PrjmasterData>();
 private Map<String, List<ResourceBaseData>> resourceMap = new HashMap<String, List<ResourceBaseData>>();
 private Map<String, List<ProfitAndLossData>> pandlMap = new HashMap<String, List<ProfitAndLossData>>();
 
 private String sbuName;
 private String ibgName;
 private String ibuName;
 private String spgmName;
 private String pgmName;
 private String pmName;
 private String prjNme;
 private String filterRoleSel;
 private String onOff;
 private int onOffCnt;

  public User() {}

  /*
   * public User(boolean enabled) { this.enabled = enabled; }
   */

  public User(String username, String password, String name) {
    this.username = username;
    this.password = password;
    //this.enabled = enabled;
    this.name = name;
  }
  
  public User(String totalRevenue, String totalEbidta) {
    this.totalRevenue = totalRevenue;
    this.totalEbidta = totalEbidta;
    //this.enabled = enabled;
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public int getTotalProjectCount() {
    return totalProjectCount;
  }

  public void setTotalProjectCount(int totalProjectCount) {
    this.totalProjectCount = totalProjectCount;
  }

  public int getTotalOffShoreCount() {
    return totalOffShoreCount;
  }

  public void setTotalOffShoreCount(int totalOffShoreCount) {
    this.totalOffShoreCount = totalOffShoreCount;
  }

  public int getTotalOnShoreCount() {
    return totalOnShoreCount;
  }

  public void setTotalOnShoreCount(int totalOnShoreCount) {
    this.totalOnShoreCount = totalOnShoreCount;
  }

  public List<PrjmasterData> getProjMasterData() {
    return projMasterData;
  }

  public void setProjMasterData(List<PrjmasterData> projMasterData) {
    this.projMasterData = projMasterData;
  }

  public String getTotalRevenue() {
    return totalRevenue;
  }

  public void setTotalRevenue(String totalRevenue) {
    this.totalRevenue = totalRevenue;
  }

  public String getTotalEbidta() {
    return totalEbidta;
  }

  public void setTotalEbidta(String totalEbidta) {
    this.totalEbidta = totalEbidta;
  }

  public String getProjectSelected() {
    return projectSelected;
  }

  public void setProjectSelected(String projectSelected) {
    this.projectSelected = projectSelected;
  }

  public Map<String, List<ResourceBaseData>> getResourceMap() {
    return resourceMap;
  }

  public void setResourceMap(Map<String, List<ResourceBaseData>> resourceMap) {
    this.resourceMap = resourceMap;
  }

  public Map<String, List<ProfitAndLossData>> getPandlMap() {
    return pandlMap;
  }

  public void setPandlMap(Map<String, List<ProfitAndLossData>> pandlMap) {
    this.pandlMap = pandlMap;
  }

    public String getSbuName() {
      return sbuName;
    }
  
    public void setSbuName(String sbuName) {
      this.sbuName = sbuName;
    }
  
    public String getIbgName() {
      return ibgName;
    }
  
    public void setIbgName(String ibgName) {
      this.ibgName = ibgName;
    }
  
    public String getIbuName() {
      return ibuName;
    }
  
    public void setIbuName(String ibuName) {
      this.ibuName = ibuName;
    }
  
    public String getSpgmName() {
      return spgmName;
    }
  
    public void setSpgmName(String spgmName) {
      this.spgmName = spgmName;
    }
  
    public String getPgmName() {
      return pgmName;
    }
  
    public void setPgmName(String pgmName) {
      this.pgmName = pgmName;
    }
  
    public String getPmName() {
      return pmName;
    }
  
    public void setPmName(String pmName) {
      this.pmName = pmName;
    }
  
    public String getPrjNme() {
      return prjNme;
    }
  
    public void setPrjNme(String prjNme) {
      this.prjNme = prjNme;
    }

    public String getFilterRoleSel() {
      return filterRoleSel;
    }

    public void setFilterRoleSel(String filterRoleSel) {
      this.filterRoleSel = filterRoleSel;
    }

    public String getOnOff() {
      return onOff;
    }

    public void setOnOff(String onOff) {
      this.onOff = onOff;
    }

    public int getOnOffCnt() {
      return onOffCnt;
    }

    public void setOnOffCnt(int onOffCnt) {
      this.onOffCnt = onOffCnt;
    }
     
}
