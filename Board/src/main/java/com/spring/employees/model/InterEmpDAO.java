package com.spring.employees.model;

import java.util.HashMap;
import java.util.List;

public interface InterEmpDAO {

	List<String> deptIdList(); // 부서번호 가져오기

	// employees 테이블에서 조건에 만족하는 사원들을 가져오기
	List<HashMap<String, String>> empList(HashMap<String, Object> paraMap);

	// employees 테이블에서 부서명별 인원수 및 퍼센티지 가져오기
	List<HashMap<String, String>> deptnameJSON();

	// employees 테이블에서 성별 인원수 및 퍼센티지 가져오기
	List<HashMap<String, String>> genderJSON();

}
