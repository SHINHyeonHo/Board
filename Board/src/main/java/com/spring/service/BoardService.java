package com.spring.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.spring.common.AES256;
import com.spring.member.model.MemberVO;
import com.spring.model.HrVO;
import com.spring.model.InterBoardDAO;
import com.spring.model.TestVO;

// === #31. Service 선언 ===
// 트랜잭션 처리를 담당하는 곳, 업무를 처리하는 곳
@Component // 굳이 이거 안써도 Service 에 포함되어있다.
@Service
public class BoardService implements InterBoardService {

	// === #34. 의존객체 주입하기(DI: Dependency Injection) ===
	@Autowired
	private InterBoardDAO dao;
	// Type 에 따라 Spring 컨테이너가 알아서 root-context.xml 에 생성된 org.spring.model.BoardDAO 의 bean 을 넣어준다. 
	// 그러므로 dao 는 null 이 아니다.

	//  === #45. 양방향 암호화 알고리즘인 AES256 를 사용하여 복호화 하기 위한 클래스(파라미터가 있는 생성자) 의존객체 주입하기(DI: Dependency Injection) === //
	@Autowired
	private AES256 aes;
	
	
	
	@Override
	public int test_insert() {
		
		int n = dao.test_insert();
		int m = dao.test_insert2();
		
		return n*m;
	}

	@Override
	public HashMap<String, List<TestVO>> test_select() {
		
		List<TestVO> testvoList = dao.test_select();
		List<TestVO> testvoList2 = dao.test_select2();
		
		HashMap<String, List<TestVO>> map = new HashMap<>();
		map.put("testvoList", testvoList);
		map.put("testvoList2", testvoList2);
		
		return map;
	}

	// Form 에서 입력받은 것을 받아온다.
	@Override
	public int test_insert(HashMap<String, String> paraMap) {
		int n = dao.test_insert(paraMap); 
		return n;
	}

	@Override
	public int ajaxtest_insert(HashMap<String, String> paraMap) {
		int n = dao.ajaxtest_insert(paraMap);
		return n;
	}
	
	@Override
	public List<TestVO> ajaxtest_select() {
		
		List<TestVO> testvoList = dao.test_select();
		
		return testvoList;
	}

	@Override
	public List<TestVO> datatables_test() {
		List<TestVO> testvoList = dao.test_select();
		return testvoList;
	}
	
	@Override
	public List<HashMap<String, String>> test_employees() {
		List<HashMap<String, String>> empList = dao.test_employees();
		return empList;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// === #37. 메인 페이지용 이미지 파일을 가져오기 === //
	@Override
	public List<String> getImgfilenameList() {
		List<String> imgfilenameList = dao.getImgfilenameList();
		return imgfilenameList;
	}

	// === #42. 로그인 처리하기 === //
	@Override
	public MemberVO getLoginMember(HashMap<String, String> paraMap) {
		
		MemberVO loginuser = dao.getLoginMember(paraMap);
		
		// === #48. aes 의존객체를 사용하여 로그인 되어진 사용자(loginuser)의 이메일 값을 복호화 하도록 한다. === //
		if(loginuser != null) {

			if(loginuser.getLastlogindategap() >= 12) {
				// 마지막으로 로그인 한 날짜 시간이 현재일로 부터 1년(12개월)이 지났으면 해당 로그인 계정을 비활성화(휴면)시킨다.
				loginuser.setIdleStatus(true);
			}
			else {
				if(loginuser.getPwdchangegap() > 3) {
					// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면
					loginuser.setRequirePwdChange(true);
				}
				
				dao.setLastLoginDate(paraMap); // 마지막으로 로그인 한 날짜시간 변경(기록)하기
				
				try {
					loginuser.setEmail(aes.decrypt(loginuser.getEmail()));
					// loginuser 의 email을 복호화 하도록 한다.
				} catch (UnsupportedEncodingException | GeneralSecurityException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return loginuser;
	}
	
}