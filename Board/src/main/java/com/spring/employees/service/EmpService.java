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
	
}
