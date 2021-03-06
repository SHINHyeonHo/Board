package com.spring.employees.service;

import java.util.HashMap;
import java.util.List;

public interface InterEmpService {

	List<String> deptIdList();
	// employees 테이블에서 근무중인 사원들의 부서번호 가져오기

	List<HashMap<String, String>> empList(HashMap<String, Object> paraMap);
	// employees 테이블에서 조건에 만족하는 사원들을 가져오기

	List<HashMap<String, String>> deptnameJSON();
	// employees 테이블에서 부서명별 인원수 및 퍼센티지 가져오기

	List<HashMap<String, String>> genderJSON();
	// employees 테이블에서 성별 인원수 및 퍼센티지 가져오기


}
