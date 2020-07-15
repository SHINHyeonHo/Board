package com.spring.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.spring.board.model.BoardVO;
import com.spring.common.Sha256;
import com.spring.member.model.MemberVO;
import com.spring.model.HrVO;
import com.spring.model.TestVO;
import com.spring.service.InterBoardService;

/*
	사용자 웹브라우저 요청(View)  ==> DispatcherServlet ==> @Controller 클래스 <==>> Service단(핵심업무로직단, business logic단) <==>> Model단[Repository](DAO, DTO) <==>> myBatis <==>> DB(오라클)           
	(http://...  *.action)                                  |                                                                                                                              
	 ↑                                                View Resolver
	 |                                                      ↓
	 |                                                View단(.jsp 또는 Bean명)
	 -------------------------------------------------------| 
	
	사용자(클라이언트)가 웹브라우저에서 http://localhost:9090/board/test_insert.action 을 실행하면
	배치서술자인 web.xml 에 기술된 대로  org.springframework.web.servlet.DispatcherServlet 이 작동된다.
	DispatcherServlet 은 bean 으로 등록된 객체중 controller 빈을 찾아서  URL값이 "/test_insert.action" 으로
	매핑된 메소드를 실행시키게 된다.                                               
	Service(서비스)단 객체를 업무 로직단(비지니스 로직단)이라고 부른다.
	Service(서비스)단 객체가 하는 일은 Model단에서 작성된 데이터베이스 관련 여러 메소드들 중 관련있는것들만을 모아 모아서
	하나의 트랜잭션 처리 작업이 이루어지도록 만들어주는 객체이다.
	여기서 업무라는 것은 데이터베이스와 관련된 처리 업무를 말하는 것으로 Model 단에서 작성된 메소드를 말하는 것이다.
	이 서비스 객체는 @Controller 단에서 넘겨받은 어떤 값을 가지고 Model 단에서 작성된 여러 메소드를 호출하여 실행되어지도록 해주는 것이다.
	실행되어진 결과값을 @Controller 단으로 넘겨준다.
*/

// == #30. 컨트롤러 선언 === //
@Component
/* 	XML에서 빈을 만드는 대신에 클래스명 앞에 @Component 어노테이션을 적어주면 해당 클래스는 bean으로 자동 등록된다. 
	그리고 bean의 이름(첫글자는 소문자)은 해당 클래스명이 된다.
	여기서는 @Controller 를 사용하므로 @Component 기능이 이미 있으므로 @Component를 명기하지 않아도 BoardController 는 bean 으로 등록되어 스프링컨테이너가 자동적으로 관리해준다. 
*/
@Controller
public class BoardController {
	
	// === #35. 의존객체 주입하기(DI: Dependency Injection) ===
	// ※ 의존객체주입(DI : Dependency Injection) 
	//  ==> 스프링 프레임워크는 객체를 관리해주는 컨테이너를 제공해주고 있다.
	//      스프링 컨테이너는 bean으로 등록되어진 BoardController 클래스 객체가 사용되어질때, 
	//      BoardController 클래스의 인스턴스 객체변수(의존객체)인 BoardService service 에 
	//      자동적으로 bean 으로 등록되어 생성되어진 BoardService service 객체를  
	//      BoardController 클래스의 인스턴스 변수 객체로 사용되어지게끔 넣어주는 것을 의존객체주입(DI : Dependency Injection)이라고 부른다. 
	//      이것이 바로 IoC(Inversion of Control == 제어의 역전) 인 것이다.
	//      즉, 개발자가 인스턴스 변수 객체를 필요에 의해 생성해주던 것에서 탈피하여 스프링은 컨테이너에 객체를 담아 두고, 
	//      필요할 때에 컨테이너로부터 객체를 가져와 사용할 수 있도록 하고 있다. 
	//      스프링은 객체의 생성 및 생명주기를 관리할 수 있는 기능을 제공하고 있으므로, 더이상 개발자에 의해 객체를 생성 및 소멸하도록 하지 않고
	//      객체 생성 및 관리를 스프링 프레임워크가 가지고 있는 객체 관리기능을 사용하므로 Inversion of Control == 제어의 역전 이라고 부른다.  
	//      그래서 스프링 컨테이너를 IoC 컨테이너라고도 부른다.
	
