package com.spring.employees.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.employees.model.InterEmpDAO;

@Service
public class EmpService implements InterEmpService {

	@Autowired
	private InterEmpDAO dao;

	// 부서번호 가져오기
	@Override
	public List<String> deptIdList() {
		List<String> deptIdList = dao.deptIdList();
		return deptIdList;
	}

	// employees 테이블에서 조건에 만족하는 사원들을 가져오기
	@Override
	public List<HashMap<String, String>> empList(HashMap<String, Object> paraMap) {
		List<HashMap<String, String>> empList = dao.empList(paraMap);
		return empList;
	}

	// employees 테이블에서 부서명별 인원수 및 퍼센티지 가져오기
	@Override
	public List<HashMap<String, String>> deptnameJSON() {
		List<HashMap<String, String>> deptnamePercentageList = dao.deptnameJSON();
		return deptnamePercentageList;
	}

	// employees 테이블에서 성별 인원수 및 퍼센티지 가져오기
	@Override
	public List<HashMap<String, String>> genderJSON() {
		List<HashMap<String, String>> genderPercentageList = dao.genderJSON();
		return genderPercentageList;
	}
	
}
