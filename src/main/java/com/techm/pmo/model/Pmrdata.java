package com.techm.pmo.model;
// Generated Jul 29, 2019 8:35:46 PM by Hibernate Tools 4.3.5.Final

import static javax.persistence.GenerationType.IDENTITY;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.BatchSize;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Pmrdata implements java.io.Serializable {

  private String projectId;
  private String pmId;
  private String janRev;
  private String febRev;
  private String marRev;
  private String aprRev;
  private String mayRev;
  private String junRev;
  private String julRev;
  private String augRev;
  private String sepRev;
  private String octRev;
  private String novRev;
  private String decRev;
  private String janEbi;
  private String febEbi;
  private String marEbi;
  private String aprEbi;
  private String mayEbi;
  private String junEbi;
  private String julEbi;
  private String augEbi;
  private String sepEbi;
  private String octEbi;
  private String novEbi;
  private String decEbi;
  
  public String getProjectId() {
    return projectId;
  }
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }
  public String getPmId() {
    return pmId;
  }
  public void setPmId(String pmId) {
    this.pmId = pmId;
  }
  public String getJanRev() {
    return janRev;
  }
  public void setJanRev(String janRev) {
    this.janRev = janRev;
  }
  public String getFebRev() {
    return febRev;
  }
  public void setFebRev(String febRev) {
    this.febRev = febRev;
  }
  public String getMarRev() {
    return marRev;
  }
  public void setMarRev(String marRev) {
    this.marRev = marRev;
  }
  public String getAprRev() {
    return aprRev;
  }
  public void setAprRev(String aprRev) {
    this.aprRev = aprRev;
  }
  public String getMayRev() {
    return mayRev;
  }
  public void setMayRev(String mayRev) {
    this.mayRev = mayRev;
  }
  public String getJunRev() {
    return junRev;
  }
  public void setJunRev(String juneRev) {
    this.junRev = juneRev;
  }
  public String getJulRev() {
    return julRev;
  }
  public void setJulRev(String julyRev) {
    this.julRev = julyRev;
  }
  public String getAugRev() {
    return augRev;
  }
  public void setAugRev(String augRev) {
    this.augRev = augRev;
  }
  public String getSepRev() {
    return sepRev;
  }
  public void setSepRev(String sepRev) {
    this.sepRev = sepRev;
  }
  public String getOctRev() {
    return octRev;
  }
  public void setOctRev(String octRev) {
    this.octRev = octRev;
  }
  public String getNovRev() {
    return novRev;
  }
  public void setNovRev(String novRev) {
    this.novRev = novRev;
  }
  public String getDecRev() {
    return decRev;
  }
  public void setDecRev(String decRev) {
    this.decRev = decRev;
  }
  public String getJanEbi() {
    return janEbi;
  }
  public void setJanEbi(String janEbi) {
    this.janEbi = janEbi;
  }
  public String getFebEbi() {
    return febEbi;
  }
  public void setFebEbi(String febEbi) {
    this.febEbi = febEbi;
  }
  public String getMarEbi() {
    return marEbi;
  }
  public void setMarEbi(String marEbi) {
    this.marEbi = marEbi;
  }
  public String getAprEbi() {
    return aprEbi;
  }
  public void setAprEbi(String aprEbi) {
    this.aprEbi = aprEbi;
  }
  public String getMayEbi() {
    return mayEbi;
  }
  public void setMayEbi(String mayEbi) {
    this.mayEbi = mayEbi;
  }
  public String getJunEbi() {
    return junEbi;
  }
  public void setJunEbi(String junEbi) {
    this.junEbi = junEbi;
  }
  public String getJulEbi() {
    return julEbi;
  }
  public void setJulEbi(String julEbi) {
    this.julEbi = julEbi;
  }
  public String getAugEbi() {
    return augEbi;
  }
  public void setAugEbi(String augEbi) {
    this.augEbi = augEbi;
  }
  public String getSepEbi() {
    return sepEbi;
  }
  public void setSepEbi(String sepEbi) {
    this.sepEbi = sepEbi;
  }
  public String getOctEbi() {
    return octEbi;
  }
  public void setOctEbi(String octEbi) {
    this.octEbi = octEbi;
  }
  public String getNovEbi() {
    return novEbi;
  }
  public void setNovEbi(String novEbi) {
    this.novEbi = novEbi;
  }
  public String getDecEbi() {
    return decEbi;
  }
  public void setDecEbi(String decEbi) {
    this.decEbi = decEbi;
  }
 
}