	//  IOC(Inversion of Control) 란 ?
	//  ==> 스프링 프레임워크는 사용하고자 하는 객체를 빈형태로 이미 만들어 두고서 컨테이너(Container)에 넣어둔후
	//      필요한 객체사용시 컨테이너(Container)에서 꺼내어 사용하도록 되어있다.
	//      이와 같이 객체 생성 및 소멸에 대한 제어권을 개발자가 하는것이 아니라 스프링 Container 가 하게됨으로써 
	//      객체에 대한 제어역할이 개발자에게서 스프링 Container로 넘어가게 됨을 뜻하는 의미가 제어의 역전 
	//      즉, IOC(Inversion of Control) 이라고 부른다.
	
	
	//  === 느슨한 결합 ===
	//      스프링 컨테이너가 BoardController 클래스 객체에서 BoardService 클래스 객체를 사용할 수 있도록 
	//      만들어주는 것을 "느슨한 결합" 이라고 부른다.
	//      느스한 결합은 BoardController 객체가 메모리에서 삭제되더라도 BoardService service 객체는 메모리에서 동시에 삭제되는 것이 아니라 남아 있다.
	
	// ===> 단단한 결합(개발자가 인스턴스 변수 객체를 필요에 의해서 생성해주던 것)
	// private InterBoardService service = new BoardService(); 
	// ===> BoardController 객체가 메모리에서 삭제 되어지면  BoardService service 객체는 멤버변수(필드)이므로 메모리에서 자동적으로 삭제되어진다.
	
	@Autowired
	private InterBoardService service;
	
