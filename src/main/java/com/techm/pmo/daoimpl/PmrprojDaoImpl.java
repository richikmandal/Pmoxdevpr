package com.techm.pmo.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.techm.pmo.dao.PmrprojDao;
import com.techm.pmo.dto.PmrData;
import com.techm.pmo.model.Casum;
import com.techm.pmo.model.PrjmasterData;
import com.techm.pmo.model.ProfitAndLossData;
import com.techm.pmo.model.ResourceBaseData;
import com.techm.pmo.model.User;

@Service
public class PmrprojDaoImpl implements PmrprojDao {

  @Autowired
  @Qualifier("jdbcMysql")
  private JdbcTemplate jdbcMysql;
  
  private String getPrjCntFrUser = "SELECT COUNT(*) FROM PMOX.T_PROJECT_MASTER ";
  
  private String getPmrDatafrUser = "SELECT Business_Unit, projectId, projectDesc, custId, IBU, IBUDescription,  IbuHeadName, Status, IBG_Description, Project_Main_Type, Project_Type, Project_Start_Date, "
      + " Project_End_Date, Program_Manager_Name,  PM_Delegate_Name,  Project_Manager_Name,  Active_Head_Count, Onsite_Active_Head_Count, Offshore_Active_Head_Count FROM pmox.t_pmr "
      + "  where PM_Delegate_ID= ? ";
  
  String p_ldata ="SELECT Project_Description,Customer_Name,Customer_Group_Description,Parent_IBU_Description,Parent_IBG_Description," + 
      " Parent_Cluster_Description,pmr.IBG_Description,pmr.Project_Main_Type,Allocated_HC_Onsite,Total_Allocated_HC,Billed_HC_Onsite,Billed_HC_Offshore," + 
      " Total_Billable,Total_HC,Onsite_Utilization,Offshore_Utilization,Total_Utilization,pnl.REVENUE_ONSITE,pnl.REVENUE_OFFSHORE,pnl.REVENUE_BOUGHT_OUT," + 
      " pnl.REVENUE_TOTAL,pnl.SALARY_COSTS_ONSITE,pnl.SALARY_COSTS_OFFSHORE,pnl.Salary_Total\r\n" + 
      " FROM pmox.t_pmr pmr ,pmox.t_pandl pnl where pmr.projectId = pnl.Project_ID and pmr.PM_Delegate_ID= ? ";
  
  String getCasumData = "SELECT BUSINESS_UNIT, SALES_SEGMENT, SOLD_TO_CUSTOMER_ID, SOLD_TO_CUSTOMER_NAME, L1_SALES, L2_SALES, CONTRACT_double, QMS_double, CONTRACT_P4_CATEGORY, CRM_OPPORTUNITY_ID, OPPORTUNITY_DESCRIPTION, CURRENCY, "+
          " TOTAL_CONTRACT_AMOUNT, TOTAL_CONTRACT_AMOUNT_IN_USD, OWNING_IBU, IBU_DESCRIPTION, IBG_ID, IBG_DESCRIPTION, CLUSTER_ID, CLUSTER_DESCRIPTION, PRPT_CONT_AMT_REPT_PRD, PRPT_CONT_AMT_REPT_PRD_USD, P4_CONTRACT_CREATION_DATE, "+
          " P4_5_CONTRACT_DATE, P5_CONTRACT_DATE, DT_CONT_CLSD_SUPRCD, CONTRACT_STATUS, PO_double, PO_RECEIVED_DATE, START_DATE, END_DATE, P4_NON_TRANSACTABLE_CATEGORY, CONTRACT_TYPE, REVENUE_RECOGNITION_CATEGORY, SALES_CONTACT, "+
          " CUSTOMER_CONTACT, CUSTOMER_GROUP_NAME, PO_REQUESTOR_EMAIL, RUS_COORDINATOR_ID, RUS_COORDINATOR_NAME, QUOTE_COORDINATOR_ID, QUOTE_COORDINATOR_NAME, CUSTOMER_PROJECT_MANAGER, PO_UTILIZED_AMOUNT_, UNBILLED_AMOUNT, PO_BALANCE_INCLUDING_UNBILLED, "+
          " QUOTED_EFFRT_MNDAYS_QTS, ACTUAL_EFFORTS_IN_MANDAYS_RUS, OFFSHORE_PERCENT_QUOTES, OFFSITE_ONSITE_PERCENT_QUOTES, OUC, ACCOUNT_TYPE, CONTRACT_SIGNED_DATE, BILL_TO_CUSTOMER, CUSTOMER_PROGRAMME, PLATFORM, FUNDING_LOB, CRGN, "+
          " FP_BILLING_TYPE, PROJECT_ID, PROJECT_MANAGER, PM_DELEGATE, PROGRAM_MANAGER, IBU_HEAD, PREVIOUS_QUOTE_REFERENCE, `RELEASE`, VERSION_NUM, PRIMARY_CDU, DIV_COMM_OR_ENT, INVOICE_CONSOLIDATED_LEVEL, TAX_TYPE_THAILAND_BU, "+
          " CAT_DETL_THAILAND_BU, CONTRACT_NUMBER, qms_number, po_number FROM pmox.t_casum  where UPPER(PM_DELEGATE) = (Select distinct UPPER(PM_Delegate_Name) from t_pmr where PM_Delegate_ID= ? ) ";
  
