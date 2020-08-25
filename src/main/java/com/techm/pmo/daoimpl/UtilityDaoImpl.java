package com.techm.pmo.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.techm.pmo.dao.UtilityDao;
import com.techm.pmo.model.User;

@Service("utilityDao")
public class UtilityDaoImpl implements UtilityDao {

	@Autowired
	@Qualifier("jdbcMysql")
	private JdbcTemplate jdbcMysql;

	private String getCdmNames = "select CDM_NAME from UTILITY_DEMO";
	private String getBrmNames = "select BRM_NAME from UTILITY_DEMO";
	private String getIbuHeadNames = "select IBU_HEAD_NAME_S from UTILITY_DEMO";
	private String getIbuSNames = "select IBU_S from UTILITY_DEMO";
	private String getBizNames = "select BIZ from UTILITY_DEMO";
	private String getSbuBizNames = "select SUB_BIZ from UTILITY_DEMO";
	private String getCompetencyNames = "select COMPETENCY from UTILITY_DEMO";
	private String getProjAnnNames = "select PROJ_ANN from UTILITY_DEMO";
	private String getHwTwNames = "select HW_TW from UTILITY_DEMO";
	private String getGeNongeNames = "select GE_NONGE from UTILITY_DEMO";
	private String getVerticalNames = "select VERTICAL from UTILITY_DEMO";
	private String getDysonNames = "select DYSON_FLAG from UTILITY_DEMO";

	@Override
	public List<String> getCdmNames() {

		List<String> data = jdbcMysql.query(getCdmNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getBrmNames() {
		List<String> data = jdbcMysql.query(getBrmNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getIbuHeadNames() {
		List<String> data = jdbcMysql.query(getIbuHeadNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getIbuSNames() {
		List<String> data = jdbcMysql.query(getIbuSNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getBizNames() {
		List<String> data = jdbcMysql.query(getBizNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getSbuBizNames() {
		List<String> data = jdbcMysql.query(getSbuBizNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getCompetencyNames() {
		List<String> data = jdbcMysql.query(getCompetencyNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getProjAnnNames() {
		List<String> data = jdbcMysql.query(getProjAnnNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getHwTwNames() {
		List<String> data = jdbcMysql.query(getHwTwNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getVerticalNames() {
		List<String> data = jdbcMysql.query(getVerticalNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getDysonNames() {
		List<String> data = jdbcMysql.query(getDysonNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

	@Override
	public List<String> getGeNongeNames() {
		List<String> data = jdbcMysql.query(getGeNongeNames, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		return data;
	}

}