	@RequestMapping(value="/test_insert.action")
	public String test_insert(HttpServletRequest request) {
		
		int n = service.test_insert();
		
		String message = "";
		
		if(n > 0) {
			message = "데이터 입력 성공!!";
		}
		else {
			message = "데이터 입력 실패!!";
		}
		
		request.setAttribute("message", message);
		request.setAttribute("n", n);
		
		return "test_insert";
		// 		/WEB-INF/views/test_insert.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value="/test_select.action")
	public String test_select(HttpServletRequest request) {
		
		HashMap<String, List<TestVO>> map = service.test_select();
		
		request.setAttribute("testvoList", map.get("testvoList"));
		request.setAttribute("testvoList2", map.get("testvoList2"));
		
		return "test/test_select";
		// 		/WEB-INF/views/test/test_select.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value="/test/test_form.action", method= {RequestMethod.GET})
	public String test_form() {
		
		return "test/testForm";
		//	  /WEB-INF/views/test/testForm.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value="/test/test_formEnd.action", method= {RequestMethod.POST})
	public String test_formEnd(HttpServletRequest request) {
		
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		
		HashMap<String, String> paraMap = new HashMap<>();
		paraMap.put("no", no);
		paraMap.put("name", name);
		
	
		int n = service.test_insert(paraMap);
		
		/*
		String message = "";
		
		if(n > 0) {
			message = "데이터 입력 성공!!";
		}
		else {
			message = "데이터 입력 실패!!";
		}
		
		request.setAttribute("message", message);
		request.setAttribute("n", n);
		
		return "test_insert";
		*/
		
		if(n > 0) {
			return "redirect:/test_select.action";
			// /test_select.action 페이지로 redirect(페이지이동) 해라
		}
		else {
			return "redirect:/test/test_form.action";
		}
	}
	
	
	@RequestMapping(value="/ajaxtest/ajaxtest_form.action", method= {RequestMethod.GET})
	public String ajaxtest_form(HttpServletRequest request) {
		
		return "test/ajaxtestForm";
		//	  /WEB-INF/views/ajaxtest/ajaxtestForm.jsp 페이지를 만들어야 한다.
	}
	
	
/*
	@RequestMapping(value="/ajaxtest/insert.action", method={RequestMethod.POST})
	public String ajaxtestInsert(HttpServletRequest request) {
		
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		
		HashMap<String, String> paraMap = new HashMap<>();
		paraMap.put("no", no);
		paraMap.put("name", name);
		
		int n = service.ajaxtest_insert(paraMap);
		
		// JSON 형식으로
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);

		String json = jsonObj.toString();
		
		request.setAttribute("json", json);
		
		return "jsonview";
		// 		/WEB-INF/views/jsonview.jsp 파일을 생성해야 한다.
	}
*/
	
	
	@ResponseBody
	@RequestMapping(value="/ajaxtest/insert.action", method={RequestMethod.POST}, produces="text/plain;charset=UTF-8")
	public String ajaxtestInsert(HttpServletRequest request) {
		
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		
		HashMap<String, String> paraMap = new HashMap<>();
		paraMap.put("no", no);
		paraMap.put("name", name);
		
		int n = service.ajaxtest_insert(paraMap);
		
		// JSON 형식으로
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);
		
		return jsonObj.toString();
		// 		/WEB-INF/views/jsonview.jsp 파일을 생성해야 한다.
	}
	
	
/*	
	@RequestMapping(value="/ajaxtest/select.action")
	public String ajaxtestSelect(HttpServletRequest request) {
		
		List<TestVO> testvoList  = service.ajaxtest_select();
		
		JSONArray jsonArr = new JSONArray();
		
		for(TestVO vo : testvoList) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("no", vo.getNo());
			jsonObj.put("name", vo.getName());
			jsonObj.put("writeday", vo.getWriteday());
			
			jsonArr.put(jsonObj);
		}
		
		String json = jsonArr.toString();
		
		request.setAttribute("json", json);
		
		return "jsonview";
	}
*/	

/*
    @ResponseBody 란?
     메소드에 @ResponseBody Annotation이 되어 있으면 return 되는 값은 View 단 페이지를 통해서 출력되는 것이 아니라 
    return 되어지는 값 그 자체를 웹브라우저에 바로 직접 쓰여지게 하는 것이다. 일반적으로 JSON 값을 Return 할때 많이 사용된다.  
   
   >>> 스프링에서 json 또는 gson을 사용한 ajax 구현시 데이터를 화면에 출력해 줄때 한글로 된 데이터가 '?'로 출력되어 한글이 깨지는 현상이 있다. 
               이것을 해결하는 방법은 @RequestMapping 어노테이션의 속성 중 produces="text/plain;charset=UTF-8" 를 사용하면 
               응답 페이지에 대한 UTF-8 인코딩이 가능하여 한글 깨짐을 방지 할 수 있다. <<< 
*/
	@ResponseBody
	@RequestMapping(value="/ajaxtest/select.action", produces="text/plain;charset=UTF-8")
	public String ajaxtestSelect() {
		
		List<TestVO> testvoList  = service.ajaxtest_select();
		
		JSONArray jsonArr = new JSONArray();
		
		for(TestVO vo : testvoList) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("no", vo.getNo());
			jsonObj.put("name", vo.getName());
			jsonObj.put("writeday", vo.getWriteday());
			
			jsonArr.put(jsonObj);
		}
		
		return jsonArr.toString();
	}
	
	
	// == 데이터테이블즈(datatables) -- datatables 1.10.19 기반으로 작성  == // 
	@RequestMapping(value="/datatables_test.action")
	public String datatables_test(HttpServletRequest request) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		request.setAttribute("testvoList", testvoList);
				
		return "test/datatables_test";
        //	   /WEB-INF/views/test/datatables_test.jsp 페이지를 만들어야 한다.
	}
	
	
	// === return 타입을 String 대신에 ModelAndView 를 사용해보겠다. 
	@RequestMapping(value="/datatables_test2.action")
	public ModelAndView datatables_test2(ModelAndView mav) {
		
		List<TestVO> testvoList = service.datatables_test();

		mav.addObject("testvoList", testvoList); // view 단으로 보낼 데이터
		mav.setViewName("test/datatables_test2"); // view 단의 파일 이름 지정하기
		
		return mav;
        //	   /WEB-INF/views/test/datatables_test.jsp 페이지를 만들어야 한다.
	}
	
	
	// ############### Quiz HR 페이징 처리해서 멤버 구하기 ######################
	@RequestMapping(value="test/empList.action")
	public ModelAndView empList(ModelAndView mav) {
		
		List<HashMap<String, String>> empList = service.test_employees();

		mav.addObject("empList", empList); // view 단으로 보낼 데이터
		mav.setViewName("test/empList.tiles1"); // view 단의 파일 이름 지정하기
		
		return mav;
        //	   /WEB-INF/views/test/datatables_hr.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value="/tiles1/datatables_test.action")
	public String datatables_test_tiles1(HttpServletRequest request) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		request.setAttribute("testvoList", testvoList);
				
		return "test/datatables_test.tiles1";
        //	   /WEB-INF/views/tiles1/test/datatables_test.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value="/tiles1/datatables_test2.action")
	public ModelAndView datatables_test_tiles1(ModelAndView mav) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		mav.addObject("testvoList", testvoList);
		mav.setViewName("test/datatables_test2.tiles1");
				
		return mav;
	}
	
	
	@RequestMapping(value="/tiles2/datatables_test.action")
	public String datatables_test_tiles2(HttpServletRequest request) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		request.setAttribute("testvoList", testvoList);
				