  String getPrjMasterData = " SELECT PROJECT_ID, PROJECT_DESC, STATUS, PROJECT_START_DATE, PROJECT_END_DATE, CLOSURE_DATE, SBU, IBG, IBU, IBU_HEAD_ID, IBU_HEAD_NAME, PGM_ID, PGM_NAME, PM_ID, PM_NAME, PROJECT_MAIN_TYPE, PROJECT_TYPE, DELIVERY_OWNERSHIP, "+
  " PROJECT_CRITICALITY, PRICING_MODEL, PROJECT_TECHNOLOGY, PROJECT_DOMAIN, PROJECT_APPLICATION_TYPE, DIGITAL_FLAG, DIGITAL_CATEGORY FROM PMOX.T_PROJECT_MASTER ";

          
  String getPrjAssoctData = " with rws as ( " + 
        "  select ON_OFF,PJM.PROJECT_ID,PJM.PM_ID,PJM.PGM_ID,PJM.STATUS,PJM.IBU_HEAD_ID from PMOX.T_RESOURCE_BASE REBS JOIN PMOX.T_PROJECT_MASTER PJM  ON PJM.PROJECT_ID = REBS.PROJECT_ID " + 
        ") select * from rws  pivot ( count(*) for ON_OFF in ('OFFSHORE' AS OFFSHORE, 'ONSITE' AS ONSITE ) ) " ;
           
  String getPrjRevEbidta = "SELECT ROUND((NVL(REV_TOTAL,0)/1000000),2) AS REV_TOTAL,ROUND(NVL(EBIDTA,0),2) AS EBIDTA FROM ( SELECT ROUND(SUM(REV_TOTAL)*100000,2) AS REV_TOTAL," +
                            " CASE when SUM(REV_TOTAL) <> 0 THEN (SUM(EBIDTA)*100)/SUM(REV_TOTAL) END  AS EBIDTA FROM T_PNL_BASE PNL JOIN PMOX.T_PROJECT_MASTER PJM  "+
                             " ON PJM.PROJECT_ID = pnl.PROJECT_ID ";
  String getPrjRevEbidta1 =  " GROUP BY FY ) A";
  String getPrjRevEbidta2 =  " AND UPPER(\"MONTH\") = TRIM(to_char(add_months( sysdate, -1 ), 'MONTH')) GROUP BY FY ";

  String getPrjResourcData = "SELECT EMPLID, EMP_NAME, GENDER, CATEGORY_CODE, HTR_FLAG, RSB.IBU, RSB.IBG, \"CLUSTER\", EMAIL_ID, BAND, EXPERIENCE, COUNTRY, CITY, ON_OFF, RSB.PROJECT_ID, RSB.PROJECT_DESC, REGULAR_CONTRACT "+
                            " FROM PMOX.T_RESOURCE_BASE RSB JOIN PMOX.T_PROJECT_MASTER PGM ON PGM.PROJECT_ID = RSB.PROJECT_ID WHERE ";



