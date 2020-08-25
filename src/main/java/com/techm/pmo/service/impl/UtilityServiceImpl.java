package com.techm.pmo.service.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.techm.pmo.dao.UserDao;
import com.techm.pmo.dao.UtilityDao;
import com.techm.pmo.daoimpl.UtilityDaoImpl;
import com.techm.pmo.model.User;
import com.techm.pmo.service.UserService;
import com.techm.pmo.service.UtilityService;

/**
 * Service class for managing users.
 */
@Service("utilityService")
public class UtilityServiceImpl implements UtilityService {


	@Resource
	private UtilityDao utilityDao;

	@Override
	public List<String> getCdmNames() {
		// TODO Auto-generated method stub
		return utilityDao.getCdmNames();
	}

	@Override
	public List<String> getBrmNames() {
		// TODO Auto-generated method stub
		return utilityDao.getBrmNames();
	}

	@Override
	public List<String> getIbuHeadNames() {
		// TODO Auto-generated method stub
		return utilityDao.getIbuHeadNames();
	}

	@Override
	public List<String> getIbuSNames() {
		// TODO Auto-generated method stub
		return utilityDao.getIbuSNames();
	}

	@Override
	public List<String> getBizNames() {
		// TODO Auto-generated method stub
		return utilityDao.getBizNames();
	}

	@Override
	public List<String> getSbuBizNames() {
		// TODO Auto-generated method stub
		return utilityDao.getSbuBizNames();
	}

	@Override
	public List<String> getCompetencyNames() {
		// TODO Auto-generated method stub
		return utilityDao.getCompetencyNames();
	}

	@Override
	public List<String> getProjAnnNames() {
		// TODO Auto-generated method stub
		return utilityDao.getProjAnnNames();
	}

	@Override
	public List<String> getHwTwNames() {
		// TODO Auto-generated method stub
		return utilityDao.getHwTwNames();
	}

	@Override
	public List<String> getGeNongeNames() {
		// TODO Auto-generated method stub
		return utilityDao.getGeNongeNames();
	}

	@Override
	public List<String> getVerticalNames() {
		// TODO Auto-generated method stub
		return utilityDao.getVerticalNames();
	}

	@Override
	public List<String> getDysonNames() {
		// TODO Auto-generated method stub
		return utilityDao.getDysonNames();
	}


  


}
