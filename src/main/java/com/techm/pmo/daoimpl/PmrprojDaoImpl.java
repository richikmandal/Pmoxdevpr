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
import com.techm.pmo.model.Pmrdata;
import com.techm.pmo.model.PrjmasterData;
import com.techm.pmo.model.ProfitAndLossData;
import com.techm.pmo.model.ResourceBaseData;
import com.techm.pmo.model.User;
import com.techm.pmo.util.PmoxUtil;

@Service
public class PmrprojDaoImpl implements PmrprojDao {

  @Autowired
  @Qualifier("jdbcMysql")
  private JdbcTemplate jdbcMysql;

  private String      getPrjCntFrUser     = "SELECT COUNT(*) FROM PMOX.T_PROJECT_MASTER ";

  private String       getPmrDatafrUser    =
      "SELECT Business_Unit, projectId, projectDesc, custId, IBU, IBUDescription,  IbuHeadName, Status, IBG_Description, Project_Main_Type, Project_Type, Project_Start_Date, "
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

  String               getPrjMasterData    =
      " SELECT PROJECT_ID, PROJECT_DESC, STATUS, PROJECT_START_DATE, PROJECT_END_DATE, CLOSURE_DATE, SBU, IBG, IBU, PROJECT_IBU_HEAD_ID, PROJECT_IBU_HEAD_NAME, PGM_ID, PGM_NAME, PM_ID, PM_NAME,PROJECT_TYPE, DELIVERY_OWNERSHIP, "
          + " PRICING_MODEL FROM PMOX.T_PROJECT_MASTER ";


  String               getPrjAssoctData    = " with rws as ( "
      + "  select ON_OFF,PJM.PROJECT_ID,PJM.PM_ID,PJM.PGM_ID,PJM.STATUS,PJM.PROJECT_IBU_HEAD_ID from PMOX.T_RESOURCE_BASE REBS JOIN PMOX.T_PROJECT_MASTER PJM  ON PJM.PROJECT_ID = REBS.PROJECT_ID "
      + ") select * from rws  pivot ( count(*) for ON_OFF in ('OFFSHORE' AS OFFSHORE, 'ONSITE' AS ONSITE ) ) ";

  String               getPrjRevEbidta     =
      "SELECT ROUND((NVL(REV_TOTAL,0)),2) AS REV_TOTAL,ROUND(NVL(EBIDTA,0),2) AS EBIDTA FROM ( SELECT ROUND(SUM(REV_TOTAL),2) AS REV_TOTAL,"
          + " CASE when SUM(REV_TOTAL) <> 0 THEN (SUM(EBIDTA)*100)/SUM(REV_TOTAL) END  AS EBIDTA FROM T_PNL_BASE PNL JOIN PMOX.T_PROJECT_MASTER PJM  "
          + " ON PJM.PROJECT_ID = pnl.PROJECT_ID ";
  String               getPrjRevEbidta1    = " GROUP BY FY ) A";
  String               getPrjRevEbidta2    =
      " AND UPPER(\"MONTH\") = TRIM(to_char(add_months( sysdate, -1 ), 'MONTH')) GROUP BY FY ";

  String               getPrjResourcData   =
      "SELECT EMP_ID, EMP_NAME, GENDER, CATEGORY_CODE, HTR_FLAG, RSB.IBU, RSB.IBG, \"CLUSTER\", EMAIL_ID, BAND, EXPERIENCE, COUNTRY, CITY, ON_OFF, RSB.PROJECT_ID, RSB.PROJECT_DESC, REGULAR_CONTRACT "
          + " FROM PMOX.T_RESOURCE_BASE RSB JOIN PMOX.T_PROJECT_MASTER PGM ON PGM.PROJECT_ID = RSB.PROJECT_ID WHERE ";