  @Override
  public User getPmrDataFrUser(User user) {
    
    
    final RowMapper<User> mapper = new ProjectRowMapper();
    
    String getPrjCntFrUserFinal = "";
    
    String getPrjAssoct = "";
     
    String getPrjRevEbidtaFinal = "";
    
    User usrRev = new User();
    
    List<User> usrData = new ArrayList<User>();
    
    try {
   
    if(user.getProjectSelected()!=null && !user.getProjectSelected().equals("")) {
      
        getPrjCntFrUserFinal = getPrjCntFrUser+ "WHERE PROJECT_ID IN (?) ";
        user.setTotalProjectCount(jdbcMysql.queryForObject(getPrjCntFrUserFinal,new Object[] { user.getProjectSelected() }, Integer.class));
        if(user.getTotalProjectCount()>0)
        {
            getPrjAssoct = getPrjAssoctData + "WHERE PROJECT_ID IN (?) AND STATUS = 'ACTIVE' ";
            usrData = (List<User>) jdbcMysql.query(
                getPrjAssoct, new Object[] { user.getProjectSelected() }, mapper);
          
            getPrjRevEbidtaFinal = getPrjRevEbidta + "WHERE PJM.PROJECT_ID IN (?) "+ getPrjRevEbidta1;
            usrRev = (User)jdbcMysql.queryForObject(getPrjRevEbidtaFinal,new Object[] { user.getProjectSelected() }, (rs, rowNum) ->
            new User(
                rs.getString("REV_TOTAL"),
                rs.getString("EBIDTA")
          ));
        }
    }
    else {
      
        getPrjCntFrUserFinal = getPrjCntFrUser+ "WHERE "+ user.getRoleName()+"_ID = ? ";
        user.setTotalProjectCount(jdbcMysql.queryForObject(getPrjCntFrUserFinal,new Object[] { user.getUsername() }, Integer.class));
        if(user.getTotalProjectCount()>0)
        {
            getPrjAssoct = getPrjAssoctData + "WHERE "+ user.getRoleName()+"_ID = ? AND STATUS = 'ACTIVE' ";
            usrData = (List<User>) jdbcMysql.query(
                getPrjAssoct, new PreparedStatementSetter() {
                  
                  public void setValues(PreparedStatement preparedStatement) throws SQLException {
                     preparedStatement.setString(1, user.getUsername());
                  }
               }, mapper);
          
            getPrjRevEbidtaFinal = getPrjRevEbidta + "WHERE "+ user.getRoleName()+"_ID = ? "+ getPrjRevEbidta1;
            usrRev = (User)jdbcMysql.queryForObject(getPrjRevEbidtaFinal,new Object[] { user.getUsername() }, (rs, rowNum) ->
            new User(
                rs.getFloat("REV_TOTAL")+"",
                rs.getFloat("EBIDTA")+""
          ));
        }
    }
   
    user.setTotalRevenue(usrRev.getTotalRevenue());
    user.setTotalEbidta(usrRev.getTotalEbidta());

    int offShoreCount = 0;
    int onShoreCount = 0;
    for(User usr :usrData)
    {
      offShoreCount = offShoreCount + usr.getTotalOffShoreCount();
      onShoreCount = onShoreCount + usr.getTotalOnShoreCount();
    }
    
    user.setTotalOffShoreCount(offShoreCount);
    user.setTotalOnShoreCount(onShoreCount);
    }
    catch(Exception ex)
    {
      //throw ex.printStackTrace();;
    }
   
    return user;

}
  
public class ProjectRowMapper implements RowMapper {
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User usr = new User();
        // SELECT tcid, tcname,tcqryone,tcqrytwo,tcdsone,tcdstwo,active FROM
        // USER_TESTCASES
      usr.setTotalOffShoreCount(rs.getInt("OFFSHORE"));
      usr.setTotalOnShoreCount(rs.getInt("ONSITE"));
     // usr.setOnAscCount(rs.getInt(2));
     // usr.setOffAscCount(rs.getInt(3));
        return usr;
    }

}
@Override
public List<PmrData> getPmrSmryDataFrUser(String userId) {
  
  // TODO Auto-generated method stub
    final RowMapper<PmrData> mapper = new PMRRowMapper();
    List<PmrData> result = (List<PmrData>) jdbcMysql.query(
        getPmrDatafrUser, new PreparedStatementSetter() {
           
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
               preparedStatement.setString(1, userId);
            }
         }, mapper);
    System.out.println(result);
  return result;
}

