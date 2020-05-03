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

/**
 * TOsHrUser generated by hbm2java
 */
/*@Entity
@Table(name = "t_pmr", catalog = "pmox")*/
public class ResourceBaseData implements java.io.Serializable {

	private String	projectId;
	private String	projectDescription	;
	private String empId  ;
	private String empName  ;
	private String  gender  ;
	private String  catCode    ;
	private String htrFlag    ;
	private String	cluster	;
	private String	email;
	private String ibg ;
	private String ibu ;
	private String band    ;
    private String  experience   ;
    private String  country  ;
    private String  city    ;
    private String  onOff  ;
    private String  regContract    ;
    public String getProjectId() {
      return projectId;
    }
    public void setProjectId(String projectId) {
      this.projectId = projectId;
    }
    public String getProjectDescription() {
      return projectDescription;
    }
    public void setProjectDescription(String projectDescription) {
      this.projectDescription = projectDescription;
    }
    public String getEmpId() {
      return empId;
    }
    public void setEmpId(String empId) {
      this.empId = empId;
    }
    public String getEmpName() {
      return empName;
    }
    public void setEmpName(String empName) {
      this.empName = empName;
    }
    public String getGender() {
      return gender;
    }
    public void setGender(String gender) {
      this.gender = gender;
    }
    public String getCatCode() {
      return catCode;
    }
    public void setCatCode(String catCode) {
      this.catCode = catCode;
    }
    public String getHtrFlag() {
      return htrFlag;
    }
    public void setHtrFlag(String htrFlag) {
      this.htrFlag = htrFlag;
    }
    public String getCluster() {
      return cluster;
    }
    public void setCluster(String cluster) {
      this.cluster = cluster;
    }
    public String getEmail() {
      return email;
    }
    public void setEmail(String email) {
      this.email = email;
    }
    public String getIbg() {
      return ibg;
    }
    public void setIbg(String ibg) {
      this.ibg = ibg;
    }
    public String getIbu() {
      return ibu;
    }
    public void setIbu(String ibu) {
      this.ibu = ibu;
    }
    public String getBand() {
      return band;
    }
    public void setBand(String band) {
      this.band = band;
    }
    public String getExperience() {
      return experience;
    }
    public void setExperience(String experience) {
      this.experience = experience;
    }
    public String getCountry() {
      return country;
    }
    public void setCountry(String country) {
      this.country = country;
    }
    public String getCity() {
      return city;
    }
    public void setCity(String city) {
      this.city = city;
    }
    public String getOnOff() {
      return onOff;
    }
    public void setOnOff(String onOff) {
      this.onOff = onOff;
    }
    public String getRegContract() {
      return regContract;
    }
    public void setRegContract(String regContract) {
      this.regContract = regContract;
    }
    

}
