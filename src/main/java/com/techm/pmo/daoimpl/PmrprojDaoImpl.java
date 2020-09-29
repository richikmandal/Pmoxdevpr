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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.techm.pmo.dao.PmrprojDao;
import com.techm.pmo.dto.PmrData;
import com.techm.pmo.model.Casum;
import com.techm.pmo.model.CasumData;
import com.techm.pmo.model.Pmrdata;
import com.techm.pmo.model.PrjmasterData;
import com.techm.pmo.model.ProfitAndLossData;
import com.techm.pmo.model.ResourceBaseData;
import com.techm.pmo.model.User;

@Service
public class PmrprojDaoImpl implements PmrprojDao {

  @Autowired
  @Qualifier("jdbcMysql")
  private JdbcTemplate jdbcMysql;

  private String      getPrjCntFrUser     = "SELECT COUNT(*) FROM PMOX.V_PRJ_MASTER ";

  private String       getPmrDatafrUser    =
      "SELECT Business_Unit, projectId, projectDesc, custId, IBU, IBUDescription,  IbuHeadName, Status, IBG_Description, Project_Main_Type, PROJECT_N_TYPE, Project_Start_Date, "
          + " Project_End_Date, Program_Manager_Name,  PM_Delegate_Name,  Project_Manager_Name,  Active_Head_Count, Onsite_Active_Head_Count, Offshore_Active_Head_Count FROM pmox.t_pmr "
          + "  where PM_Delegate_ID= ? ";

  String               p_ldata             =
      "SELECT Project_Description,Customer_Name,Customer_Group_Description,Parent_IBU_Description,Parent_IBG_Description,"
          + " Parent_Cluster_Description,pmr.IBG_Description,pmr.Project_Main_Type,Allocated_HC_Onsite,Total_Allocated_HC,Billed_HC_Onsite,Billed_HC_Offshore,"
          + " Total_Billable,Total_HC,Onsite_Utilization,Offshore_Utilization,Total_Utilization,pnl.REVENUE_ONSITE,pnl.REVENUE_OFFSHORE,pnl.REVENUE_BOUGHT_OUT,"
          + " pnl.REVENUE_TOTAL,pnl.SALARY_COSTS_ONSITE,pnl.SALARY_COSTS_OFFSHORE,pnl.Salary_Total\r\n"
          + " FROM pmox.t_pmr pmr ,pmox.t_pandl pnl where pmr.projectId = pnl.Project_ID and pmr.PM_Delegate_ID= ? ";

  String               getCasumData        =
      "SELECT BUSINESS_UNIT, SALES_SEGMENT, SOLD_TO_CUSTOMER_ID, SOLD_TO_CUSTOMER_NAME, L1_SALES, L2_SALES, CONTRACT_double, QMS_double, CONTRACT_P4_CATEGORY, CRM_OPPORTUNITY_ID, OPPORTUNITY_DESCRIPTION, CURRENCY, "
          + " TOTAL_CONTRACT_AMOUNT, TOTAL_CONTRACT_AMOUNT_IN_USD, OWNING_IBU, IBU_DESCRIPTION, IBG_ID, IBG_DESCRIPTION, CLUSTER_ID, CLUSTER_DESCRIPTION, PRPT_CONT_AMT_REPT_PRD, PRPT_CONT_AMT_REPT_PRD_USD, P4_CONTRACT_CREATION_DATE, "
          + " P4_5_CONTRACT_DATE, P5_CONTRACT_DATE, DT_CONT_CLSD_SUPRCD, CONTRACT_STATUS, PO_double, PO_RECEIVED_DATE, START_DATE, END_DATE, P4_NON_TRANSACTABLE_CATEGORY, CONTRACT_TYPE, REVENUE_RECOGNITION_CATEGORY, SALES_CONTACT, "
          + " CUSTOMER_CONTACT, CUSTOMER_GROUP_NAME, PO_REQUESTOR_EMAIL, RUS_COORDINATOR_ID, RUS_COORDINATOR_NAME, QUOTE_COORDINATOR_ID, QUOTE_COORDINATOR_NAME, CUSTOMER_PROJECT_MANAGER, PO_UTILIZED_AMOUNT_, UNBILLED_AMOUNT, PO_BALANCE_INCLUDING_UNBILLED, "
          + " QUOTED_EFFRT_MNDAYS_QTS, ACTUAL_EFFORTS_IN_MANDAYS_RUS, OFFSHORE_PERCENT_QUOTES, OFFSITE_ONSITE_PERCENT_QUOTES, OUC, ACCOUNT_TYPE, CONTRACT_SIGNED_DATE, BILL_TO_CUSTOMER, CUSTOMER_PROGRAMME, PLATFORM, FUNDING_LOB, CRGN, "
          + " FP_BILLING_TYPE, PROJECT_ID, PROJECT_MANAGER, PM_DELEGATE, PROGRAM_MANAGER, IBU_HEAD, PREVIOUS_QUOTE_REFERENCE, `RELEASE`, VERSION_NUM, PRIMARY_CDU, DIV_COMM_OR_ENT, INVOICE_CONSOLIDATED_LEVEL, TAX_TYPE_THAILAND_BU, "
          + " CAT_DETL_THAILAND_BU, CONTRACT_NUMBER, qms_number, po_number FROM pmox.t_casum  where UPPER(PM_DELEGATE) = (Select distinct UPPER(PM_Delegate_Name) from t_pmr where PM_Delegate_ID= ? ) ";

  String  getPrjMasterData    =
      " SELECT PROJECT_ID, PROJECT_DESC, STATUS, PROJECT_START_DATE, PROJECT_END_DATE, CLOSURE_DATE,SBU, D_SBU_HEAD_ID,D_SBU_HEAD_NAME,S_SBU_HEAD_ID,S_SBU_HEAD_NAME,IBG,D_IBG_HEAD_ID,D_IBG_HEAD_NAME, " + 
      " S_IBG_HEAD_ID,S_IBG_HEAD_NAME, IBU, D_IBU_HEAD_ID, D_IBU_HEAD_NAME,S_IBU_HEAD_ID,S_IBU_HEAD_NAME, PGM_ID, PGM_NAME,SALES_MGR_ID,SALES_MGR_NAME, PM_ID, PM_NAME,PROJECT_N_TYPE, DELIVERY_OWNERSHIP, " + 
      " PRICING_MODEL FROM PMOX.V_PRJ_MASTER ";