public static class PMRRowMapper implements RowMapper<PmrData> {
      public PmrData mapRow(ResultSet rs, int rowNum) throws SQLException {
          PmrData pmrdata = new PmrData();
          pmrdata.setBuisnessUnit(rs.getString("Business_Unit"));
          pmrdata.setProjectId(rs.getString("projectId"));
          pmrdata.setProjectDesc(rs.getString("projectDesc"));
          //pmrdata.setCustId(rs.getInt("custId"));
          pmrdata.setIbuName(rs.getString("IBU"));
         // pmrdata.setIbuDesc(rs.getString("IBUDescription"));
          pmrdata.setIbuHeadName(rs.getString("IbuHeadName"));
          pmrdata.setStatus(rs.getString("Status"));
          pmrdata.setIbgName(rs.getString("IBG_Description"));
          pmrdata.setProjMainTyp(rs.getString("Project_Main_Type"));
          pmrdata.setProjectType(rs.getString("Project_Type"));
          pmrdata.setProjStartDt(rs.getString("Project_Start_Date"));
          pmrdata.setProjEndDt(rs.getString("Project_End_Date"));
          pmrdata.setProgramMangrNm(rs.getString("Program_Manager_Name"));
          pmrdata.setPmDelegateNm(rs.getString("PM_Delegate_Name"));
          pmrdata.setProjMangerNm(rs.getString("Project_Manager_Name"));
          pmrdata.setActiveHeadCount(rs.getInt("Active_Head_Count"));
          pmrdata.setOnsActHeadCnt(rs.getInt("Onsite_Active_Head_Count"));
          pmrdata.setOffActHeadCnt(rs.getInt("Offshore_Active_Head_Count"));
          
          return pmrdata;
      }
  }


@Override
public List<PrjmasterData> getPrjMasterDataFrUser(String userId) {
    // TODO Auto-generated method stub
     final RowMapper<PrjmasterData> mapper = new PrjmasterRowMapper();
        List<PrjmasterData> result = (List<PrjmasterData>) jdbcMysql.query(
                getPrjMasterData, new PreparedStatementSetter() {
               
                public void setValues(PreparedStatement preparedStatement) throws SQLException {
                   preparedStatement.setString(1, userId);
                }
             }, mapper);
        System.out.println(result);
      return result;
}


public static class PrjmasterRowMapper implements RowMapper<PrjmasterData> {
    public PrjmasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
        PrjmasterData pmrdata = new PrjmasterData();
    
    
        

        
        return pmrdata;
    }
}

