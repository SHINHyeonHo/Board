package com.spring.employees.model;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmpDAO implements InterEmpDAO {

	@Resource
	private SqlSessionTemplate sqlsession3;

	// 부서번호가져오기
	@Override
	public List<String> deptIdList() {
		List<String> deptIdList = sqlsession3.selectList("emp.deptIdList");
		return deptIdList;
	}

	// employees 테이블에서 조건에 만족하는 사원들을 가져오기
	@Override
	public List<HashMap<String, String>> empList(HashMap<String, Object> paraMap) {
		List<HashMap<String, String>> empList = sqlsession3.selectList("emp.empList", paraMap);
		return empList;
	}
	
}