  String  getPrjAssoctData    = " SELECT ON_OFF,COUNT(*) ON_OFF_CNT FROM PMOX.T_RES_BASE  WHERE ";

  String               getPrjRevEbidta     =
      "SELECT ROUND((NVL(REV_TOTAL,0)/POWER(10,6)),3) AS REV_TOTAL,ROUND(NVL(EBIDTA,0),2) AS EBIDTA FROM ( SELECT ROUND(SUM(REV_TOTAL),2) AS REV_TOTAL,"
          + " CASE when SUM(REV_TOTAL) <> 0 THEN (SUM(EBIDTA)*100)/SUM(REV_TOTAL) END  AS EBIDTA FROM T_PNL_BASE PNL JOIN PMOX.V_PRJ_MASTER PJM  "
          + " ON PJM.PROJECT_ID = pnl.PROJECT_ID ";
  String               getPrjRevEbidta1    = " GROUP BY FY ) A";
  String               getPrjRevEbidta2    =
      " AND UPPER(\"MONTH\") = TRIM(to_char(add_months( sysdate, -1 ), 'MONTH')) GROUP BY FY ";

  String  getPrjResourcData = "SELECT EMP_ID, EMP_NAME, GENDER, CATEGORY_CODE, HTR_FLAG, RSB.IBU, EMAIL_ID, BAND, EXPERIENCE, COUNTRY, CITY, ON_OFF, RSB.PROJECT_ID, RSB.PROJECT_DESC  "
          + " FROM PMOX.T_RES_BASE RSB WHERE ";

  String               getPrjPnLData       =
      "SELECT PNL.PROJECT_ID, PNL.PROJECT_DESC, \"MONTH\", QTR, FY, ON_HC, OF_HC, TOT_HC, E, H, M, T, P1, P2, U1, U2, U3, U4, UJ, REV_TOTAL, SAL_ON, SAL_OFF, SAL_TOTAL, VISA_WP, "
          + " TRAVEL_FOREIGN, TRAVEL_INLAND, SUBCON_EXPNS, SUBCON_EXPNS_ON, SUBCON_EXPNS_OFF, TOTAL_SUBCON_EXPNS, PROJECT_EXPNS, RING_FENCING, IBU_BUFFER_COST, IBG_BUFFER_COST, "
          + " SBU_BUFFER_COST, ALLOC_DIRECT_COST, OTHER_EXPNS, TOTAL_DIRECT_COST, CONTR, SGNA, EBIDTA, PGM_ID, PM_ID FROM PMOX.T_PNL_BASE PNL JOIN PMOX.V_PRJ_MASTER PGM ON PGM.PROJECT_ID = PNL.PROJECT_ID WHERE ";
  String               getPmSeriesData     = "  with rws as ( "
      + "  select PJM.PROJECT_ID,PJM.PM_ID,PJM.PGM_ID,PJM.STATUS,REV_TOTAL,PM_NAME from PMOX.T_PNL_BASE PNL JOIN PMOX.V_PRJ_MASTER PJM  ON PJM.PROJECT_ID = PNL.PROJECT_ID "
      + " ) select PGM_ID,PM_NAME,PM_ID,ROUND(SUM(REV_TOTAL),2) AS REV_TOTAL from rws ";
  String               getPmSeriesData1    = " GROUP BY PM_ID,PM_NAME,PGM_ID";

  String               getRevEbidtaConsQry =
      " select ROUND(((SUM(JANUARY_EBIDTA)*100/SUM(JANUARY_REV_TOTAL))/POWER(10,6)),3)  AS JANUARY_EBIDTA,SUM(FEBRUARY_EBIDTA)*100/SUM(FEBRUARY_REV_TOTAL) AS FEBRUARY_EBIDTA,  " + 
      "  SUM(MARCH_EBIDTA)*100/SUM(MARCH_REV_TOTAL) AS MARCH_EBIDTA,SUM(APRIL_EBIDTA)*100/SUM(APRIL_REV_TOTAL) AS APRIL_EBIDTA, " + 
      "  SUM(MAY_EBIDTA)*100/SUM(MAY_REV_TOTAL) AS MAY_EBIDTA,SUM(JUNE_EBIDTA)*100/SUM(JUNE_REV_TOTAL) AS JUNE_EBIDTA," + 
      "  SUM(JULY_EBIDTA)*100/SUM(JULY_REV_TOTAL) AS JULY_EBIDTA,SUM(AUGUST_EBIDTA)*100/SUM(AUGUST_REV_TOTAL) AS AUGUST_EBIDTA," + 
      "  SUM(SEPTEMBER_EBIDTA)*100/SUM(SEPTEMBER_REV_TOTAL) AS SEPTEMBER_EBIDTA,SUM(OCTOBER_EBIDTA)*100/SUM(OCTOBER_REV_TOTAL) AS OCTOBER_EBIDTA," + 
      "  SUM(NOVEMBER_EBIDTA)*100/SUM(NOVEMBER_REV_TOTAL) AS NOVEMBER_EBIDTA,SUM(DECEMBER_EBIDTA)*100/SUM(DECEMBER_REV_TOTAL) AS DECEMBER_EBIDTA, " + 
      "  ROUND((SUM(JANUARY_REV_TOTAL)/POWER(10,6)),3) AS JANUARY_REV_TOTAL,ROUND((SUM(FEBRUARY_REV_TOTAL)/POWER(10,6)),3)  AS FEBRUARY_REV_TOTAL,ROUND((SUM(MARCH_REV_TOTAL)/POWER(10,6)),3) AS MARCH_REV_TOTAL, " + 
      "  ROUND((SUM(APRIL_REV_TOTAL)/POWER(10,6)),3) AS APRIL_REV_TOTAL,ROUND((SUM(MAY_REV_TOTAL)/POWER(10,6)),3) AS MAY_REV_TOTAL,ROUND((SUM(JUNE_REV_TOTAL)/POWER(10,6)),3) AS JUNE_REV_TOTAL, " + 
      "  ROUND((SUM(JULY_REV_TOTAL)/POWER(10,6)),3) AS JULY_REV_TOTAL,ROUND((SUM(AUGUST_REV_TOTAL)/POWER(10,6)),3) AS AUGUST_REV_TOTAL, ROUND((SUM(SEPTEMBER_REV_TOTAL)/POWER(10,6)),3) AS SEPTEMBER_REV_TOTAL, " + 
      "  ROUND((SUM(OCTOBER_REV_TOTAL)/POWER(10,6)),3) AS OCTOBER_REV_TOTAL,ROUND((SUM(NOVEMBER_REV_TOTAL)/POWER(10,6)),3) AS NOVEMBER_REV_TOTAL,ROUND((SUM(DECEMBER_REV_TOTAL)/POWER(10,6)),3) AS DECEMBER_REV_TOTAL " + 
      "  from (select * from ( select \"MONTH\",PJM.PROJECT_ID,PJM.PM_ID,PJM.PM_NAME,PJM.PGM_ID,PJM.PGM_NAME,PJM.STATUS,PJM.D_IBU_HEAD_ID,PJM.D_IBU_HEAD_NAME,REV_TOTAL,EBIDTA " + 
      "  from PMOX.T_PNL_BASE PNL   JOIN PMOX.V_PRJ_MASTER PJM  ON PJM.PROJECT_ID = PNL.PROJECT_ID   ) pivot ( sum(NVL(REV_TOTAL,0)) as REV_TOTAL , " + 
      "  SUM(NVL(EBIDTA,0)) AS EBIDTA for \"MONTH\" in ('JANUARY' AS JANUARY, 'FEBRUARY' AS FEBRUARY ,'MARCH' AS MARCH,'APRIL' AS APRIL,   'MAY' AS MAY," + 
      "  'JUNE' AS JUNE,'JULY' AS JULY,'AUGUST' AS AUGUST,'SEPTEMBER' AS SEPTEMBER,'OCTOBER' AS OCTOBER,   'NOVEMBER' AS NOVEMBER,'DECEMBER' AS DECEMBER))) WHERE ";
  String getPnLData1 = "SELECT HEADER,SUM(CFY_APR) CFY_APR ,SUM(CFY_MAY) CFY_MAY,SUM(CFY_JUN) CFY_JUN,SUM(CFY_JUL) CFY_JUL,SUM(CFY_AUG) CFY_AUG,SUM(CFY_SEP) CFY_SEP,  \r\n" + 
      "SUM(CFY_OCT) CFY_OCT,SUM(CFY_NOV) CFY_NOV,SUM(CFY_DEC) CFY_DEC, SUM(CFY_JAN) CFY_JAN,SUM(CFY_FEB) CFY_FEB,SUM(CFY_MAR) CFY_MAR FROM V_PNL_CALC  WHERE ";
  String getPnLData2 = " GROUP BY HEADER ORDER BY HEADER";
  