@Override
public List<ProfitAndLossData> getP_L(String userId) {
    // TODO Auto-generated method stub
      final RowMapper<ProfitAndLossData> mapper = new P_LRowMapper();
      List<ProfitAndLossData> result = (List<ProfitAndLossData>) jdbcMysql.query(
              p_ldata, new PreparedStatementSetter() {
                   
                  public void setValues(PreparedStatement preparedStatement) throws SQLException {
                     preparedStatement.setString(1, userId);
                  }
               },mapper);
      System.out.println(result);
   
    return result;
}
public static class P_LRowMapper implements RowMapper<ProfitAndLossData> {
    public ProfitAndLossData mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProfitAndLossData p_l = new ProfitAndLossData();
        p_l.setProject_Description(rs.getString("Project_Description"));
        p_l.setCustomer_Name(rs.getString("Customer_Name"));
        p_l.setCustomer_Group_Description(rs.getString("Customer_Group_Description"));
        p_l.setParent_IBU_Description(rs.getString("Parent_IBU_Description"));
        p_l.setParent_IBG_Description(rs.getString("Parent_IBG_Description"));
        p_l.setParent_Cluster_Description(rs.getString("IBG_Description"));
        p_l.setProject_Main_Type(rs.getString("Project_Main_Type"));
        p_l.setAllocated_HC_Onsite(rs.getString("Allocated_HC_Onsite"));
        p_l.setTotal_Allocated_HC(rs.getString("Total_Allocated_HC"));
        p_l.setBilled_HC_Onsite(rs.getString("Billed_HC_Onsite"));
        p_l.setBilled_HC_Offshore(rs.getString("Billed_HC_Offshore"));
        p_l.setTotal_Billable(rs.getString("Total_Billable"));
        p_l.setTotal_HC(rs.getString("Total_HC"));
        p_l.setOnsite_Utilization(rs.getString("Onsite_Utilization"));
        p_l.setOffshore_Utilization(rs.getString("Offshore_Utilization"));
        p_l.setTotal_Utilization(rs.getString("Total_Utilization"));
        p_l.setREVENUE_ONSITE(rs.getString("REVENUE_ONSITE"));
        p_l.setREVENUE_OFFSHORE(rs.getString("REVENUE_OFFSHORE"));
        p_l.setREVENUE_BOUGHT_OUT(rs.getString("REVENUE_BOUGHT_OUT"));
        p_l.setREVENUE_TOTAL(rs.getString("REVENUE_TOTAL"));
        p_l.setSALARY_COSTS__OFFSHORE(rs.getString("SALARY_COSTS_OFFSHORE"));
        p_l.setSALARY_COSTS_ONSITE(rs.getString("SALARY_COSTS_ONSITE"));
        p_l.setSalary_Total(rs.getString("Salary_Total"));
        