		return "test/datatables_test.tiles2";
        //	   /WEB-INF/views/tiles2/test/datatables_test.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value="/tiles2/datatables_test2.action")
	public ModelAndView datatables_test_tiles2(ModelAndView mav) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		mav.addObject("testvoList", testvoList);
		mav.setViewName("test/datatables_test2.tiles2");
				
		return mav;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// === #36. 메인 페이지 요청 === //
	@RequestMapping(value="/index.action")
	public ModelAndView index(ModelAndView mav) {
		
		List<String> imgfilenameList = service.getImgfilenameList();
		
		mav.addObject("imgfilenameList", imgfilenameList);
		mav.setViewName("main/index.tiles1");
		//				/WEB-INF/views/tiles1/main/index.jsp 파일을 생성한다.
		
		return mav;
		
	}
	
	// === #40. 로그인 폼 페이지 요청 === //
	@RequestMapping(value="/login.action")
	public ModelAndView login(ModelAndView mav) {
		
		mav.setViewName("login/loginform.tiles1");
		//		/WEB-INF/views/tiles1/login/loginform.jsp 파일을 생성한다.
		
		return mav;
	}
	
	// === #41. 로그인 처리하기 === //
	@RequestMapping(value="/loginEnd.action", method= {RequestMethod.POST})
	public ModelAndView loginEnd(HttpServletRequest request, ModelAndView mav) {
		
		String userid = request.getParameter("userid");
		String pwd = request.getParameter("pwd");
		
		HashMap<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("pwd", Sha256.encrypt(pwd));
		
		MemberVO loginuser = service.getLoginMember(paraMap);
		
		HttpSession session = request.getSession();
		
		if(loginuser == null) {
			String msg = "아이디 또는 암호가 틀립니다.";
			String loc = "javascript:history.back()";
			
			mav.addObject("msg", msg);
			mav.addObject("loc", loc);

			mav.setViewName("msg");
			//	/WEB-INF/views/msg.jsp 파일을 생성한다.
		}
		
		else {
			if(loginuser.isIdleStatus()) {
				// 로그인을 한지 1년이 지나서 휴면상태에 빠진 경우
				String msg = "로그인을 한지 1년이 지나서 휴면계정입니다. <br/>관리자에게 문의해주세요.";
				String loc = "javascript:history.back()";
				
				mav.addObject("msg", msg);
				mav.addObject("loc", loc);

				mav.setViewName("msg");
			}
			else {
				if(loginuser.isRequirePwdChange()) {
					// 암호를 최근 3개월 동안 변경하지 않은 경우
					session.setAttribute("loginuser", loginuser);
					
					String msg = "암호를 최근 3개월 동안 변경하지 않으셨습니다. 암호변경을 해주세요.";
					String loc = request.getContextPath()+"/myinfo.action";
								//	  /board/myinfo.action
					
					mav.addObject("msg", msg);
					mav.addObject("loc", loc);

					mav.setViewName("msg");

				}
				else {
					// 아무런 이상없이 로그인 하는 경우
					session.setAttribute("loginuser", loginuser);
					
					mav.setViewName("login/loginEnd.tiles1");
					// /WEB-INF/views/tiles1/login/loginEnd.jsp 파일을 생성한다.
				}
			}
			
		}
		
		return mav;
	}
	
