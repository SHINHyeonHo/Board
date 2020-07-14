package com.spring.service;

import java.util.HashMap;
import java.util.List;

import com.spring.member.model.MemberVO;
import com.spring.model.HrVO;
import com.spring.model.TestVO;

public interface InterBoardService {

	int test_insert();
	
	HashMap<String, List<TestVO>> test_select();

	int test_insert(HashMap<String, String> paraMap);

	int ajaxtest_insert(HashMap<String, String> paraMap);

	List<TestVO> ajaxtest_select();

	List<TestVO> datatables_test();

	List<HashMap<String, String>> test_employees(); // quiz

	///////////////////////////////////////////////////// === 게시판 === /////////////////////////////////////////////////////
	
	List<String> getImgfilenameList(); // 이미지 파일명 가져오기

	MemberVO getLoginMember(HashMap<String, String> paraMap); // 로그인 처리하기
	
}