  String getPnLData3 = "SELECT \"MONTH\",MONTH_NO, SUM(\"ON #\") \"ON #\",SUM(\"OF #\") \"OF #\", SUM(\"HC #\") \"HC #\", ROUND((SUM(REVENUE)/POWER(10,6)),3) REVENUE, ROUND((SUM(\"DIRECT COST\")/POWER(10,6)),3) \"DIRECT COST\", "+
       " ROUND((SUM(\"CNB COST\")/POWER(10,6)),3) \"CNB COST\", ROUND((SUM(\"TNL COST\")/POWER(10,6)),3) \"TNL COST\", ROUND((SUM(\"BUFFER COST\")/POWER(10,6)),3) \"BUFFER COST\",ROUND((SUM(\"SGNA COST\")/POWER(10,6)),3) \"SGNA COST\", ROUND((SUM(EBIDTA)/POWER(10,6)),3) EBIDTA ,ROUND((SUM(EBIDTA)*100/SUM(REVENUE)),2) AS \"EBIDTA%\" "+
       " FROM PMOX.V_PNL_CALC_NEW WHERE ";
  String getPnLData4= " GROUP BY MONTH ,MONTH_NO ORDER BY MONTH_NO ";
  
  String getPoReceivedDtl = "SELECT * FROM (SELECT PJM.PGM_ID,PJM.PM_ID,PJM.D_IBU_HEAD_ID,CAS.PO_NUM,CAS.CUST_ID,CAS.CUST_NAME,CAS.OPTY_ID,CAS.OPTY_DESC, " + 
      " CAS.CNTRCT_NUM,CAS.CNTRCT_AMT,CAS.CRNCY,CAS.CNTRCT_AMT_USD, CAS.CNTRCT_STATUS,CAS.CNTRCT_START_DATE,CAS.CNTRCT_END_DATE, " + 
      " CAS.PROJECT_ID, PJM.PROJECT_DESC ,PJM.D_IBU_HEAD_NAME,PJM.PGM_NAME,PJM.PM_NAME,PJM.PROJECT_N_TYPE  FROM T_CASUM CAS JOIN V_PRJ_MASTER PJM " + 
     " ON PJM.PROJECT_ID = CAS.PROJECT_ID ) A WHERE ";
  String getPoReceivedDtl1 =  " AND PO_NUM IS NOT NULL AND CNTRCT_STATUS != 'CLOSED' ORDER BY PO_NUM ";
  
  String getRevProjectionDtl = " SELECT REV_CAT AS CATEGORY,sum(\"CFY_Q1\") CFYQ1,sum(\"CFY_Q2\") CFYQ2,sum(\"CFY_Q3\") CFYQ3,sum(\"CFY_Q4\") CFYQ4 FROM V_REV_FCST WHERE ";
  String getRevProjectionDtl1 = " GROUP BY REV_CAT ORDER BY REV_CAT";
  
  String getRevProjTarget = " SELECT 'a-Target' AS CATEGORY , sum(CFY_Q1) CFYQ1,sum(CFY_Q2) CFYQ2,sum(CFY_Q3) CFYQ3,sum(CFY_Q4) CFYQ4 FROM T_REV_TARGET WHERE ";
    
