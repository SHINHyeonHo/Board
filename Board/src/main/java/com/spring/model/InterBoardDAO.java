package com.spring.model;

import java.util.HashMap;
import java.util.List;

import com.spring.board.model.BoardVO;
import com.spring.member.model.MemberVO;

public interface InterBoardDAO {

	int test_insert();	// spring-test1 테이블에 insert 하기 ! 연습용
	int test_insert2();	// 상대방 spring-test1 테이블에 insert 하기 ! 연습용
	
	List<TestVO> test_select();   // spring_test1 테이블을 select 하기!
	List<TestVO> test_select2();  // 상대방 spring_test2 테이블을 select 하기!
	
	int test_insert(HashMap<String, String> paraMap); // spring_test1 테이블에 insert 하기
	int ajaxtest_insert(HashMap<String, String> paraMap);
	
	List<HashMap<String, String>> test_employees();
	
	////////////////////////////////////////// == 게시판 == //////////////////////////////////////////////////////////
	
	List<String> getImgfilenameList(); // 이미지 파일명 가져오기
	MemberVO getLoginMember(HashMap<String, String> paraMap); // 로그인 처리하기
	void setLastLoginDate(HashMap<String, String> paraMap); // 마지막으로 로그인 한 날짜시간 변경(기록)하기
	int add(BoardVO boardvo); // 글쓰기(파일첨부가 없는 글쓰기)
	List<BoardVO> boardListNoSearch(); // 게시글 목록
	BoardVO getView(String seq); // 1개 글 보여주기
	void setAddReadCount(String seq); // 글조회수 증가
	int updateBoard(BoardVO boardvo);
	int deleteBoard(HashMap<String, String> paraMap); // 글 삭제
	
}