	// == 나의 정보 수정 페이지(간략하게 성명만 조회가 되도록 하는 것으로 하겠습니다.) == //
	@RequestMapping(value="/myinfo.action")
	public String myinfo() {
		return "login/myinfo.tiles1";
		//		/WEB-INF/views/tiles1/login/myinfo.jsp 파일을 생성한다.
	}
	
	// === #50. 로그아웃 처리하기 === //
	@RequestMapping(value="/logout.action")
	public ModelAndView logout(HttpServletRequest request, ModelAndView mav) {
		
		HttpSession session = request.getSession();
		session.invalidate();
		
		String msg = "로그아웃 되었습니다.";
		String loc = request.getContextPath()+"/index.action";
		
		mav.addObject("msg", msg);
		mav.addObject("loc", loc);

		mav.setViewName("msg");
		
		return mav;
	}
		
	// === #51. 게시판 글쓰기 폼페이지 요청 === //
	@RequestMapping(value="/add.action")
	public ModelAndView add(ModelAndView mav) {
		
		mav.setViewName("board/add.tiles1");
		//		/WEB-INF/views/tiles1/board/add.jsp 파일을 생성한다.
		return mav;
	}
	
	// === #54. 게시판 글쓰기 완료 요청 === //
	@RequestMapping(value="/addEnd.action", method= {RequestMethod.POST})
	public String addEnd(BoardVO boardvo) {
		// form 태그의 name 명과  BoardVO 의  필드명이 같다라면
		// request.getParameter("form 태그의 name명"); 을 사용하지 않더라도
		// 자동적으로 BoardVO boardvo 에 set 되어진다.

		/*
 		== 확인용 ==
		System.out.println("1. " + boardvo.getFk_userid());
		System.out.println("2. " + boardvo.getName());
		System.out.println("3. " + boardvo.getSubject());
		System.out.println("4. " + boardvo.getContent());
		System.out.println("5. " + boardvo.getPw());
		*/
		
		int n = service.add(boardvo);
		
		if(n == 1) {
			return "redirect:/list.action";
			//		/list.action 페이지로 redirect(페이지이동)하라는 말이다.
		}
		else {
			return "redirect:/add.action";
			//		/add.action 페이지로 redirect(페이지이동)하라는 말이다. 실패시에!!
		}
	}
	
	
	// === #58. 글목록 보기 페이지 요청 === //
	@RequestMapping(value="/list.action")
	public ModelAndView list(ModelAndView mav) {
		
		List<BoardVO> boardList = null;
		
		// == 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 == //
		boardList = service.boardListNoSearch();
		
		mav.addObject("boardList", boardList);
		mav.setViewName("board/list.tiles1");
		//		/WEB-INF/views/tiles1/board/list.jsp 파일을 생성한다.
		
		return mav;
	}

}