        return p_l;
    }
}
@Override
public List<Casum> getCasum(String userId) {
    
     final RowMapper<Casum> mapper = new CasumRowMapper();
     List<Casum> result = (List<Casum>) jdbcMysql.query(
             getCasumData,new PreparedStatementSetter() {
                   
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                       preparedStatement.setString(1, userId);
                    }
                 }, mapper);
     System.out.println(result);
  
   return result;
}
public static class CasumRowMapper implements RowMapper<Casum> {
   public Casum mapRow(ResultSet rs, int rowNum) throws SQLException {
       Casum casum = new Casum();
      
       casum.setBusiness_unit(rs.getString("BUSINESS_UNIT"));
       casum.setSales_segment(rs.getString("SALES_SEGMENT"));
       casum.setSold_to_customer_id(rs.getString("SOLD_TO_CUSTOMER_ID"));
       casum.setSold_to_customer_id(rs.getString("SOLD_TO_CUSTOMER_NAME"));
       casum.setL1_sales(rs.getString("L1_SALES"));
       casum.setL1_sales(rs.getString("L2_SALES"));
       casum.setContract_double(rs.getString("CONTRACT_double"));
       casum.setQms_double(rs.getString("QMS_double"));
       casum.setContract_p4_category(rs.getString("CONTRACT_P4_CATEGORY"));
       casum.setCrm_opportunity_id(rs.getString("CRM_OPPORTUNITY_ID"));
       casum.setOpportunity_description(rs.getString("OPPORTUNITY_DESCRIPTION"));
       casum.setCurrency(rs.getString("CURRENCY"));
       casum.setTotal_contract_amount(rs.getString("TOTAL_CONTRACT_AMOUNT"));
       casum.setTotal_contract_amount_in_usd(rs.getString("TOTAL_CONTRACT_AMOUNT_IN_USD"));
       casum.setOwning_ibu(rs.getString("OWNING_IBU"));
       casum.setIbu_description(rs.getString("IBU_DESCRIPTION"));
       casum.setIbg_id(rs.getString("IBG_ID"));
       casum.setIbg_description(rs.getString("IBG_DESCRIPTION"));
       casum.setCluster_id(rs.getString("CLUSTER_ID"));
       casum.setCluster_description(rs.getString("CLUSTER_DESCRIPTION"));
       casum.setPrpt_cont_amt_rept_prd(rs.getString("PRPT_CONT_AMT_REPT_PRD"));
       casum.setPrpt_cont_amt_rept_prd_usd(rs.getString("PRPT_CONT_AMT_REPT_PRD_USD"));
       casum.setP4_contract_creation_date(rs.getString("P4_CONTRACT_CREATION_DATE"));
       casum.setP4_5_contract_date(rs.getString("P4_5_CONTRACT_DATE"));
       casum.setP5_contract_date(rs.getString("P5_CONTRACT_DATE"));
       casum.setDt_cont_clsd_suprcd(rs.getString("DT_CONT_CLSD_SUPRCD"));
       casum.setContract_status(rs.getString("CONTRACT_STATUS"));
       casum.setPo_double(rs.getString("PO_double"));
       casum.setPo_received_date(rs.getString("PO_RECEIVED_DATE"));
       casum.setStart_date(rs.getString("START_DATE"));
       casum.setEnd_date(rs.getString("END_DATE"));
       casum.setP4_non_transactable_category(rs.getString("P4_NON_TRANSACTABLE_CATEGORY"));
       casum.setContract_type(rs.getString("CONTRACT_TYPE"));
       casum.setRevenue_recognition_category(rs.getString("REVENUE_RECOGNITION_CATEGORY"));
       casum.setSales_contact(rs.getString("SALES_CONTACT"));
       casum.setCustomer_contact(rs.getString("CUSTOMER_CONTACT"));
       casum.setCustomer_group_name(rs.getString("CUSTOMER_GROUP_NAME"));
       casum.setPo_requestor_email(rs.getString("PO_REQUESTOR_EMAIL"));
       casum.setRus_coordinator_id(rs.getString("RUS_COORDINATOR_ID"));
       casum.setRus_coordinator_name(rs.getString("RUS_COORDINATOR_NAME"));
       casum.setQuote_coordinator_id(rs.getString("QUOTE_COORDINATOR_ID"));
       casum.setQuote_coordinator_name(rs.getString("QUOTE_COORDINATOR_NAME"));
       casum.setCustomer_project_manager(rs.getString("CUSTOMER_PROJECT_MANAGER"));
       casum.setPo_utilized_amount_(rs.getString("PO_UTILIZED_AMOUNT_"));
       casum.setUnbilled_amount(rs.getString("UNBILLED_AMOUNT"));
       casum.setPo_balance_including_unbilled(rs.getString("PO_BALANCE_INCLUDING_UNBILLED"));
       casum.setQuoted_effrt_mndays_qts(rs.getString("QUOTED_EFFRT_MNDAYS_QTS"));
       casum.setActual_efforts_in_mandays_rus(rs.getString("ACTUAL_EFFORTS_IN_MANDAYS_RUS"));
       casum.setOffshore_percent_quotes(rs.getString("OFFSHORE_PERCENT_QUOTES"));
       casum.setOffsite_onsite_percent_quotes(rs.getString("OFFSITE_ONSITE_PERCENT_QUOTES"));
       casum.setOuc(rs.getString("OUC"));
       casum.setAccount_type(rs.getString("ACCOUNT_TYPE"));
       casum.setContract_signed_date(rs.getString("CONTRACT_SIGNED_DATE"));
       casum.setBill_to_customer(rs.getString("BILL_TO_CUSTOMER"));
       casum.setCustomer_programme(rs.getString("CUSTOMER_PROGRAMME"));
       casum.setPlatform(rs.getString("PLATFORM"));
       casum.setFunding_lob(rs.getString("FUNDING_LOB"));
       casum.setCrgn(rs.getString("CRGN"));
       casum.setFp_billing_type(rs.getString("FP_BILLING_TYPE"));
       casum.setProject_id(rs.getString("PROJECT_ID"));
       casum.setProject_manager(rs.getString("PROJECT_MANAGER"));
       casum.setPm_delegate(rs.getString("PM_DELEGATE"));
       casum.setProgram_manager(rs.getString("PROGRAM_MANAGER"));
       casum.setIbu_head(rs.getString("IBU_HEAD"));
       casum.setPrevious_quote_reference(rs.getString("PREVIOUS_QUOTE_REFERENCE"));
       casum.setRelease(rs.getString("RELEASE"));
       casum.setVersion_num(rs.getString("VERSION_NUM"));
       casum.setPrimary_cdu(rs.getString("PRIMARY_CDU"));
       casum.setDiv_comm_or_ent(rs.getString("DIV_COMM_OR_ENT"));
       casum.setInvoice_consolidated_level(rs.getString("INVOICE_CONSOLIDATED_LEVEL"));
       casum.setTax_type_thailand_bu(rs.getString("TAX_TYPE_THAILAND_BU"));
       casum.setCat_detl_thailand_bu(rs.getString("CAT_DETL_THAILAND_BU"));
       casum.setContract_number(rs.getString("CONTRACT_NUMBER"));
       casum.setQms_number(rs.getString("qms_number"));
       casum.setPo_number(rs.getString("po_number"));       
       return casum;
   }
}
@Override
public User getProjectMasterDataForPGM(User user) {
 
  String getPrjMasterDataFinal = "";
  List<PrjmasterData> ProjMap =  new ArrayList<PrjmasterData>();
  
  if(user.getProjectSelected()!=null && !user.getProjectSelected().equals("")) {
    getPrjMasterDataFinal = getPrjMasterData+ "WHERE PROJECT_ID IN (?) ORDER BY PM_ID";
    ProjMap = jdbcMysql.query(getPrjMasterDataFinal,new Object[]{user.getProjectSelected()},
        new PrjMasterMapRowMapper());
  }
  else
  {
    getPrjMasterDataFinal = getPrjMasterData+ "WHERE "+ user.getRoleName()+"_ID = ? ORDER BY PM_ID";
    ProjMap = jdbcMysql.query(getPrjMasterDataFinal,new Object[]{user.getUsername()},
        new PrjMasterMapRowMapper());
  }
  
  user.setProjMasterData(ProjMap);
  
  return user;
  
  }

