package com.spring.service;

import java.util.HashMap;
import java.util.List;

import com.spring.board.model.BoardVO;
import com.spring.board.model.CommentVO;
import com.spring.member.model.MemberVO;
import com.spring.model.TestVO;

public interface InterBoardService {

	int test_insert();
	
	HashMap<String, List<TestVO>> test_select();

	int test_insert(HashMap<String, String> paraMap);

	int ajaxtest_insert(HashMap<String, String> paraMap);

	List<TestVO> ajaxtest_select();

	List<TestVO> datatables_test();

	List<HashMap<String, String>> test_employees();

	/////////////////////////////////////////////////////////
	List<String> getImgfilenameList();  // 이미지 파일명 가져오기 

	MemberVO getLoginMember(HashMap<String, String> paraMap); // 로그인 처리하기  

	int add(BoardVO boardvo); // 글쓰기(파일첨부가 없는 글쓰기)

	List<BoardVO> boardListNoSearch(); // 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 

	BoardVO getView(String seq, String userid); // 글조회수 증가와 함께 글1개를 조회를 해주는 것  
	                                            // 글조회수 증가는 다른 사람의 글을 읽을때만 증가하도록 한다. 
	                                            // 로그인 하지 않은 상태에서 글을 읽을때 조회수 증가는 일어나지 않도록 한다.   

	BoardVO getViewWithNoAddCount(String seq); // 글조회수 증가는 없고 단순히 글1개 조회만을 해주는 것

	int edit(BoardVO boardvo); // 1개글 수정하기 

//	int del(HashMap<String, String> paraMap); // 1개글 삭제하기 
	int del(HashMap<String, String> paraMap) throws Throwable; // 1개글 삭제하기(딸린 댓글이 있는 경우 댓글도 동시에 삭제한다. 트랜잭션처리)  

	void pointPlus(HashMap<String, String> paraMap); // AOP 에서 사용하는 것으로 회원에게 포인트 증가를 하기 위한것.

	int addComment(CommentVO commentvo) throws Throwable; // 댓글쓰기 

	List<CommentVO> getCommentList(String parentSeq); // 원게시물에 딸린 댓글들을 조회해오는 것

	List<BoardVO> boardListSearch(HashMap<String, String> paraMap); // 페이징 처리를 안한 검색어가 있는 전체 글목록 보여주기 

	List<String> wordSearchShow(HashMap<String, String> paraMap); // 검색어 입력시 자동글 완성하기 

	void scheduleTest1(); // 스프링 스케줄러 연습1

	int getTotalCount(HashMap<String, String> paraMap); // 총 게시물 건수(totalCount)를 구하기(검색이 있을 때와 검색이 없을때로 나뉜다.)

	List<BoardVO> boardListSearchWithPaging(HashMap<String, String> paraMap); // 페이징 처리한 글목록 가져오기(검색이 있든지, 검색이 없든지 모두 다 포함한것) 

	List<CommentVO> getCommentListPaging(HashMap<String, String> paraMap); // 원게시물에 딸린 댓글들을 페이징처리해서 조회해오기

	int getCommentTotalCount(HashMap<String, String> paraMap); // 원글 글번호(parentSeq)에 해당하는 댓글의 총갯수 알아오기
	
}





