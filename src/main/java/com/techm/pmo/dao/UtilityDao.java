package com.techm.pmo.dao;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.techm.pmo.model.User;

public interface UtilityDao {

	public List<String> getCdmNames();

	public List<String> getBrmNames();

	public List<String> getIbuHeadNames();

	public List<String> getGeNongeNames();

	public List<String> getDysonNames();

	public List<String> getIbuSNames();

	public List<String> getBizNames();

	public List<String> getSbuBizNames();

	public List<String> getCompetencyNames();

	public List<String> getProjAnnNames();

	public List<String> getHwTwNames();

	public List<String> getVerticalNames();

}
