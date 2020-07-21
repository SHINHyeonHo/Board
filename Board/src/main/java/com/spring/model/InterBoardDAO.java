package com.spring.model;

import java.util.HashMap;
import java.util.List;

import com.spring.board.model.BoardVO;
import com.spring.board.model.CommentVO;
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
	void pointPlus(HashMap<String, String> paraMap); // AOP 에서 사용하는 것으로 회원에게 포인트 증가를 하기 위한 것이다.
	int addComment(CommentVO commentvo); // 댓글쓰기(tblComment 테이블에 insert) 
	int updateCommentCount(String parentSeq); // tblBoard 테이블에 commentCount 컬럼의 값을 1증가(update)
	
	List<CommentVO> getCommentList(String parentSeq); // 원게시물에 딸린 댓글들을 조회해오는 것
	void deleteComment(HashMap<String, String> paraMap); // 딸린 댓글을 삭제한다.(딸린 댓글이 없을수도 있지만 실행한다.)
	List<BoardVO> boardListSearch(HashMap<String, String> paraMap); // 검색한 List
	List<String> wordSearchShow(HashMap<String, String> paraMap); // 검색어 입력시 자동글 완성하기 
	
}