  @Override
  public User getPmrDataFrUser(User user) {


    final RowMapper<User> mapper = new ProjectRowMapper();
    String getPrjAssoct = "";

    User usrRev = new User();

    List<User> usrData = new ArrayList<User>();

    try {


          getPrjAssoct = getPrjAssoctData + user.getRoleName() + "_ID = ? ";
          
                  
            getPrjAssoct = getFilterProject(getPrjAssoct, user,false);
          
            
            getPrjAssoct = getFilterPM(getPrjAssoct, user,false);
         
            getPrjAssoct =  getFilterPGM(getPrjAssoct, user,false);
       
            getPrjAssoct =  getFilterSPGM(getPrjAssoct, user,false);
         
            getPrjAssoct =  getFilterIBU(getPrjAssoct, user,false);
        
            
            getPrjAssoct =  getFilterIBG(getPrjAssoct, user,false);
         
            
            getPrjAssoct =  getFilterSBU(getPrjAssoct, user,false);
          
          
                    
          usrData = (List<User>) jdbcMysql.query(getPrjAssoct + " GROUP BY ON_OFF ", new PreparedStatementSetter() {

            public void setValues(PreparedStatement preparedStatement) throws SQLException {
              preparedStatement.setString(1, user.getUsername());
            }
          }, mapper);

          user.setTotalOnShoreCount(0);
          user.setTotalOffShoreCount(0);
          
      for (User usr : usrData) {
        if(usr.getOnOff().equals("ONSITE")) {
          user.setTotalOnShoreCount(usr.getOnOffCnt());
        }else {
          
          user.setTotalOffShoreCount(usr.getOnOffCnt());
        }
      }
    }
    catch (Exception ex) {
      // throw ex.printStackTrace();;
    }

    return user;

  }

  public class ProjectRowMapper implements RowMapper {
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User usr = new User();
     
      usr.setOnOff(rs.getString("ON_OFF"));
      usr.setOnOffCnt(rs.getInt("ON_OFF_CNT"));
      