  String               getPrjPnLData       =
      "SELECT PNL.PROJECT_ID, PNL.PROJECT_DESC, \"MONTH\", QTR, FY, ON_HC, OF_HC, TOT_HC, E, H, M, T, P1, P2, U1, U2, U3, U4, UJ, REV_TOTAL, SAL_ON, SAL_OFF, SAL_TOTAL, VISA_WP, "
          + " TRAVEL_FOREIGN, TRAVEL_INLAND, SUBCON_EXPNS, SUBCON_EXPNS_ON, SUBCON_EXPNS_OFF, TOTAL_SUBCON_EXPNS, PROJECT_EXPNS, RING_FENCING, IBU_BUFFER_COST, IBG_BUFFER_COST, "
          + " SBU_BUFFER_COST, ALLOC_DIRECT_COST, OTHER_EXPNS, TOTAL_DIRECT_COST, CONTR, SGNA, EBIDTA, PGM_ID, PM_ID FROM PMOX.T_PNL_BASE PNL JOIN PMOX.T_PROJECT_MASTER PGM ON PGM.PROJECT_ID = PNL.PROJECT_ID WHERE ";
  String               getPmSeriesData     = "  with rws as ( "
      + "  select PJM.PROJECT_ID,PJM.PM_ID,PJM.PGM_ID,PJM.STATUS,REV_TOTAL,PM_NAME from PMOX.T_PNL_BASE PNL JOIN PMOX.T_PROJECT_MASTER PJM  ON PJM.PROJECT_ID = PNL.PROJECT_ID "
      + " ) select PGM_ID,PM_NAME,PM_ID,ROUND(SUM(REV_TOTAL),2) AS REV_TOTAL from rws ";
  String               getPmSeriesData1    = " GROUP BY PM_ID,PM_NAME,PGM_ID";

  String               getRevEbidtaConsQry =
      " select SUM(JANUARY_EBIDTA)*100/SUM(JANUARY_REV_TOTAL) AS JANUARY_EBIDTA,SUM(FEBRUARY_EBIDTA)*100/SUM(FEBRUARY_REV_TOTAL) AS FEBRUARY_EBIDTA,"
          + " SUM(MARCH_EBIDTA)*100/SUM(MARCH_REV_TOTAL) AS MARCH_EBIDTA,SUM(APRIL_EBIDTA)*100/SUM(APRIL_REV_TOTAL) AS APRIL_EBIDTA,"
          + " SUM(MAY_EBIDTA)*100/SUM(MAY_REV_TOTAL) AS MAY_EBIDTA,SUM(JUNE_EBIDTA)*100/SUM(JUNE_REV_TOTAL) AS JUNE_EBIDTA,"
          + " SUM(JULY_EBIDTA)*100/SUM(JULY_REV_TOTAL) AS JULY_EBIDTA,SUM(AUGUST_EBIDTA)*100/SUM(AUGUST_REV_TOTAL) AS AUGUST_EBIDTA,\r\n"
          + " SUM(SEPTEMBER_EBIDTA)*100/SUM(SEPTEMBER_REV_TOTAL) AS SEPTEMBER_EBIDTA,SUM(OCTOBER_EBIDTA)*100/SUM(OCTOBER_REV_TOTAL) AS OCTOBER_EBIDTA,"
          + " SUM(NOVEMBER_EBIDTA)*100/SUM(NOVEMBER_REV_TOTAL) AS NOVEMBER_EBIDTA,SUM(DECEMBER_EBIDTA)*100/SUM(DECEMBER_REV_TOTAL) AS DECEMBER_EBIDTA,"
          + " SUM(JANUARY_REV_TOTAL) AS JANUARY_REV_TOTAL,SUM(FEBRUARY_REV_TOTAL) AS FEBRUARY_REV_TOTAL,SUM(MARCH_REV_TOTAL) AS MARCH_REV_TOTAL,SUM(APRIL_REV_TOTAL) AS APRIL_REV_TOTAL,"
          + " SUM(MAY_REV_TOTAL) AS MAY_REV_TOTAL,SUM(JUNE_REV_TOTAL) AS JUNE_REV_TOTAL,SUM(JULY_REV_TOTAL) AS JULY_REV_TOTAL,SUM(AUGUST_REV_TOTAL) AS AUGUST_REV_TOTAL, "
          + " SUM(SEPTEMBER_REV_TOTAL) AS SEPTEMBER_REV_TOTAL,SUM(OCTOBER_REV_TOTAL) AS OCTOBER_REV_TOTAL,SUM(NOVEMBER_REV_TOTAL) AS NOVEMBER_REV_TOTAL,SUM(DECEMBER_REV_TOTAL) AS DECEMBER_REV_TOTAL"
          + " from ( select * from ( "
          + " select \"MONTH\",PJM.PROJECT_ID,PJM.PM_ID,PM_NAME,PJM.PGM_ID,PGM_NAME,PJM.STATUS,PJM.PROJECT_IBU_HEAD_ID,PROJECT_IBU_HEAD_NAME,REV_TOTAL,EBIDTA from PMOX.T_PNL_BASE PNL "
          + " JOIN PMOX.T_PROJECT_MASTER PJM  ON PJM.PROJECT_ID = PNL.PROJECT_ID "
          + " ) pivot ( sum(NVL(REV_TOTAL,0)) as REV_TOTAL , SUM(NVL(EBIDTA,0)) AS EBIDTA for \"MONTH\" in ('JANUARY' AS JANUARY, 'FEBRUARY' AS FEBRUARY ,'MARCH' AS MARCH,'APRIL' AS APRIL, "
          + " 'MAY' AS MAY,'JUNE' AS JUNE,'JULY' AS JULY,'AUGUST' AS AUGUST,'SEPTEMBER' AS SEPTEMBER,'OCTOBER' AS OCTOBER, "
          + " 'NOVEMBER' AS NOVEMBER,'DECEMBER' AS DECEMBER) )) WHERE  ";
  String getPnLData1 = "SELECT category,SUM(APRIL) APRIL ,SUM(MAY) MAY,SUM(JUNE) JUNE,SUM(JULY) JULY,SUM(AUGUST) AUGUST,SUM(SEPTEMBER) SEPTEMBER,SUM(OCTOBER) OCTOBER, " + 
      " SUM(NOVEMBER) NOVEMBER,SUM(DECEMBER) DECEMBER,SUM(JANUARY) JANUARY,SUM(FEBRUARY) FEBRUARY,SUM(MARCH) MARCH FROM PMOX.V_PNL_PROJECT WHERE ";
  String getPnLData2 = " GROUP BY category ORDER BY category";

