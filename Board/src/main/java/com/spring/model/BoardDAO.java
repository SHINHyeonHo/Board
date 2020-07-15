package com.spring.model;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.board.model.BoardVO;
import com.spring.member.model.MemberVO;

// === #32. DAO 선언 ===
// @Component 역할은 bean 에 올리기 위해 사용하는데 repository 에 이미 사용이 가능하다.
@Repository
public class BoardDAO implements InterBoardDAO {

	// === #33. 의존객체 주입하기(DI: Dependency Injection) ===
	// >>> 의존 객체 자동 주입(Automatic Dependency Injection)은
	//     스프링 컨테이너가 자동적으로 의존 대상 객체를 찾아서 해당 객체에 필요한 의존객체를 주입하는 것을 말한다. 
	//     단, 의존객체는 스프링 컨테이너속에 bean 으로 등록되어 있어야 한다. 

	//     의존 객체 자동 주입(Automatic Dependency Injection)방법 3가지 
	//     1. @Autowired ==> Spring Framework에서 지원하는 어노테이션이다. 
	//                       스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다.
	
	//     2. @Resource  ==> Java 에서 지원하는 어노테이션이다.
	//                       스프링 컨테이너에 담겨진 의존객체를 주입할때 필드명(이름)을 찾아서 연결(의존객체주입)한다.
	
	//     3. @Inject    ==> Java 에서 지원하는 어노테이션이다.
    //                       스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다.
	
	/*
	@Autowired // 타입이 한개일 경우
	private SqlSessionTemplate abc;
	*/
	@Resource // 타입이 여러개일 경우
	private SqlSessionTemplate sqlsession;
	
	@Resource
	private SqlSessionTemplate sqlsession2;
	
	@Resource
	private SqlSessionTemplate sqlsession3;
	
	// Type 에 따라 Spring 컨테이너가 알아서 root-context.xml 에 생성된 org.spring.model.BoardDAO 의 bean 에 넣어준다.
	// 그러므로 sqlsession 는 null 이 아니다.
	
	@Override
	public int test_insert() {
		int n = sqlsession.insert("board.test_insert");
		return n;
	}

	@Override
	public int test_insert2() {
		int n = sqlsession2.insert("remote_board.test_insert");
		return n;
	}

	@Override
	public List<TestVO> test_select() {
		List<TestVO> testvoList = sqlsession.selectList("board.test_select");
		return testvoList;
	}
	
	@Override
	public List<TestVO> test_select2() {
		List<TestVO> testvoList = sqlsession2.selectList("remote_board.test_select");
		return testvoList;
	}

	@Override
	public int test_insert(HashMap<String, String> paraMap) {
		int n = sqlsession.insert("board.test_insertPm", paraMap);
		return n;
	}

	@Override
	public int ajaxtest_insert(HashMap<String, String> paraMap) {
		int n = sqlsession.insert("board.ajaxtest_insert", paraMap);
		return n;
	}
	
	@Override
	public List<HashMap<String, String>> test_employees() {
		List<HashMap<String, String>> empList = sqlsession3.selectList("board.test_employees");
		return empList;
	}

	
	///////////////////////////////////////////////// === 게시판 === ///////////////////////////////////////////////////////

	// === #38. 메인 페이지용 이미지 파일을 가져오기 === // 
	@Override
	public List<String> getImgfilenameList() {
		
		List<String> imgfilenameList = sqlsession.selectList("board.getImgfilenameList");
		
		return imgfilenameList;
	}

	// === #46. 로그인 처리하기 === //
	@Override
	public MemberVO getLoginMember(HashMap<String, String> paraMap) {
		
		MemberVO loginuser = sqlsession.selectOne("board.getLoginMember", paraMap);
		
		return loginuser;
	}
	// 마지막으로 로그인 한 날짜시간 변경(기록)하기
	@Override
	public void setLastLoginDate(HashMap<String, String> paraMap) {
		sqlsession.update("board.setLastLoginDate", paraMap);
	}

	// === #56. 글쓰기(파일첨부가 없는 글쓰기) === //
	@Override
	public int add(BoardVO boardvo) {
		int n = sqlsession.insert("board.add", boardvo);
		return n;
	}

	// == #60. 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 == //
	@Override
	public List<BoardVO> boardListNoSearch() {
		List<BoardVO> boardList = sqlsession.selectList("board.boardListNoSearch");
		return boardList;
	}

	// === $64. 글 1개 보여주기 === //
	@Override
	public BoardVO getView(String seq) {
		BoardVO boardvo = sqlsession.selectOne("board.getView", seq);
		return boardvo;
	}

	// === $65. 글조회수 1 증가하기 === //
	@Override
	public void setAddReadCount(String seq) {
		sqlsession.update("board.setAddReadCount", seq);
	}

	
}