      return usr;
    }

  }

  @Override
  public List<PmrData> getPmrSmryDataFrUser(String userId) {

    // TODO Auto-generated method stub
    final RowMapper<PmrData> mapper = new PMRRowMapper();
    List<PmrData> result =
        (List<PmrData>) jdbcMysql.query(getPmrDatafrUser, new PreparedStatementSetter() {

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
      pmrdata.setIbuName(rs.getString("IBU"));
      pmrdata.setIbuHeadName(rs.getString("IbuHeadName"));
      pmrdata.setStatus(rs.getString("Status"));
      pmrdata.setIbgName(rs.getString("IBG_Description"));
      pmrdata.setProjMainTyp(rs.getString("Project_Main_Type"));
      pmrdata.setProjectType(rs.getString("PROJECT_N_TYPE"));
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
    List<PrjmasterData> result =
        (List<PrjmasterData>) jdbcMysql.query(getPrjMasterData, new PreparedStatementSetter() {

          public void setValues(PreparedStatement preparedStatement) throws SQLException {
            preparedStatement.setString(1, userId);
          }
        }, mapper);
    //System.out.println(result);
    return result;
  }


  public static class PrjmasterRowMapper implements RowMapper<PrjmasterData> {
    public PrjmasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
      PrjmasterData pmrdata = new PrjmasterData();

      return pmrdata;
    }
  }


  @Override
  public User getProjectMasterDataForPGM(User user) {

    String getPrjMasterDataFinal = "";
    List<PrjmasterData> ProjMap = new ArrayList<PrjmasterData>();

    /*
     * if (user.getProjectSelected() != null && !user.getProjectSelected().equals("")) {
     * getPrjMasterDataFinal = getPrjMasterData + "WHERE PROJECT_ID IN (?) ORDER BY PM_ID"; ProjMap
     * = jdbcMysql.query(getPrjMasterDataFinal, new Object[] {user.getProjectSelected()}, new
     * PrjMasterMapRowMapper()); } else { getPrjMasterDataFinal = getPrjMasterData + "WHERE " +
     * user.getRoleName() + "_ID = ? ORDER BY PM_ID"; ProjMap =
     * jdbcMysql.query(getPrjMasterDataFinal, new Object[] {user.getUsername()}, new
     * PrjMasterMapRowMapper()); }
     */
    
    getPrjMasterDataFinal =  getPrjMasterData + "WHERE " + user.getRoleName() + "_ID = ? ";
    
   /* if(user.getFilterRoleSel()!=null) {
      if(user.getFilterRoleSel().equals("PRJ") ) {
        
        getPrjMasterDataFinal = getFilterProject(getPrjMasterDataFinal, user,false);
      }
      if(user.getFilterRoleSel().equals("PM") ) {
        
        getPrjMasterDataFinal = getFilterPM(getPrjMasterDataFinal, user,false);
      }
      if(user.getFilterRoleSel().equals("PGM") ) {
        
        getPrjMasterDataFinal =  getFilterPGM(getPrjMasterDataFinal, user,false);
      }
      if(user.getFilterRoleSel().equals("SALES") ) {
        
        getPrjMasterDataFinal =  getFilterSPGM(getPrjMasterDataFinal, user,false);
      }
      if(user.getFilterRoleSel().equals("IBU") ) {
        
        getPrjMasterDataFinal =  getFilterIBU(getPrjMasterDataFinal, user,false);
      }
      if(user.getFilterRoleSel().equals("IBG") ) {
        
        getPrjMasterDataFinal =  getFilterIBG(getPrjMasterDataFinal, user,false);
      }
      if(user.getFilterRoleSel().equals("SBU")) {
        
        getPrjMasterDataFinal =  getFilterSBU(getPrjMasterDataFinal, user,false);
      }
     
    } */
    
   
      getPrjMasterDataFinal = getFilterProject(getPrjMasterDataFinal, user,false);
   
      getPrjMasterDataFinal = getFilterPM(getPrjMasterDataFinal, user,false);
   
      getPrjMasterDataFinal =  getFilterPGM(getPrjMasterDataFinal, user,false);
   
      
      getPrjMasterDataFinal =  getFilterSPGM(getPrjMasterDataFinal, user,false);
   
      getPrjMasterDataFinal =  getFilterIBU(getPrjMasterDataFinal, user,false);
    
      
      getPrjMasterDataFinal =  getFilterIBG(getPrjMasterDataFinal, user,false);
   
      
      getPrjMasterDataFinal =  getFilterSBU(getPrjMasterDataFinal, user,false);
   
    
    ProjMap = jdbcMysql.query(getPrjMasterDataFinal, new Object[] {user.getUsername()},
        new PrjMasterMapRowMapper());
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
      pmData.setdSbuHeadId(rs.getString("D_SBU_HEAD_ID"));
      pmData.setdSbuHeadName(rs.getString("D_SBU_HEAD_NAME"));
      pmData.setsSbuHeadId(rs.getString("S_SBU_HEAD_ID"));
      pmData.setsSbuHeadName(rs.getString("S_SBU_HEAD_NAME"));
      pmData.setIbg(rs.getString("IBG"));
      pmData.setdIbgHeadId(rs.getString("D_IBG_HEAD_ID"));
      pmData.setdIbgHeadName(rs.getString("D_IBG_HEAD_NAME"));
      pmData.setsIbgHeadId(rs.getString("S_IBG_HEAD_ID"));
      pmData.setsIbgHeadName(rs.getString("S_IBG_HEAD_NAME"));
      pmData.setIbu(rs.getString("IBU"));
      pmData.setIbuHeadId(rs.getString("D_IBU_HEAD_ID"));
      pmData.setIbuHeadName(rs.getString("D_IBU_HEAD_NAME"));
      pmData.setsIbuHeadId(rs.getString("S_IBU_HEAD_ID"));
      pmData.setsIbuHeadName(rs.getString("S_IBU_HEAD_NAME"));
      pmData.setPgmId(rs.getString("PGM_ID"));
      pmData.setPgmName(rs.getString("PGM_NAME"));
      pmData.setsPgmId(rs.getString("SALES_MGR_ID"));
      pmData.setsPgmName(rs.getString("SALES_MGR_NAME"));
      pmData.setPmId(rs.getString("PM_ID"));
      pmData.setPmName(rs.getString("PM_NAME"));
      pmData.setProjectType(rs.getString("PROJECT_N_TYPE"));
      pmData.setDeliveryOwnership(rs.getString("DELIVERY_OWNERSHIP"));
      pmData.setPricingModel(rs.getString("PRICING_MODEL"));
     
      return pmData;
    }
  }

  @SuppressWarnings("unchecked")
  public User getResourceDataForPGM(User user) {

    String getPrjResourcDataFinal = "";
    Map<String, List<ResourceBaseData>> resourceMap = new HashMap<String, List<ResourceBaseData>>();

  
      getPrjResourcDataFinal = getPrjResourcData + user.getRoleName() + "_ID = ? ";
      
      /*if(user.getFilterRoleSel()!=null) {
        if(user.getFilterRoleSel().equals("PRJ") ) {
          
          getPrjResourcDataFinal = getFilterProject(getPrjResourcDataFinal, user,false);
        }
        if(user.getFilterRoleSel().equals("PM") ) {
          
          getPrjResourcDataFinal = getFilterPM(getPrjResourcDataFinal, user,false);
        }
        if(user.getFilterRoleSel().equals("PGM") ) {
          
          getPrjResourcDataFinal =  getFilterPGM(getPrjResourcDataFinal, user,false);
        }
        if(user.getFilterRoleSel().equals("SALES") ) {
          
          getPrjResourcDataFinal =  getFilterSPGM(getPrjResourcDataFinal, user,false);
        }
        if(user.getFilterRoleSel().equals("IBU") ) {
          
          getPrjResourcDataFinal =  getFilterIBU(getPrjResourcDataFinal, user,false);
        }
        if(user.getFilterRoleSel().equals("IBG") ) {
          
          getPrjResourcDataFinal =  getFilterIBG(getPrjResourcDataFinal, user,false);
        }
        if(user.getFilterRoleSel().equals("SBU")) {
          
          getPrjResourcDataFinal =  getFilterSBU(getPrjResourcDataFinal, user,false);
        }
       
      }    */
      
      getPrjResourcDataFinal = getFilterProject(getPrjResourcDataFinal, user ,false);
      
      
      getPrjResourcDataFinal = getFilterPM(getPrjResourcDataFinal, user ,false);
 
      getPrjResourcDataFinal =  getFilterPGM(getPrjResourcDataFinal, user ,false);
 
      getPrjResourcDataFinal =  getFilterSPGM(getPrjResourcDataFinal, user ,false);
   
      getPrjResourcDataFinal =  getFilterIBU(getPrjResourcDataFinal, user ,false);
  
      
      getPrjResourcDataFinal =  getFilterIBG(getPrjResourcDataFinal, user ,false);
 
      
      getPrjResourcDataFinal =  getFilterSBU(getPrjResourcDataFinal, user ,false);
      
      resourceMap = jdbcMysql.query(getPrjResourcDataFinal + " ORDER BY PROJECT_ID ", new Object[] {user.getUsername()},
          new ResourceMapExtractor());
   
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
      rsData.setEmpId(rs.getString("EMP_ID"));
      rsData.setEmpName(rs.getString("EMP_NAME"));
      //rsData.setGender(rs.getString("GENDER"));
      rsData.setCatCode(rs.getString("CATEGORY_CODE"));
      rsData.setHtrFlag(rs.getString("HTR_FLAG"));
     // rsData.setCluster(rs.getString("CLUSTER"));
     // rsData.setEmail(rs.getString("EMAIL_ID"));
     // rsData.setBand(rs.getString("BAND"));
      //rsData.setExperience(rs.getString("EXPERIENCE"));
      //rsData.setCountry(rs.getString("COUNTRY"));
     // rsData.setCity(rs.getString("CITY"));
      rsData.setOnOff(rs.getString("ON_OFF"));
      //rsData.setRegContract(rs.getString("REGULAR_CONTRACT"));

      return rsData;
    }
  }


  private static final class ResourceMapExtractor
      implements ResultSetExtractor<Map<String, List<ResourceBaseData>>> {
    @Override
    public Map<String, List<ResourceBaseData>> extractData(ResultSet rs) throws SQLException {
      Map<String, List<ResourceBaseData>> projectMap =
          new HashMap<String, List<ResourceBaseData>>();
      List<ResourceBaseData> prjMasterList = null;
      String lastPrjId = "";
      while (rs.next()) {

        String prjId = rs.getString("PROJECT_ID");

        if (!prjId.equals(lastPrjId)) {
          if (lastPrjId != null && !lastPrjId.equals("")) {
            projectMap.put(lastPrjId + "", prjMasterList);
          }
          lastPrjId = prjId;
          prjMasterList = new ArrayList<ResourceBaseData>();
        }

        ResourceBaseData rsData = new ResourceBaseData();

        rsData.setProjectId(prjId);
        rsData.setProjectDescription(rs.getString("PROJECT_DESC"));
        //rsData.setIbg(rs.getString("D_IBG"));
        rsData.setIbu(rs.getString("IBU"));
        rsData.setEmpId(rs.getString("EMP_ID"));
        rsData.setEmpName(rs.getString("EMP_NAME"));
        rsData.setGender(rs.getString("GENDER"));
        rsData.setCatCode(rs.getString("CATEGORY_CODE"));
        rsData.setHtrFlag(rs.getString("HTR_FLAG"));
        //rsData.setCluster(rs.getString("CLUSTER"));
        rsData.setEmail(rs.getString("EMAIL_ID"));
        rsData.setBand(rs.getString("BAND"));
        rsData.setExperience(rs.getString("EXPERIENCE"));
        rsData.setCountry(rs.getString("COUNTRY"));
        rsData.setCity(rs.getString("CITY"));
        rsData.setOnOff(rs.getString("ON_OFF"));
        //rsData.setRegContract(rs.getString("REGULAR_CONTRACT"));

        prjMasterList.add(rsData);

      }
      projectMap.put(lastPrjId + "", prjMasterList);
      return projectMap;
    }
  }

 
  @Override
  public List<Pmrdata> getPmSeriesData(User user) {
    // TODO Auto-generated method stub
    String getPmSeriesQueryFinal = "";
    List<Pmrdata> lstPmSeriesData;


    if (user.getProjectSelected() != null && !user.getProjectSelected().equals("")) {

      getPmSeriesQueryFinal = getRevEbidtaConsQry + " PROJECT_ID IN (?) ORDER BY PROJECT_ID";
      lstPmSeriesData = jdbcMysql.query(getPmSeriesQueryFinal,
          new Object[] {user.getProjectSelected()}, new PmSeriesMapRowMapper());

    }
    else {
      getPmSeriesQueryFinal = getRevEbidtaConsQry + user.getRoleName() + "_ID = ? ";
      lstPmSeriesData = jdbcMysql.query(getPmSeriesQueryFinal, new Object[] {user.getUsername()},
          new PmSeriesMapRowMapper());
    }
    return lstPmSeriesData;
  }

  public static class PmSeriesMapRowMapper implements RowMapper<Pmrdata> {
    public Pmrdata mapRow(ResultSet rs, int rowNum) throws SQLException {
      Pmrdata pmData = new Pmrdata();

      pmData.setJanRev(rs.getString("JANUARY_REV_TOTAL"));
      pmData.setFebRev(rs.getString("FEBRUARY_REV_TOTAL"));
      pmData.setMarRev(rs.getString("MARCH_REV_TOTAL"));
      pmData.setAprRev(rs.getString("APRIL_REV_TOTAL"));
      pmData.setMayRev(rs.getString("MAY_REV_TOTAL"));
      pmData.setJunRev(rs.getString("JUNE_REV_TOTAL"));
      pmData.setJulRev(rs.getString("JULY_REV_TOTAL"));
      pmData.setAugRev(rs.getString("AUGUST_REV_TOTAL"));
      pmData.setSepRev(rs.getString("SEPTEMBER_REV_TOTAL"));
      pmData.setOctRev(rs.getString("OCTOBER_REV_TOTAL"));
      pmData.setNovRev(rs.getString("NOVEMBER_REV_TOTAL"));
      pmData.setDecRev(rs.getString("DECEMBER_REV_TOTAL"));

      pmData.setJanEbi(rs.getString("JANUARY_EBIDTA"));
      pmData.setFebEbi(rs.getString("FEBRUARY_EBIDTA"));
      pmData.setMarEbi(rs.getString("MARCH_EBIDTA"));
      pmData.setAprEbi(rs.getString("APRIL_EBIDTA"));
      pmData.setMayEbi(rs.getString("MAY_EBIDTA"));
      pmData.setJunEbi(rs.getString("JUNE_EBIDTA"));
      pmData.setJulEbi(rs.getString("JULY_EBIDTA"));
      pmData.setAugEbi(rs.getString("AUGUST_EBIDTA"));
      pmData.setSepEbi(rs.getString("SEPTEMBER_EBIDTA"));
      pmData.setOctEbi(rs.getString("OCTOBER_EBIDTA"));
      pmData.setNovEbi(rs.getString("NOVEMBER_EBIDTA"));
      pmData.setDecEbi(rs.getString("DECEMBER_EBIDTA"));

      return pmData;
    }
  }


  @Override
  public List<ProfitAndLossData> getP_L(String user) {
   
    return null;
  }
  
  public String getFilterProject(String queryFinal,User user,Boolean grpByRqrd)
  {
    
    if(user.getPrjNme() !=null && !user.getPrjNme().equals("---All Projects---")) {
      
      queryFinal = queryFinal +  " AND PROJECT_DESC = '"+user.getPrjNme()+"'";
    }
    /*else {
      
      queryFinal = getFilterPM(queryFinal,user,grpByRqrd);
    }*/
    return queryFinal;
  }
  
  public String getFilterPM(String queryFinal,User user,Boolean grpByRqrd)
  {
    
    if( user.getPmName() !=null && !user.getPmName().equals("---All PMs---")) {
      
      queryFinal = queryFinal +  " AND PM_NAME= '"+user.getPmName()+"'";
    }
    /* else {
      queryFinal = getFilterPGM(queryFinal,user,grpByRqrd);
    }*/
    return queryFinal;
  }
  
  public String getFilterPGM(String queryFinal,User user,Boolean grpByRqrd)
  {
    
    if( user.getPgmName() !=null && !user.getPgmName().equals("---All PGMs---")) {
      
      queryFinal = queryFinal +  " AND PGM_NAME= '"+user.getPgmName()+"'";
     
    }
  /*  else {
      queryFinal = getFilterIBU(queryFinal,user,grpByRqrd);
    }*/
    return queryFinal;
  }
  
  public String getFilterSPGM(String queryFinal,User user,Boolean grpByRqrd)
  {
    
    if( user.getSpgmName() !=null && !user.getSpgmName().equals("---All Sales Mangers---")) {
      
      queryFinal = queryFinal +  " AND SALES_MGR_NAME= '"+user.getSpgmName()+"'";
    }
   /* else {
      queryFinal = getFilterIBU(queryFinal,user,grpByRqrd);
    }*/
    return queryFinal;
  }
  
  public String getFilterIBU(String queryFinal,User user,Boolean grpByRqrd)
  {
    
    if( user.getIbuName()!=null &&  !user.getIbuName().equals("---All IBU---")) {
      
      queryFinal = queryFinal +  " AND IBU= '"+user.getIbuName()+"'";
    }
  /*  else {
      queryFinal = getFilterIBG(queryFinal,user,grpByRqrd);
    }*/
    return queryFinal;
  }
  
  public String getFilterIBG(String queryFinal,User user,Boolean grpByRqrd)
  {
    
    if( user.getIbgName()!=null &&  !user.getIbgName().equals("---All IBG---")) {
      
      queryFinal = queryFinal +  " AND IBG= '"+user.getIbgName()+"'";
      
    }
   /* else {
      queryFinal = getFilterSBU(queryFinal,user,grpByRqrd);
    } */
    return queryFinal;
  }
  public String getFilterSBU(String queryFinal,User user,Boolean grpByRqrd)
  {
    
    if(user.getSbuName()!=null && !user.getSbuName().equals("---All SBU---")) {
      
      queryFinal = queryFinal +  " AND SBU= '"+user.getSbuName()+"'";
    }
   
    return queryFinal;
  }
  
  

  @Override
  public List<ProfitAndLossData> getPnLSummary(User user) {

    String getPmSeriesQueryFinal = "";
    List<ProfitAndLossData> lstPnLSeriesData;
    
      
    getPmSeriesQueryFinal = getPnLData3 + user.getRoleName() + "_ID = ? " ;
          
    
        getPmSeriesQueryFinal = getFilterProject(getPmSeriesQueryFinal, user ,false);
     
        
        getPmSeriesQueryFinal = getFilterPM(getPmSeriesQueryFinal, user ,false);
   
        getPmSeriesQueryFinal =  getFilterPGM(getPmSeriesQueryFinal, user ,false);
   
        getPmSeriesQueryFinal =  getFilterSPGM(getPmSeriesQueryFinal, user ,false);
     
        getPmSeriesQueryFinal =  getFilterIBU(getPmSeriesQueryFinal, user ,false);
    
        
        getPmSeriesQueryFinal =  getFilterIBG(getPmSeriesQueryFinal, user ,false);
   
        
        getPmSeriesQueryFinal =  getFilterSBU(getPmSeriesQueryFinal, user ,false);
     
    getPmSeriesQueryFinal = getPmSeriesQueryFinal + getPnLData4 ;
      
    lstPnLSeriesData = jdbcMysql.query(getPmSeriesQueryFinal, new Object[] {user.getUsername()},
      new PnLSeriesMapRowMapper());
    
    
    return lstPnLSeriesData;
  }

  public static class PnLSeriesMapRowMapper implements RowMapper<ProfitAndLossData> {
    public ProfitAndLossData mapRow(ResultSet rs, int rowNum) throws SQLException {
      ProfitAndLossData pnlData = new ProfitAndLossData();
   
      pnlData.setMonth(rs.getString("MONTH"));
      pnlData.setTotalHc(rs.getInt("HC #"));
      pnlData.setOnHc(rs.getInt("ON #"));
      pnlData.setOffHc(rs.getInt("OF #"));
      pnlData.setRevCost(rs.getFloat("REVENUE"));
      pnlData.setDirectCost(rs.getFloat("DIRECT COST"));
      pnlData.setCnbCost(rs.getFloat("CNB COST"));
      pnlData.setTnlCost(rs.getFloat("TNL COST"));
      pnlData.setBufferCost(rs.getFloat("BUFFER COST"));
      pnlData.setSgnaCost(rs.getFloat("SGNA COST"));
      pnlData.setEbidta(rs.getFloat("EBIDTA"));
      pnlData.setEbidtaper(rs.getFloat("EBIDTA%"));
     
      return pnlData;
    }
  }
  
  public static class PnLSeriesMapRowMapper1 implements RowMapper<ProfitAndLossData> {
    public ProfitAndLossData mapRow(ResultSet rs, int rowNum) throws SQLException {
      ProfitAndLossData pnlData = new ProfitAndLossData();
      String cat = rs.getString("HEADER");
      pnlData.setAttribute(cat.substring(cat.indexOf("-")+1, cat.length()));
      pnlData.setMonApr(rs.getFloat("CFY_APR"));
      pnlData.setMonMay(rs.getFloat("CFY_MAY"));
      pnlData.setMonJun(rs.getFloat("CFY_JUN"));
      pnlData.setMonJul(rs.getFloat("CFY_JUl"));
      pnlData.setMonAug(rs.getFloat("CFY_AUG"));
      pnlData.setMonSep(rs.getFloat("CFY_SEP"));
      pnlData.setMonOct(rs.getFloat("CFY_OCT"));
      pnlData.setMonNov(rs.getFloat("CFY_NOV"));
      pnlData.setMonDec(rs.getFloat("CFY_DEC"));
      pnlData.setMonJan(rs.getFloat("CFY_JAN"));
      pnlData.setMonFeb(rs.getFloat("CFY_FEB"));
      pnlData.setMonMar(rs.getFloat("CFY_MAR"));
      float total = pnlData.getMonApr()+pnlData.getMonMay()+pnlData.getMonJun()+pnlData.getMonJul()+pnlData.getMonAug()+pnlData.getMonSep()+pnlData.getMonOct()+pnlData.getMonNov()+
      pnlData.getMonDec()+pnlData.getMonJan()+pnlData.getMonFeb()+pnlData.getMonMar();
      pnlData.setTotal(total); 
     
      return pnlData;
    }
  }

  @Override
  public List<CasumData> getPoReceived(User user) {
    String getCasumQueryFinal = "";
    List<CasumData> lstCasumData;
    
    if (user.getProjectSelected() != null && !user.getProjectSelected().equals("")) {

      getCasumQueryFinal = getPoReceivedDtl + " PROJECT_ID IN (?) " + getPoReceivedDtl1;
      lstCasumData = jdbcMysql.query(getCasumQueryFinal,new Object[] {user.getProjectSelected()}, new CasumRowMapper());

    }
    else {
      getCasumQueryFinal = getPoReceivedDtl + user.getRoleName() + "_ID = ? "+getPoReceivedDtl1 ;
      lstCasumData = jdbcMysql.query(getCasumQueryFinal, new Object[] {user.getUsername()},new CasumRowMapper());
    }
    return lstCasumData;
  }
  
  public static class CasumRowMapper implements RowMapper<CasumData> {
    public CasumData mapRow(ResultSet rs, int rowNum) throws SQLException {
       
        CasumData casumData = new CasumData();
        casumData.setPoNum(rs.getString("PO_NUM"));
        casumData.setContrctNum(rs.getString("CNTRCT_NUM"));
        casumData.setContrctAmnt(rs.getString("CNTRCT_AMT"));
        casumData.setContrctStatus(rs.getString("CNTRCT_STATUS"));
        casumData.setContrctStartDt(rs.getString("CNTRCT_START_DATE"));
        casumData.setContrctEndDt(rs.getString("CNTRCT_END_DATE"));
        casumData.setProjectId(rs.getString("PROJECT_ID"));
        casumData.setProjectDesc(rs.getString("PROJECT_DESC"));
        casumData.setCustomerName(rs.getString("CUST_NAME"));
        casumData.setOptyDesc(rs.getString("OPTY_DESC"));
        casumData.setIbuHeadName(rs.getString("D_IBU_HEAD_NAME"));
        casumData.setPgmName(rs.getString("PGM_NAME"));
        casumData.setPmName(rs.getString("PM_NAME"));
        casumData.setProjectType(rs.getString("PROJECT_N_TYPE"));
            
      return casumData;
    }
  }

  @Override
  public List<Casum> getCasum(String user) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<CasumData> getRevenueProjData(User user) {
    String getRevProjQueryFinal = "";
    String getRevProjTargetFinal = "";
    String groupBy = "";
   
    List<CasumData> lstCasumData = new ArrayList<CasumData>();
    List<CasumData> lstTargetData = new ArrayList<CasumData>();
    
        getRevProjQueryFinal = getRevProjectionDtl + user.getRoleName() + "_ID = ? " ;
  
            getRevProjQueryFinal = getFilterProject(getRevProjQueryFinal, user ,false);
     
            getRevProjQueryFinal = getFilterPM(getRevProjQueryFinal, user ,false);
   
            getRevProjQueryFinal =  getFilterPGM(getRevProjQueryFinal, user ,false);
        
            getRevProjQueryFinal =  getFilterSPGM(getRevProjQueryFinal, user,false);
         
            
            getRevProjQueryFinal =  getFilterIBU(getRevProjQueryFinal, user,false);
          
            getRevProjQueryFinal =  getFilterIBG(getRevProjQueryFinal, user,false);
          
            getRevProjQueryFinal =  getFilterSBU(getRevProjQueryFinal, user,false);
           
          
          
          getRevProjQueryFinal = getRevProjQueryFinal + getRevProjectionDtl1 ;
          
        lstCasumData = jdbcMysql.query(getRevProjQueryFinal, new Object[] {user.getUsername()},
            new CasumSeriesMapRowMapper());
      
        getRevProjTargetFinal = getRevProjTarget + user.getRoleName() + "_ID = ? " ;
        
        groupBy = "GROUP BY " + user.getRoleName() + "_ID";
        
   
        getRevProjTargetFinal = getFilterProject(getRevProjTargetFinal, user ,false);
        
          getRevProjTargetFinal = getFilterPM(getRevProjTargetFinal, user ,false);

          getRevProjTargetFinal =  getFilterPGM(getRevProjTargetFinal, user ,false);
        
          getRevProjTargetFinal =  getFilterSPGM(getRevProjTargetFinal, user,true);
                
          getRevProjTargetFinal =  getFilterIBU(getRevProjTargetFinal, user,true);
        
          getRevProjTargetFinal =  getFilterIBG(getRevProjTargetFinal, user,true);
         
          getRevProjTargetFinal =  getFilterSBU(getRevProjTargetFinal, user,true);
          
          
          
          if( user.getSbuName() !=null && !user.getSpgmName().equals("---All SBU---")) {
            
            groupBy = " GROUP BY SBU ";
          }
          
          if( user.getIbgName()!=null &&  !user.getIbgName().equals("---All IBG---")) {
            
            groupBy = " GROUP BY IBG ";
          }
          
          if( user.getIbuName()!=null &&  !user.getIbuName().equals("---All IBU---")) {
            
            groupBy = " GROUP BY IBU ";
          }
          
          if( user.getSpgmName() !=null && !user.getSpgmName().equals("---All Sales Mangers---")) {
            
            groupBy = " GROUP BY SALES_MGR_NAME ";
          }
          
          if( user.getPgmName() !=null && !user.getPgmName().equals("---All PGMs---")) {
            
            groupBy = " GROUP BY PGM_NAME ";
          }
          
          if( user.getPgmName() !=null && !user.getPgmName().equals("---All PMs---")) {
            
            groupBy = " GROUP BY PM_NAME ";
          }
          
          
          getRevProjTargetFinal = getRevProjTargetFinal + groupBy ;
       
        lstTargetData = jdbcMysql.query(getRevProjTargetFinal, new Object[] {user.getUsername()},
            new CasumSeriesMapRowMapper());
        
        if(lstTargetData!=null && lstTargetData.size() > 0) {
        
          lstCasumData.add(lstTargetData.get(0));
        }
        else {
          
          CasumData pnlData = new CasumData();
          pnlData.setCategory("a-Target");
          pnlData.setCfyQOne(0);
          pnlData.setCfyQTwo(0);
          pnlData.setCfyQThree(0);
          pnlData.setCfyQFour(0);
          lstCasumData.add(pnlData);
        }
       
    return lstCasumData;
  }

  public static class CasumSeriesMapRowMapper implements RowMapper<CasumData> {
    public CasumData mapRow(ResultSet rs, int rowNum) throws SQLException {
      CasumData pnlData = new CasumData();
      pnlData.setCategory(rs.getString("CATEGORY"));
      pnlData.setCfyQOne(rs.getFloat("CFYQ1"));
      pnlData.setCfyQTwo(rs.getFloat("CFYQ2"));
      pnlData.setCfyQThree(rs.getFloat("CFYQ3"));
      pnlData.setCfyQFour(rs.getFloat("CFYQ4"));
      return pnlData;
    }
  }




}