  @Override
  public User getPmrDataFrUser(User user) {


    final RowMapper<User> mapper = new ProjectRowMapper();

    String getPrjCntFrUserFinal = "";

    String getPrjAssoct = "";

    String getPrjRevEbidtaFinal = "";

    User usrRev = new User();

    List<User> usrData = new ArrayList<User>();

    try {

      if (user.getProjectSelected() != null && !user.getProjectSelected().equals("")) {

        getPrjCntFrUserFinal = getPrjCntFrUser + "WHERE PROJECT_ID IN (?) ";
        user.setTotalProjectCount(jdbcMysql.queryForObject(getPrjCntFrUserFinal,
            new Object[] {user.getProjectSelected()}, Integer.class));
        if (user.getTotalProjectCount() > 0) {
          getPrjAssoct = getPrjAssoctData + "WHERE PROJECT_ID IN (?) AND STATUS = 'ACTIVE' ";
          usrData = (List<User>) jdbcMysql.query(getPrjAssoct,
              new Object[] {user.getProjectSelected()}, mapper);

          getPrjRevEbidtaFinal =
              getPrjRevEbidta + "WHERE PJM.PROJECT_ID IN (?) " + getPrjRevEbidta1;
          usrRev = (User) jdbcMysql.queryForObject(getPrjRevEbidtaFinal,
              new Object[] {user.getProjectSelected()},
              (rs, rowNum) -> new User(rs.getString("REV_TOTAL"), rs.getString("EBIDTA")));
        }
      }
      else {

        getPrjCntFrUserFinal = getPrjCntFrUser + "WHERE " + user.getRoleName() + "_ID = ? ";
        user.setTotalProjectCount(jdbcMysql.queryForObject(getPrjCntFrUserFinal,
            new Object[] {user.getUsername()}, Integer.class));
        if (user.getTotalProjectCount() > 0) {
          getPrjAssoct =
              getPrjAssoctData + "WHERE " + user.getRoleName() + "_ID = ? AND STATUS = 'ACTIVE' ";
          usrData = (List<User>) jdbcMysql.query(getPrjAssoct, new PreparedStatementSetter() {

            public void setValues(PreparedStatement preparedStatement) throws SQLException {
              preparedStatement.setString(1, user.getUsername());
            }
          }, mapper);

          getPrjRevEbidtaFinal =
              getPrjRevEbidta + "WHERE " + user.getRoleName() + "_ID = ? " + getPrjRevEbidta1;
          usrRev = (User) jdbcMysql.queryForObject(getPrjRevEbidtaFinal,
              new Object[] {user.getUsername()},
              (rs, rowNum) -> new User(rs.getFloat("REV_TOTAL") + "", rs.getFloat("EBIDTA") + ""));
        }
      }

      user.setTotalRevenue(usrRev.getTotalRevenue());
      user.setTotalEbidta(usrRev.getTotalEbidta());

      int offShoreCount = 0;
      int onShoreCount = 0;
      for (User usr : usrData) {
        offShoreCount = offShoreCount + usr.getTotalOffShoreCount();
        onShoreCount = onShoreCount + usr.getTotalOnShoreCount();
      }

      user.setTotalOffShoreCount(offShoreCount);
      user.setTotalOnShoreCount(onShoreCount);
    }
    catch (Exception ex) {
      // throw ex.printStackTrace();;
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
      // pmrdata.setCustId(rs.getInt("custId"));
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
    List<PrjmasterData> result =
        (List<PrjmasterData>) jdbcMysql.query(getPrjMasterData, new PreparedStatementSetter() {

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
  public User getProjectMasterDataForPGM(User user) {

    String getPrjMasterDataFinal = "";
    List<PrjmasterData> ProjMap = new ArrayList<PrjmasterData>();

    if (user.getProjectSelected() != null && !user.getProjectSelected().equals("")) {
      getPrjMasterDataFinal = getPrjMasterData + "WHERE PROJECT_ID IN (?) ORDER BY PM_ID";
      ProjMap = jdbcMysql.query(getPrjMasterDataFinal, new Object[] {user.getProjectSelected()},
          new PrjMasterMapRowMapper());
    }
    else {
      getPrjMasterDataFinal =
          getPrjMasterData + "WHERE " + user.getRoleName() + "_ID = ? ORDER BY PM_ID";
      ProjMap = jdbcMysql.query(getPrjMasterDataFinal, new Object[] {user.getUsername()},
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
      pmData.setIbuHeadId(rs.getString("PROJECT_IBU_HEAD_ID"));
      pmData.setIbuHeadName(rs.getString("PROJECT_IBU_HEAD_NAME"));
      pmData.setPgmId(rs.getString("PGM_ID"));
      pmData.setPgmName(rs.getString("PGM_NAME"));
      pmData.setPmId(rs.getString("PM_ID"));
      pmData.setPmName(rs.getString("PM_NAME"));
      // pmData.setProjectMainType(rs.getString("PROJECT_MAIN_TYPE"));
      pmData.setProjectType(rs.getString("PROJECT_TYPE"));
      pmData.setDeliveryOwnership(rs.getString("DELIVERY_OWNERSHIP"));
      // pmData.setProjectCriticality(rs.getString("PROJECT_CRITICALITY"));
      pmData.setPricingModel(rs.getString("PRICING_MODEL"));
     
      return pmData;
    }
  }

  @SuppressWarnings("unchecked")
  public User getResourceDataForPGM(User user) {

    String getPrjResourcDataFinal = "";
    Map<String, List<ResourceBaseData>> resourceMap = new HashMap<String, List<ResourceBaseData>>();

    if (user.getProjectSelected() != null && !user.getProjectSelected().equals("")) {
      getPrjResourcDataFinal = getPrjResourcData + " RSB.PROJECT_ID IN (?) ORDER BY PROJECT_ID";
      resourceMap = (Map<String, List<ResourceBaseData>>) jdbcMysql.query(getPrjResourcDataFinal,
          new Object[] {user.getProjectSelected()}, new ResourceMapExtractor());
    }
    else {
      getPrjResourcDataFinal =
          getPrjResourcData + user.getRoleName() + "_ID = ? ORDER BY PROJECT_ID";
      resourceMap = jdbcMysql.query(getPrjResourcDataFinal, new Object[] {user.getUsername()},
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
      rsData.setEmpId(rs.getString("EMP_ID"));
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
        rsData.setIbg(rs.getString("IBG"));
        rsData.setIbu(rs.getString("IBU"));
        rsData.setEmpId(rs.getString("EMP_ID"));
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
      projectMap.put(lastPrjId + "", prjMasterList);
      return projectMap;
    }
  }

  @SuppressWarnings("unchecked")
  public User getPandLDataForPGM(User user) {

    String getPrjPnLDataFinal = "";
    Map<String, List<ProfitAndLossData>> pandlMap = new HashMap<String, List<ProfitAndLossData>>();

    if (user.getProjectSelected() != null && !user.getProjectSelected().equals("")) {
      getPrjPnLDataFinal = getPrjPnLData + " PNL.PROJECT_ID IN (?) ORDER BY PROJECT_ID";
      pandlMap = (Map<String, List<ProfitAndLossData>>) jdbcMysql.query(getPrjPnLDataFinal,
          new Object[] {user.getProjectSelected()}, new PandLMapExtractor());
    }
    else {
      getPrjPnLDataFinal = getPrjPnLData + user.getRoleName() + "_ID = ? ORDER BY PROJECT_ID";
      pandlMap = jdbcMysql.query(getPrjPnLDataFinal, new Object[] {user.getUsername()},
          new PandLMapExtractor());
    }

    user.setPandlMap(pandlMap);

    return user;

  }

  @SuppressWarnings("unused")
  private static final class PandLMapExtractor
      implements ResultSetExtractor<Map<String, List<ProfitAndLossData>>> {
    @Override
    public Map<String, List<ProfitAndLossData>> extractData(ResultSet rs) throws SQLException {
      Map<String, List<ProfitAndLossData>> projectMap =
          new HashMap<String, List<ProfitAndLossData>>();
      List<ProfitAndLossData> pnlMasterList = null;
      String lastPrjId = "";
      while (rs.next()) {

        String prjId = rs.getString("PROJECT_ID");

        if (!prjId.equals(lastPrjId)) {
          if (lastPrjId != null && !lastPrjId.equals("")) {
            projectMap.put(lastPrjId + ":" + rs.getString("PGM_ID") + ":" + rs.getString("PM_ID"),
                pnlMasterList);
          }
          lastPrjId = prjId;
          pnlMasterList = new ArrayList<ProfitAndLossData>();
        }

        ProfitAndLossData rsData = new ProfitAndLossData();

        rsData.setProjectId(prjId);
        rsData.setProjectDescription(rs.getString("PROJECT_DESC"));
        rsData.setMonth(rs.getString("MONTH"));
        rsData.setQuarter​(rs.getString("QTR"));
        rsData.setFy(rs.getString("FY"));
        rsData.setOnHc(rs.getString("ON_HC"));
        rsData.setOffHc(rs.getString("OF_HC"));
        rsData.setTotalHc(rs.getString("TOT_HC"));
        rsData.seteBand(rs.getString("E"));
        rsData.sethBand(rs.getString("H"));
        rsData.setmBand(rs.getString("M"));
        rsData.settBand(rs.getString("T"));
        rsData.setP1Band(rs.getString("P1"));
        rsData.setP2Band(rs.getString("P2"));
        rsData.setU1Band(rs.getString("U1"));
        rsData.setU2Band(rs.getString("U2"));
        rsData.setU3Band(rs.getString("U3"));
        rsData.setU4Band(rs.getString("U4"));
        rsData.setuJBand(rs.getString("UJ"));
        rsData.setRevTotal(rs.getString("REV_TOTAL"));
        rsData.setSalOnsite(rs.getString("SAL_ON"));
        rsData.setSalOffshore(rs.getString("SAL_OFF"));
        rsData.setSalaryTotal(rs.getString("SAL_TOTAL"));
        rsData.setVisaWP(rs.getString("VISA_WP"));
        rsData.setTravelForeign(rs.getString("TRAVEL_FOREIGN"));
        rsData.setTravelInland(rs.getString("TRAVEL_INLAND"));

        rsData.setSubconExpenses(rs.getString("SUBCON_EXPNS"));
        rsData.setSubconExpOnsite(rs.getString("SUBCON_EXPNS_ON"));
        rsData.setSubconExpOffshore(rs.getString("SUBCON_EXPNS_OFF"));
        rsData.setTotalSubconExpenses(rs.getString("TOTAL_SUBCON_EXPNS"));
        rsData.setProjectExpens(rs.getString("PROJECT_EXPNS"));
        rsData.setRingFencing(rs.getString("RING_FENCING"));
        rsData.setIbuBufferCost(rs.getString("IBU_BUFFER_COST"));
        rsData.setIbgBufferCost(rs.getString("IBG_BUFFER_COST"));
        rsData.setSbuBufferCost(rs.getString("SBU_BUFFER_COST"));
        rsData.setAllocDirectCost(rs.getString("ALLOC_DIRECT_COST"));
        rsData.setOtherExpense(rs.getString("OTHER_EXPNS"));
        rsData.setTotalDirectCost(rs.getString("TOTAL_DIRECT_COST"));
        rsData.setContr(rs.getString("CONTR"));
        rsData.setSgna(rs.getString("SGNA"));
        rsData.setEbidta(rs.getString("EBIDTA"));


        pnlMasterList.add(rsData);

      }
      projectMap.put(lastPrjId + "", pnlMasterList);
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Casum> getCasum(String user) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ProfitAndLossData> getPnLSummary(User user) {

    String getPmSeriesQueryFinal = "";
    List<ProfitAndLossData> lstPnLSeriesData;
    
    if (user.getProjectSelected() != null && !user.getProjectSelected().equals("")) {

      getPmSeriesQueryFinal = getPnLData1 + " PROJECT_ID IN (?) " + getPnLData2;
      lstPnLSeriesData = jdbcMysql.query(getPmSeriesQueryFinal,
          new Object[] {user.getProjectSelected()}, new PnLSeriesMapRowMapper());

    }
    else {
      getPmSeriesQueryFinal = getPnLData1 + user.getRoleName() + "_ID = ? " + getPnLData2;
      lstPnLSeriesData = jdbcMysql.query(getPmSeriesQueryFinal, new Object[] {user.getUsername()},
          new PnLSeriesMapRowMapper());
    }
    return lstPnLSeriesData;
  }

  public static class PnLSeriesMapRowMapper implements RowMapper<ProfitAndLossData> {
    public ProfitAndLossData mapRow(ResultSet rs, int rowNum) throws SQLException {
      ProfitAndLossData pnlData = new ProfitAndLossData();
      String cat = rs.getString("CATEGORY");
      pnlData.setAttribute(cat.substring(cat.indexOf("-")+1, cat.length()));
      pnlData.setMonApr(rs.getString("APRIL"));
      pnlData.setMonMay(rs.getString("MAY"));
      pnlData.setMonJun(rs.getString("JUNE"));
      pnlData.setMonJul(rs.getString("JULY"));
      pnlData.setMonAug(rs.getString("AUGUST"));
      pnlData.setMonSep(rs.getString("SEPTEMBER"));
      pnlData.setMonOct(rs.getString("OCTOBER"));
      pnlData.setMonNov(rs.getString("NOVEMBER"));
      pnlData.setMonDec(rs.getString("DECEMBER"));
      pnlData.setMonJan(rs.getString("JANUARY"));
      pnlData.setMonFeb(rs.getString("FEBRUARY"));
      pnlData.setMonMar(rs.getString("MARCH"));
      pnlData.setTotal((Float.parseFloat(pnlData.getMonApr()==null? "0.0" : pnlData.getMonApr())+Float.parseFloat(pnlData.getMonMay()==null? "0.0" :pnlData.getMonMay())+Float.parseFloat(pnlData.getMonJun()==null? "0.0" :pnlData.getMonJun())+Float.parseFloat(pnlData.getMonJul()==null? "0.0" :pnlData.getMonJul())+
      Float.parseFloat(pnlData.getMonAug()==null? "0.0" :pnlData.getMonAug())+Float.parseFloat(pnlData.getMonSep()==null? "0.0" :pnlData.getMonSep())+Float.parseFloat(pnlData.getMonOct()==null? "0.0" :pnlData.getMonOct())+Float.parseFloat(pnlData.getMonNov()==null? "0.0" :pnlData.getMonNov())+
      Float.parseFloat(pnlData.getMonDec()==null? "0.0" :pnlData.getMonDec())+Float.parseFloat(pnlData.getMonJan()==null? "0.0" :pnlData.getMonJan())+Float.parseFloat(pnlData.getMonFeb()==null? "0.0" :pnlData.getMonFeb())+Float.parseFloat(pnlData.getMonMar()==null? "0.0" :pnlData.getMonMar()))+"");
      
     
      return pnlData;
    }
  }



}
