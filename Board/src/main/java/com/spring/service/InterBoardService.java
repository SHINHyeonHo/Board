package com.spring.service;

import java.util.HashMap;
import java.util.List;

import com.spring.board.model.BoardVO;
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

	int add(BoardVO boardvo); // 게시판 글쓰기(파일첨부가 없는 글쓰기)

	List<BoardVO> boardListNoSearch(); // 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기

	BoardVO getView(String seq, String userid); // 글조회수 증가와 함께 글1개를 조회해주는 것.
												// 글조회수 증가는 다른 사람의 글을 읽을 때만 증가하도록 한다.
												// 로그인 하지 않은 상태에서 즉, userid 가 null 값인 상태에서 글을 읽을때 조회수 증가는 일어나지 않도록 한다.

	BoardVO getViewWithNoAddCount(String seq); 	// 글조회수 증가는 없고 단순히 글 1개 조회만을 해주는 것이다.

	int edit(BoardVO boardvo); // 1개글 수정하기

	int del(HashMap<String, String> paraMap); // 글 삭제
	

	
}