public static class PrjMasterMapRowMapper implements RowMapper<PrjmasterData> {
  public PrjmasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
    PrjmasterData pmData = new PrjmasterData();
    
    pmData.setProjectId(rs.getString("PROJECT_ID"));
    pmData.setProjectDescription(rs.getString("PROJECT_DESC"));
    pmData.setStatus(rs.getString("STATUS"));
    pmData.setProjectStartDate(rs.getString("PROJECT_START_DATE"));
    pmData.setProjectEndDate(rs.getString("PROJECT_END_DATE"));
    pmData.setClosureDate(rs.getString("CLOSURE_DATE"));
    pmData.setSbu(rs.getString("SBU"));
    pmData.setIbg(rs.getString("IBG"));
    pmData.setIbu(rs.getString("IBU"));
    pmData.setIbuHeadId(rs.getString("IBU_HEAD_ID"));
    pmData.setIbuHeadName(rs.getString("IBU_HEAD_NAME"));
    pmData.setPgmId(rs.getString("PGM_ID"));
    pmData.setPgmName(rs.getString("PGM_NAME"));
    pmData.setPmId(rs.getString("PM_ID"));
    pmData.setPmName(rs.getString("PM_NAME"));
    pmData.setProjectMainType(rs.getString("PROJECT_MAIN_TYPE"));
    pmData.setProjectType(rs.getString("PROJECT_TYPE"));
    pmData.setDeliveryOwnership(rs.getString("DELIVERY_OWNERSHIP"));
    pmData.setProjectCriticality(rs.getString("PROJECT_CRITICALITY"));
    pmData.setPricingModel(rs.getString("PRICING_MODEL"));
    pmData.setProjectTechnology(rs.getString("PROJECT_TECHNOLOGY"));
    pmData.setProjectDomain(rs.getString("PROJECT_DOMAIN"));
    pmData.setProjectApplicationType(rs.getString("PROJECT_APPLICATION_TYPE"));
    pmData.setDigitalFlag(rs.getString("DIGITAL_FLAG"));
    pmData.setDigitalCategory(rs.getString("DIGITAL_CATEGORY"));
      
      return pmData;
  }
}

@SuppressWarnings("unchecked")
public User getResourceDataForPGM(User user) {
 
    String getPrjResourcDataFinal = "";
    Map<String, List<ResourceBaseData>> resourceMap =  new HashMap<String, List<ResourceBaseData>>();
    
    if(user.getProjectSelected()!=null && !user.getProjectSelected().equals("")) {
      getPrjResourcDataFinal = getPrjResourcData+ " RSB.PROJECT_ID IN (?) ORDER BY PROJECT_ID";
      resourceMap = (Map<String, List<ResourceBaseData>>) jdbcMysql.query(getPrjResourcDataFinal,new Object[]{user.getProjectSelected()},
          new ResourceMapExtractor());
    }
    else
    {
      getPrjResourcDataFinal = getPrjResourcData+ user.getRoleName()+"_ID = ? ORDER BY PROJECT_ID";
      resourceMap = jdbcMysql.query(getPrjResourcDataFinal,new Object[]{user.getUsername()},
          new ResourceMapExtractor());
    }
    
    user.setResourceMap(resourceMap);
    
    return user;
  
  }
  
public static class ResourceBaseMapRowMapper implements RowMapper<ResourceBaseData> {
  public ResourceBaseData mapRow(ResultSet rs, int rowNum) throws SQLException {
    ResourceBaseData rsData = new ResourceBaseData();
    
    rsData.setProjectId(rs.getString("PROJECT_ID"));
    rsData.setProjectDescription(rs.getString("PROJECT_DESC"));
    rsData.setIbg(rs.getString("IBG"));
    rsData.setIbu(rs.getString("IBU"));
    rsData.setEmpId(rs.getString("EMPLID"));
    rsData.setEmpName(rs.getString("EMP_NAME"));
    rsData.setGender(rs.getString("GENDER"));
    rsData.setCatCode(rs.getString("CATEGORY_CODE"));
    rsData.setHtrFlag(rs.getString("HTR_FLAG"));
    rsData.setCluster(rs.getString("CLUSTER"));
    rsData.setEmail(rs.getString("EMAIL_ID"));
    rsData.setBand(rs.getString("BAND"));
    rsData.setExperience(rs.getString("EXPERIENCE"));
    rsData.setCountry(rs.getString("COUNTRY"));
    rsData.setCity(rs.getString("CITY"));
    rsData.setOnOff(rs.getString("ON_OFF"));
    rsData.setRegContract(rs.getString("REGULAR_CONTRACT"));

      return rsData;
  }
}
 
 
 private static final class ResourceMapExtractor implements ResultSetExtractor<Map<String, List<ResourceBaseData>>> {
   @Override
   public Map<String, List<ResourceBaseData>> extractData(ResultSet rs) throws SQLException {
   Map<String, List<ResourceBaseData>> projectMap = new HashMap<String, List<ResourceBaseData>>();
   List<ResourceBaseData> prjMasterList = null;
   String lastPrjId = "";
   while (rs.next()) {
     
     String prjId = rs.getString("PROJECT_ID");
        
       if(!prjId.equals(lastPrjId))
       {
         if(lastPrjId!=null && !lastPrjId.equals(""))
         {
           projectMap.put(lastPrjId+"",prjMasterList);
         }
         lastPrjId = prjId;
         prjMasterList = new ArrayList<ResourceBaseData>();
       }
   
       ResourceBaseData rsData = new ResourceBaseData();
         
       rsData.setProjectId(prjId);
       rsData.setProjectDescription(rs.getString("PROJECT_DESC"));
       rsData.setIbg(rs.getString("IBG"));
       rsData.setIbu(rs.getString("IBU"));
       rsData.setEmpId(rs.getString("EMPLID"));
       rsData.setEmpName(rs.getString("EMP_NAME"));
       rsData.setGender(rs.getString("GENDER"));
       rsData.setCatCode(rs.getString("CATEGORY_CODE"));
       rsData.setHtrFlag(rs.getString("HTR_FLAG"));
       rsData.setCluster(rs.getString("CLUSTER"));
       rsData.setEmail(rs.getString("EMAIL_ID"));
       rsData.setBand(rs.getString("BAND"));
       rsData.setExperience(rs.getString("EXPERIENCE"));
       rsData.setCountry(rs.getString("COUNTRY"));
       rsData.setCity(rs.getString("CITY"));
       rsData.setOnOff(rs.getString("ON_OFF"));
       rsData.setRegContract(rs.getString("REGULAR_CONTRACT"));
        
       prjMasterList.add(rsData);
   
        }
     projectMap.put(lastPrjId+"", prjMasterList);
           return projectMap;
       }
   }

}
