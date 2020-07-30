package com.spring.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.spring.board.model.BoardVO;
import com.spring.board.model.CommentVO;
import com.spring.common.FileManager;
import com.spring.common.MyUtil;
import com.spring.common.Sha256;
import com.spring.member.model.MemberVO;
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

// === #30. 컨트롤러 선언 === 
@Component
/* XML에서 빈을 만드는 대신에 클래스명 앞에 @Component 어노테이션을 적어주면 해당 클래스는 bean으로 자동 등록된다. 
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
	
	@Autowired // Type에 따라 알아서 Bean 을 주입해준다.
	private InterBoardService service;
	
	// ===== #150. 파일업로드 및 다운로드를 해주는 FileManager 클래스 의존객체 주입하기(DI : Dependency Injection) =====
	@Autowired // Type에 따라 알아서 Bean 을 주입해준다.
	private FileManager fileManager;
	
	
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
		//     /WEB-INF/views/test_insert.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value="/test_select.action")
	public String test_select(HttpServletRequest request) {
		
		HashMap<String, List<TestVO>> map = service.test_select();
		
		request.setAttribute("testvoList", map.get("testvoList"));
		request.setAttribute("testvoList2", map.get("testvoList2"));
				
		return "test/test_select";
        //	   /WEB-INF/views/test/test_select.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value="/test/test_form.action", method= {RequestMethod.GET})
	public String test_form() {
		
		return "test/testForm";
        //	   /WEB-INF/views/test/testForm.jsp 페이지를 만들어야 한다.
	}
	
	@RequestMapping(value="/test/test_formEnd.action", method= {RequestMethod.POST})
	public String test_formEnd(HttpServletRequest request) {
		
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		
		HashMap<String,String> paraMap = new HashMap<>();
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
			//      /test_select.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
		else {
			return "redirect:/test/test_form.action";
		}
	}
	
	
	@RequestMapping(value="/ajaxtest/ajaxtest_form.action")
	public String ajaxtest_form() {
		
		return "test/ajaxtestForm";
        //	   /WEB-INF/views/test/ajaxtestForm.jsp 페이지를 만들어야 한다.
	}
	

/*	
	@RequestMapping(value="/ajaxtest/insert.action", method= {RequestMethod.POST})
	public String ajaxtestInsert(HttpServletRequest request) {
		
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		
		HashMap<String,String> paraMap = new HashMap<>();
		paraMap.put("no", no);
		paraMap.put("name", name);
		
		int n = service.ajaxtest_insert(paraMap);
		
		// 결과물인 n을 JSON 형식으로 만들어주어야 한다. 
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);
		
		String json = jsonObj.toString();
		
		request.setAttribute("json", json);
		
		return "jsonview";
		//     /WEB-INF/views/jsonview.jsp 파일을 생성해야 한다.
	}
*/
	
	@ResponseBody
	@RequestMapping(value="/ajaxtest/insert.action", method= {RequestMethod.POST}, produces="text/plain;charset=UTF-8")
	public String ajaxtestInsert(HttpServletRequest request) {
		
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		
		HashMap<String,String> paraMap = new HashMap<>();
		paraMap.put("no", no);
		paraMap.put("name", name);
		
		int n = service.ajaxtest_insert(paraMap);
		
		// 결과물인 n을 JSON 형식으로 만들어주어야 한다. 
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);
		
		return jsonObj.toString();
	}
	
/*	
	@RequestMapping(value="/ajaxtest/select.action")
	public String ajaxtest_select(HttpServletRequest request) {
		
		List<TestVO> testvoList = service.ajaxtest_select();
		
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
	public String ajaxtest_select() {
		
		List<TestVO> testvoList = service.ajaxtest_select();
		
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
	
	// === return 타입을 String 대신에 ModelAndView 를 사용해보겠다. === //
	@RequestMapping(value="/datatables_test2.action")
	public ModelAndView datatables_test(ModelAndView mav) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		mav.addObject("testvoList", testvoList);  // view단으로 보낼 데이터 
		mav.setViewName("test/datatables_test");  // view단의 파일이름 지정하기 
        //	/WEB-INF/views/test/datatables_test.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	
	
	@RequestMapping(value="/tiles1/datatables_test.action")
	public String datatables_test_tiles1(HttpServletRequest request) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		request.setAttribute("testvoList", testvoList);
				
		return "test/datatables_test.tiles1";
        //	   /WEB-INF/views/tiles1/test/datatables_test.jsp 페이지를 만들어야 한다.
	}
	
	
	// === return 타입을 String 대신에 ModelAndView 를 사용해보겠다. === //
	@RequestMapping(value="/tiles1/datatables_test2.action")
	public ModelAndView datatables_test_tiles1(ModelAndView mav) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		mav.addObject("testvoList", testvoList);
	    mav.setViewName("test/datatables_test.tiles1");
        //  /WEB-INF/views/tiles1/test/datatables_test.jsp 페이지를 만들어야 한다.
	    
		return mav;
	}
	
	
	@RequestMapping(value="/tiles2/datatables_test.action")
	public String datatables_test_tiles2(HttpServletRequest request) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		request.setAttribute("testvoList", testvoList);
				
		return "test/datatables_test.tiles2";
        //	   /WEB-INF/views/tiles2/test/datatables_test.jsp 페이지를 만들어야 한다.
	}
	
	
	// === return 타입을 String 대신에 ModelAndView 를 사용해보겠다. === //
	@RequestMapping(value="/tiles2/datatables_test2.action")
	public ModelAndView datatables_test_tiles2(ModelAndView mav) {
		
		List<TestVO> testvoList = service.datatables_test();
		
		mav.addObject("testvoList", testvoList);
		mav.setViewName("test/datatables_test.tiles2");
        //	/WEB-INF/views/tiles2/test/datatables_test.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	
	
	@RequestMapping(value="/test/employees.action")
	public ModelAndView test_employees(ModelAndView mav) {
		
		List<HashMap<String,String>> empList = service.test_employees();
		
		mav.addObject("empList", empList);
		mav.setViewName("test/empList.tiles1");
        //	/WEB-INF/views/tiles1/test/empList.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	
	///////////////////////////////////////////////////////////////
	
	// === #36. 메인 페이지 요청 === // 
	@RequestMapping(value="/index.action")
	public ModelAndView index(ModelAndView mav) {
		
		List<String> imgfilenameList = service.getImgfilenameList(); 
		
		mav.addObject("imgfilenameList", imgfilenameList);
		mav.setViewName("main/index.tiles1");
		//   /WEB-INF/views/tiles1/main/index.jsp 파일을 생성한다.
		
		return mav;
	}
	
	
	// === #40. 로그인 폼 페이지 요청 === //
	@RequestMapping(value="/login.action")
	public ModelAndView login(ModelAndView mav) {
		
		mav.setViewName("login/loginform.tiles1");
	    //   /WEB-INF/views/tiles1/login/loginform.jsp 파일을 생성한다.
		
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
			//  /WEB-INF/views/msg.jsp 파일을 생성한다.
		}
		
		else {
			if(loginuser.isIdleStatus() == true) {
				// 로그인을 한지 1년이 지나서 휴면상태에 빠진 경우
				
				String msg = "로그인을 한지 1년이 지나서 휴면상태에 빠졌습니다. 관리자에게 문의 바랍니다.";
				String loc = "javascript:history.back()";
				
				mav.addObject("msg", msg);
				mav.addObject("loc", loc);
				
				mav.setViewName("msg");
			}
			
			else {
			    if(loginuser.isRequirePwdChange() == true) {
			    	// 암호를 최근 3개월 동안 변경하지 않은 경우 
			    	session.setAttribute("loginuser", loginuser);
			    	
			    	String msg = "암호를 최근 3개월 동안 변경하지 않으셨습니다. 암호변경을 위해 나의정보 페이지로 이동합니다.";
					String loc = request.getContextPath()+"/myinfo.action";
					            //    /board/myinfo.action
					
					mav.addObject("msg", msg);
					mav.addObject("loc", loc);
					
					mav.setViewName("msg");
			    }
			    
			    else {
			    	// 아무런 이상없이 로그인 하는 경우 
			    	session.setAttribute("loginuser", loginuser);
			    	
			    	if(session.getAttribute("gobackURL") != null) {
			    		// 세션에 저장된 돌아갈 페이지 주소(gobackURL)가 있다라면 
			    		
			    		String gobackURL = (String) session.getAttribute("gobackURL");
			    		mav.addObject("gobackURL", gobackURL); // request 영역에 저장시키는 것이다.
			    		
			    		session.removeAttribute("gobackURL");  // 중요!!!!
			    	}
			    	
			    	mav.setViewName("login/loginEnd.tiles1");
			    	//   /WEB-INF/views/tiles1/login/loginEnd.jsp 파일을 생성한다.
			    }
			}
			
		}
		
		
		return mav;
	}
	
	
	// == 나의정보 수정 페이지(간략하게 성명만 조회가 되도록 하는 것으로 하겠습니다) == //
	@RequestMapping(value="/myinfo.action")
	public String myinfo() {
		return "login/myinfo.tiles1";
	    //   /WEB-INF/views/tiles1/login/myinfo.jsp 파일을 생성한다.
	}
	
	
	// === #50. 로그아웃 처리하기 ===
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
	
	
	// === #51. 게시판 글쓰기 폼페이지 요청 === 
	@RequestMapping(value="/add.action")
	public ModelAndView requiredLogin_add(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// === #138. 답변글쓰기가 추가된 경우 === //
		String groupno = request.getParameter("groupno");
		String fk_seq = request.getParameter("fk_seq");
		String depthno = request.getParameter("depthno");
		
		mav.addObject("groupno", groupno);
		mav.addObject("fk_seq", fk_seq);
		mav.addObject("depthno", depthno);
		//////////////////////////////////////////////////
		
		mav.setViewName("board/add.tiles1");
	    //   /WEB-INF/views/tiles1/board/add.jsp 파일을 생성한다.
		
		return mav;
	}
	
	
	
	// === #54. 게시판 글쓰기 완료 요청 ==
	@RequestMapping(value="/addEnd.action", method= {RequestMethod.POST})
//	public String pointPlus_addEnd(HashMap<String, String> paraMap, BoardVO boardvo) {
	    // form 태그의 name 명과  BoardVO 의  필드명이 같다라면
		// request.getParameter("form 태그의 name명"); 을 사용하지 않더라도
		// 자동적으로 BoardVO boardvo 에 set 되어진다.
		
/*
	=== #147. 파일첨부가 된 글쓰기 이므로 
			   먼저 위의 public String pointPlus_addEnd(HashMap<String, String> paraMap, BoardVO boardvo) { 을 주석처리한 이후에 아래와 같이 해야한다.
			  MultipartHttpServletRequest mrequest 를 사용하기 위해서는
			  \Board\src\main\webapp\WEB-INF\spring\appServlet\servlet-context.xml 파일에서 #20. 의
			  multipartResolver를 bean으로 등록해주어야 한다.!!!!
*/
	
	public String pointPlus_addEnd(HashMap<String, String> paraMap, BoardVO boardvo, MultipartHttpServletRequest mrequest) {
		/*
			웹페이지에 요청form이 enctype="multipart/form-data" 으로 되어있어서 Multipart 요청(파일처리 요청)이 들어올때 
	      	컨트롤러에서는 HttpServletRequest 대신 MultipartHttpServletRequest 인터페이스를 사용해야 한다.
	   		MultipartHttpServletRequest 인터페이스는 HttpServletRequest 인터페이스와 MultipartRequest 인터페이스를 상속받고있다.
	      	즉, 웹 요청 정보를 얻기 위한 getParameter()와 같은 메소드와 Multipart(파일처리) 관련 메소드를 모두 사용가능하다.
		*/
		
		// === 사용자가 쓴 글에 파일이 첨부되어있는 것인지 아니면 파일첨부가 안된것인지 구분을 지어주어야 한다.

		// === !!! 첨부파일이 있는지 없는지 알아오기 시작 !!! ===
		MultipartFile attach = boardvo.getAttach();
		if( !attach.isEmpty() ) {
			// attach(첨부파일)가 비어있지 않다면(즉, 첨부파일이 있는 경우라면
			/*
				1. 사용자가 보낸 파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
				>>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
					우리는 WAS의 webapp/resources/files 라는 폴더로 지정해준다.
			*/
			// WAS의 webapp 의 절대경로를 알아와야 한다.
			HttpSession session = mrequest.getSession();
			String root = session.getServletContext().getRealPath("/");
			String path = root + "resources" + File.separator + "files";
			/* File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
			     운영체제가 Windows 이라면 File.separator 는 "\" 이고,
			     운영체제가 UNIX, Linux 이라면 File.separator 는 "/" 이다.
			*/
			// path 가 첨부파일을 저장할 WAS(톰캣)의 폴더가 된다.
			// System.out.println("BoardController path => " + path);
			// BoardController path => C:\springworkspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\files
			
			/*
				2. 파일첨부를 위한 변ㅅ의 설정 및 값을 초기화 한 후 파일 올리기
			*/
			String newFileName = "";
			// WAS(톰캣)의 디스크에 저장될 파일명 
			
			byte[] bytes = null;
			// 첨부파일을 WAS(톰캣)의 디스크에 저장할때 사용되는 용도.
			
			long fileSize = 0;
			// 파일크기를 읽어오기 위한 용도
			
			try {
				bytes = attach.getBytes();
				// getBytes() 메소드는 첨부된 파일(attach)을 바이트단위로 파일을 다 읽어오는 것이다. 
				// 예를 들어, 첨부한 파일이 "강아지.png" 이라면
				// 이파일을 WAS(톰캣) 디스크에 저장시키기 위해 byte[] 타입으로 변경해서 올린다.
				
				newFileName = fileManager.doFileUpload(bytes, attach.getOriginalFilename(), path);
				// 위의 것이 파일 올리기를 해주는 것이다.
				// attach.getOriginalFilename() 은 첨부된 파일의 파일명(강아지.png)이다.
				
				// System.out.println("BoardController newFileName => " + newFileName);
				// BoardController newFileName => 202007271209093467247615434800.jpg
				// BoardController newFileName => 202007271210073467305353517600.jpg
				
				/*
					3. BoardVO boardvo 에 fileName 값과 orgFilename 값과 fileSize 값을 넣어주기
				*/
				boardvo.setFileName(newFileName);
				// WAS(톰캣)에 저장될 파일명(20190725092715353243254235235234.png)
				
				boardvo.setOrgFilename(attach.getOriginalFilename());
				// 게시판 페이지에서 첨부된 파일명(강아지.png)을 보여줄때 및 
				// 사용자가 파일을 다운로드 할때 사용된어지는 파일명
				
				fileSize = attach.getSize();
				boardvo.setFileSize(String.valueOf(fileSize));
				// 게시판 페이지에서 첨부한 파일의 크기를 보여줄때 사용하는 것으로써  String 타입으로 변경해서 저장한다.
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		// === !!! 첨부파일이 있는지 없는지 알아오기 끝 !!! ===
		
		int n = 0;
		
		if( attach.isEmpty() ) {
			// 첨부파일이 없는 경우라면
			n = service.add(boardvo);
		}
		else {
			// 첨부파일이 있는 경우라면
			n = service.add_withFile(boardvo);
		}
		
		paraMap.put("userid", boardvo.getFk_userid());
		// === after Advice용 (글을 작성하면 포인트 100 을 주기위해서 글쓴이가 누구인지 알아온다.) === 
		
		if(n==1) {
			paraMap.put("pointPlus", "100");
			// === after Advice용 (글을 작성하면 포인트 100 을 준다.) === 
			
			return "redirect:/list.action";	
			//      /list.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
		else {
			paraMap.put("pointPlus", "0");
			// === after Advice용 (글을 작성이 실패되면 포인트 0 을 준다.) === 
			
			return "redirect:/add.action";
            //	   /add.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
	}
	
	
	// === #58. 글목록 보기 페이지 요청 === 
	@RequestMapping(value="/list.action")
	public ModelAndView list(HttpServletRequest request, ModelAndView mav) {
		
		List<BoardVO> boardList = null;
		
		// == 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 ==
	//	boardList = service.boardListNoSearch();
		
		// == #100. 페이징 처리를 안한 검색어가 있는 전체 글목록 보여주기 ==
	/*
		String searchType = request.getParameter("searchType");
		String searchWord = request.getParameter("searchWord");
		
		if(searchWord == null || searchWord.trim().isEmpty()) {
			searchWord = "";
		}
		
		HashMap<String,String> paraMap = new HashMap<String,String>();
		paraMap.put("searchType", searchType);
		paraMap.put("searchWord", searchWord);
		
		boardList = service.boardListSearch(paraMap);
		
		if(!"".equals(searchWord)) {
			mav.addObject("paraMap", paraMap);
		}
	*/
		
		// == #112. 페이징 처리를 한 검색어가 있는 전체 글목록 보여주기 ==
		// 페이징 처리를 통한 글목록 보여주기는 예를 들어 3페이지의 내용을 보고자 한다라면 
		// 검색을 할 경우는 아래와 같이
		// list.action?searchType=subject&searchWord=순신&currentShowPageNo=3 와 같이 해주어야 한다.
		// 또는 
		// 검색이 없는 전체를 볼때는 아래와 같이 
		// list.action?searchType=subject&searchWord=&currentShowPageNo=3 와 같이 해주어야 한다.
		
		String searchType = request.getParameter("searchType");
		String searchWord = request.getParameter("searchWord");
		String str_currentShowPageNo = request.getParameter("currentShowPageNo");
		
		if(searchWord == null || searchWord.trim().isEmpty()) {
			searchWord = "";
		}
		
		if(searchType == null) {
			searchType = "";
		}
		
		HashMap<String, String> paraMap = new HashMap<>();
		paraMap.put("searchType", searchType);
		paraMap.put("searchWord", searchWord);
		
		// 먼저 총 게시물 건수(totalCount)를 구해와야 한다.
		// 총 게시물 건수(totalCount)는 검색조건이 있을 때와 없을때로 나뉘어진다.
		int totalCount = 0;        // 총게시물 건수
		int sizePerPage = 10;      // 한 페이지당 보여줄 게시물 건수
		int currentShowPageNo = 0; // 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정함.
		int totalPage = 0;         // 총 페이지 수(웹브라우저상에 보여줄 총 페이지 개수, 페이지바) 
		
		int startRno = 0;          // 시작 행번호
		int endRno = 0;            // 끝 행번호
		
		// 총 게시물 건수(totalCount)
		totalCount = service.getTotalCount(paraMap);
     //	System.out.println("~~~~~ 확인용 totalCount : " + totalCount); 
		
		// 만약에 총 게시물 건수(totalCount)가 127개 이라면
		// 총 페이지 수(totalPage)는 13개가 되어야 한다.
		totalPage = (int) Math.ceil( (double)totalCount/sizePerPage );  // (double)127/10 ==> 12.7 ==> Math.ceil(12.7) ==> (int)13.0  ==> 13
		                                                                // (double)120/10 ==> 12.0 ==> Math.ceil(12.0) ==> (int)12.0  ==> 12
		
		if(str_currentShowPageNo == null) {
			// 게시판에 보여지는 초기화면
			
			currentShowPageNo = 1;
			// 즉, 초기화면인 /list.action 은  /list.action?currentShowPageNo=1 로 하겠다는 말이다. 
		}
		else {
			try {
				currentShowPageNo = Integer.parseInt(str_currentShowPageNo); 
				if(currentShowPageNo < 1 || currentShowPageNo > totalPage) {
					currentShowPageNo = 1;
				}
			} catch(NumberFormatException e) {
				currentShowPageNo = 1;
			}
		}
		
		// **** 가져올 게시글의 범위를 구한다.(공식임!!!) **** 
		/*
		     currentShowPageNo      startRno     endRno
		    --------------------------------------------
		         1 page        ===>    1           10
		         2 page        ===>    11          20
		         3 page        ===>    21          30
		         4 page        ===>    31          40
		         ......                ...         ...
		 */
	
		startRno = ((currentShowPageNo - 1 ) * sizePerPage) + 1;
		endRno = startRno + sizePerPage - 1; 
		
		paraMap.put("startRno", String.valueOf(startRno));
		paraMap.put("endRno", String.valueOf(endRno));
		
		boardList = service.boardListSearchWithPaging(paraMap);
		// 페이징 처리한 글목록 가져오기(검색이 있든지, 검색이 없든지 모두 다 포함한것) 
		
		if(!"".equals(searchWord)) {
			mav.addObject("paraMap", paraMap);
		}
		
		
		// === #119. 페이지바 만들기 === //
		String pageBar = "<ul style='list-style: none;'>";
		
		int blockSize = 10;
		// blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수 이다.
		/*
		      1 2 3 4 5 6 7 8 9 10  다음                   -- 1개블럭
		   이전  11 12 13 14 15 16 17 18 19 20  다음   -- 1개블럭
		   이전  21 22 23
		*/
		
		int loop = 1;
		/*
		    loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		*/
		
		int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;
		// *** !! 공식이다. !! *** //
		
	/*
	    1  2  3  4  5  6  7  8  9  10  -- 첫번째 블럭의 페이지번호 시작값(pageNo)은 1 이다.
	    11 12 13 14 15 16 17 18 19 20  -- 두번째 블럭의 페이지번호 시작값(pageNo)은 11 이다.
	    21 22 23 24 25 26 27 28 29 30  -- 세번째 블럭의 페이지번호 시작값(pageNo)은 21 이다.
	    
	    currentShowPageNo         pageNo
	   ----------------------------------
	         1                      1 = ((1 - 1)/10) * 10 + 1
	         2                      1 = ((2 - 1)/10) * 10 + 1
	         3                      1 = ((3 - 1)/10) * 10 + 1
	         4                      1
	         5                      1
	         6                      1
	         7                      1 
	         8                      1
	         9                      1
	         10                     1 = ((10 - 1)/10) * 10 + 1
	        
	         11                    11 = ((11 - 1)/10) * 10 + 1
	         12                    11 = ((12 - 1)/10) * 10 + 1
	         13                    11 = ((13 - 1)/10) * 10 + 1
	         14                    11
	         15                    11
	         16                    11
	         17                    11
	         18                    11 
	         19                    11 
	         20                    11 = ((20 - 1)/10) * 10 + 1
	         
	         21                    21 = ((21 - 1)/10) * 10 + 1
	         22                    21 = ((22 - 1)/10) * 10 + 1
	         23                    21 = ((23 - 1)/10) * 10 + 1
	         ..                    ..
	         29                    21
	         30                    21 = ((30 - 1)/10) * 10 + 1
	*/
		
		String url = "list.action";
		
		// === [이전] 만들기 === 
		if(pageNo != 1) {
			pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'>[이전]</a></li>";
		}
		
		while( !(loop > blockSize || pageNo > totalPage) ) {
			
			if(pageNo == currentShowPageNo) {
				pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
			}
			else {
				pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>";
			}
			
			loop++;
			pageNo++;
			
		}// end of while------------------------------
		
		
		// === [다음] 만들기 ===
		if( !(pageNo > totalPage) ) {
			pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>[다음]</a></li>";
		}
		
		pageBar += "</ul>";
		
		mav.addObject("pageBar", pageBar);
		
		////////////////////////////////////////////////////
		// === #121. 
		// 페이징 처리되어진 후 특정글제목을 클릭하여 상세내용을 본 이후
		// 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해
		// 현재 페이지 주소를 뷰단으로 넘겨준다.
		String gobackURL = MyUtil.getCurrentURL(request);
		
		// System.out.println("BoardController gobackURL : " + gobackURL);
		mav.addObject("gobackURL", gobackURL);
		
		//////////////////////////////////////////////////////
		// === #69. 글조회수(readCount)증가 (DML문 update)는
		//          반드시 목록보기에 와서 해당 글제목을 클릭했을 경우에만 증가되고,
		//          웹브라우저에서 새로고침(F5)을 했을 경우에는 증가가 되지 않도록 해야 한다.
		//          이것을 하기 위해서는 session 을 사용하여 처리하면 된다.

		HttpSession session = request.getSession();
		session.setAttribute("readCountPermission", "yes");
		/*
		   session 에  "readCountPermission" 키값으로 저장된 value값은 "yes" 이다.
		   session 에  "readCountPermission" 키값에 해당하는 value값 "yes"를 얻으려면 
		      반드시 웹브라우저에서 주소창에 "/list.action" 이라고 입력해야만 얻어올 수 있다. 
		*/
		//////////////////////////////////////////////////////
		
		mav.addObject("boardList",boardList);
		mav.setViewName("board/list.tiles1");
		
		return mav;
	}
	
	
	// === #62. 글1개를 보여주는 페이지 요청 ==
	@RequestMapping(value="/view.action")
	public ModelAndView view(HttpServletRequest request, ModelAndView mav) {
		
		// 조회하고자 하는 글번호 받아오기 
		String seq = request.getParameter("seq");
		
		HttpSession session = request.getSession();
		MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");
		
		String userid = null;
		
		if(loginuser != null) {
			userid = loginuser.getUserid();
			// userid 는 로그인 되어진 사용자의 userid 이다.
		}
		
		// === #68. !!! 중요 !!! 
        //     글1개를 보여주는 페이지 요청은 select 와 함께 
		//     DML문(지금은 글조회수 증가인 update문)이 포함되어져 있다.
		//     이럴경우 웹브라우저에서 페이지 새로고침(F5)을 했을때 DML문이 실행되어
		//     매번 글조회수 증가가 발생한다.
		//     그래서 우리는 웹브라우저에서 페이지 새로고침(F5)을 했을때는
		//     단순히 select만 해주고 DML문(지금은 글조회수 증가인 update문)은 
		//     실행하지 않도록 해주어야 한다. !!! === //
		
		BoardVO boardvo = null;
		
		// 위의 글목록보기 #69. 에서 session.setAttribute("readCountPermission", "yes"); 해두었다.
        if( "yes".equals(session.getAttribute("readCountPermission")) ) {
        	// 글목록보기를 클릭한 다음에 특정글을 조회해온 경우이다.
        	
        	boardvo = service.getView(seq, userid);
    		// 글조회수 증가와 함께 글1개를 조회를 해주는 것 
        	
        	session.removeAttribute("readCountPermission");
        	// 중요함!! session 에 저장된 readCountPermission 을 삭제한다.
        }
        else {
        	// 웹브라우저에서 새로고침(F5)을 클릭한 경우이다.
        	
        	boardvo = service.getViewWithNoAddCount(seq);
        	// 글조회수 증가는 없고 단순히 글1개 조회만을 해주는 것이다.
        }
        
        String gobackURL = request.getParameter("gobackURL");
        mav.addObject("gobackURL", gobackURL);

		mav.addObject("boardvo", boardvo);
		mav.setViewName("board/view.tiles1");
		
		return mav;
	}
	
	
	// === #71. 글수정 페이지 요청 ==
	@RequestMapping(value="/edit.action")
	public ModelAndView requiredLogin_edit(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 글 수정해야할 글번호 가져오기 
		String seq = request.getParameter("seq");
		
		// 글 수정해야할 글1개 내용 가져오기 
		BoardVO boardvo = service.getViewWithNoAddCount(seq); 
		// 글조회수(readCount) 증가 없이 그냥 글1개만 가져오는 것.
		
		HttpSession session = request.getSession();
		MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");
		
		if( !loginuser.getUserid().equals(boardvo.getFk_userid()) ) {
			String msg = "다른 사용자의 글은 수정이 불가합니다.";
			String loc = "javascript:history.back()";
			
			mav.addObject("msg", msg);
			mav.addObject("loc", loc);
			mav.setViewName("msg");
		}
		else {
			// 자신의 글을 수정할 경우
			// 가져온 1개글을 글수정할 폼이 있는 view 단으로 보내준다.
			mav.addObject("boardvo", boardvo);
			mav.setViewName("board/edit.tiles1");
		}
		
		return mav;
	}
	
	
	// === #72. 글수정 페이지 완료하기 ==
	@RequestMapping(value="/editEnd.action", method= {RequestMethod.POST})
	public ModelAndView editEnd(HttpServletRequest request, BoardVO boardvo, ModelAndView mav) {
		
		/*  글 수정을 하려면 원본글의 글암호와 수정시 입력해준 암호가 일치할때만 
		        글 수정이 가능하도록 해야한다. */
		int n = service.edit(boardvo);
		
		if(n == 0) {
			mav.addObject("msg", "암호가 일치하지 않아 글 수정이 불가합니다.");
		}
		else {
			mav.addObject("msg", "글수정 성공!!");
		}
		
		mav.addObject("loc", request.getContextPath()+"/view.action?seq="+boardvo.getSeq());
		mav.setViewName("msg");
		
		return mav;
	}
	
	
	// === #76. 글삭제 페이지 요청 ==
	@RequestMapping(value="/del.action")
	public ModelAndView requiredLogin_del(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 삭제해야할 글번호를 받아온다.
		String seq = request.getParameter("seq");
		
		// 삭제해야할 글1개 내용 가져와서 로그인한 사람이 쓴 글이라면 글삭제가 가능하지만 
		// 다른 사람이 쓴 글은 삭제가 불가하도록 해야 한다.
		BoardVO boardvo = service.getViewWithNoAddCount(seq);
		// 글조회수(readCount) 증가 없이 그냥 글1개만 가져오는 것
		
		HttpSession session = request.getSession();
		MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");
		
		if( !loginuser.getUserid().equals(boardvo.getFk_userid()) ) {
			String msg = "다른 사용자의 글은 삭제가 불가합니다.";
			String loc = "javascript:history.back()";
			
			mav.addObject("msg", msg);
			mav.addObject("loc", loc);
			mav.setViewName("msg");
		}
		else {
			// 자신의 글을 삭제할 경우
			// 글작성시 입력해준 암호와 일치하는지 여부를 알아오도록 암호를 입력받아주는 del.jsp 페이지를 띄우도록 한다. 
			mav.addObject("seq", seq);
			mav.setViewName("board/del.tiles1");
		}
		
		return mav;
	}
	
	
	// === #77. 글삭제 페이지 완료하기 ==
	@RequestMapping(value="/delEnd.action", method= {RequestMethod.POST})
	public ModelAndView delEnd(HttpServletRequest request, ModelAndView mav) 
		throws Throwable {
		
		/*  글 삭제를 하려면 원본글의 글암호와 삭제시 입력해준 암호가 일치할때만 
		        글 삭제가 가능하도록 해야한다. */
		String seq = request.getParameter("seq");
		String pw = request.getParameter("pw");
		
		HashMap<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("pw", pw);
		
		int n = service.del(paraMap);
		
		if(n == 0) {
			mav.addObject("msg", "암호가 일치하지 않아 글 삭제가 불가합니다.");
			mav.addObject("loc", request.getContextPath()+"/view.action?seq="+seq);
		}
		else {
			mav.addObject("msg", "글삭제 성공!!");
			mav.addObject("loc", request.getContextPath()+"/list.action"); 
		}
		
		mav.setViewName("msg");
		
		return mav;
	}
	
	
   // === #84. 댓글쓰기(Ajax 로 처리) ===
   @ResponseBody
   @RequestMapping(value="/addComment.action", method= {RequestMethod.POST})      
   public String pointPlus_addComment(HashMap<String, String> paraMap, CommentVO commentvo) {
	   
	   String jsonStr = "";
	   
	   try {
	   	    paraMap.put("userid", commentvo.getFk_userid());
	        // === after Advice용 (댓글을 작성하면 포인트 50 을 주기위해서 글쓴이가 누구인지 알아온다.) === 
	   
			int n = service.addComment(commentvo);
		    // 댓글쓰기(insert) 및 
	        // 원게시물(tblBoard 테이블)에 댓글의 갯수 증가(update 1씩 증가)하기  
	
	        if(n==1) {
		        paraMap.put("pointPlus", "50");
		        // === after Advice용 (댓글을 작성하면 포인트 50 을 준다.) === 
	        }
	        else {
		        paraMap.put("pointPlus", "0");
			    // === after Advice용 (댓글을 작성이 실패되면 포인트 0 을 준다.) === 
	        }
	   
	        JSONObject jsonObj = new JSONObject();
	        jsonObj.put("n", n);
	   
	        jsonStr = jsonObj.toString();
	   
	    } catch (Throwable e) {
			e.printStackTrace();
		}
	   
	    return jsonStr;
   }
   
   
   /*
   @ExceptionHandler 에 대해서.....
   ==> 어떤 컨트롤러내에서 발생하는 익셉션이 있을시 익셉션 처리를 해주려고 한다면
       @ExceptionHandler 어노테이션을 적용한 메소드를 구현해주면 된다
        
      컨트롤러내에서 @ExceptionHandler 어노테이션을 적용한 메소드가 존재하면, 
      스프링은 익셉션 발생시 @ExceptionHandler 어노테이션을 적용한 메소드가 처리해준다.
      따라서, 컨트롤러에 발생한 익셉션을 직접 처리하고 싶다면 @ExceptionHandler 어노테이션을 적용한 메소드를 구현해주면 된다.
   */
   /*
   @ExceptionHandler(java.sql.SQLSyntaxErrorException.class)
   public String handleSQLSyntaxErrorException(java.sql.SQLSyntaxErrorException e, HttpServletRequest request) {
	   
	   System.out.println("~~~~~~~~~ 오류코드 : " + e.getErrorCode());
	   // ~~~~~~~~~ 오류코드 : 904
	   
	   String msg = "SQL구문 오류가 발생했습니다.\n 오류코드번호 : "+e.getErrorCode();
	   String loc = "javascript:history.back()";
	   
	   request.setAttribute("msg", msg);
	   request.setAttribute("loc", loc);
	   
	   return "msg";
   }
   */
   
   
   // === #90. 원게시물에 딸린 댓글들을 조회해오기(Ajax 로 처리) ===
   @ResponseBody
   @RequestMapping(value="/readComment.action", produces="text/plain;charset=UTF-8")      
   public String readComment(HttpServletRequest request) {
	   
	   String parentSeq = request.getParameter("parentSeq"); 
	   
	   List<CommentVO> commentList = service.getCommentList(parentSeq);
	   
	   JSONArray jsonArr = new JSONArray();
	   
	   if(commentList != null) {
		   for(CommentVO cmtvo : commentList) {
		       JSONObject jsonObj = new JSONObject();
		       jsonObj.put("content", cmtvo.getContent());
		       jsonObj.put("name", cmtvo.getName());
	   		   jsonObj.put("regDate", cmtvo.getRegDate());
		    		
		       jsonArr.put(jsonObj);
		    }
	   }
	    
	   return jsonArr.toString();
   } 
   
   
   // === #106. 검색어 입력시 자동글 완성하기 3 ===
   @ResponseBody
   @RequestMapping(value="/wordSearchShow.action", produces="text/plain;charset=UTF-8")
   public String wordSearchShow(HttpServletRequest request) {
	   
	   String searchType = request.getParameter("searchType");
	   String searchWord = request.getParameter("searchWord");
	   
	   HashMap<String,String> paraMap = new HashMap<>();
	   paraMap.put("searchType", searchType);
	   paraMap.put("searchWord", searchWord);
	   
	   List<String> wordList = service.wordSearchShow(paraMap);
	   
	   JSONArray jsonArr = new JSONArray();
	   
	   if(wordList != null) {
		   for(String word : wordList) {
			   JSONObject jsonObj = new JSONObject();
			   jsonObj.put("word", word);
			   
			   jsonArr.put(jsonObj);
		   }
	   }
	   
	   return jsonArr.toString();
   }
   
   
   // == 스프링 스케줄러 연습하기 == //
   // 여기서는 스프링 스케줄러 연습이므로 alert 를 창 띄우는 것으로 끝내지만 
   // WAS에서 작업해야할 대량 메일발송 또는 대량 문자발송이라든지 
   // 또는 DB에 접속하여 DB와 관련된 업무처리를 하도록 서비스업무를 호출하도록 하면 된다.
   @RequestMapping(value="/alarmTest.action", method= {RequestMethod.GET}) 
   public ModelAndView alertTest(HttpServletRequest request, ModelAndView mav) {
	   	   
	   String msg = "즐거운 점심시간 입니다.~~~";
	   String loc = request.getContextPath()+"/index.action";
	   
	   mav.addObject("msg", msg);
	   mav.addObject("loc", loc);
	   mav.setViewName("msg");
	   
	   return mav;
   }
   
   
   
   
   
   // === #90. 원게시물에 딸린 댓글들을 페이징처리해서 조회해오기(Ajax 로 처리) ===
   @ResponseBody
   @RequestMapping(value="/commentList.action", produces="text/plain;charset=UTF-8")      
   public String commentList(HttpServletRequest request) {
	   
	   String parentSeq = request.getParameter("parentSeq"); 
	   String currentShowPageNo = request.getParameter("currentShowPageNo");
	   
	   if(currentShowPageNo == null) {
		   currentShowPageNo = "1";
	   }
	   
	   int sizePerPage = 5; // 한 페이지당 5개의 댓글을 보여줄것이다.
	   
	// **** 가져올 게시글의 범위를 구한다.(공식임!!!) **** 
		/*
		     currentShowPageNo      startRno     endRno
		    --------------------------------------------
		         1 page        ===>    1           10
		         2 page        ===>    11          20
		         3 page        ===>    21          30
		         4 page        ===>    31          40
		         ......                ...         ...
		 */
		
	   int startRno = (( Integer.parseInt(currentShowPageNo) - 1 ) * sizePerPage) + 1;
	   int endRno = startRno + sizePerPage - 1; 
		
	   HashMap<String, String> paraMap = new HashMap<>();
	   paraMap.put("parentSeq", parentSeq);
	   paraMap.put("startRno", String.valueOf(startRno));
	   paraMap.put("endRno", String.valueOf(endRno));
	   
	   List<CommentVO> commentList = service.getCommentListPaging(paraMap);
	   
	   JSONArray jsonArr = new JSONArray();
	   
	   if(commentList != null) {
		   for(CommentVO cmtvo : commentList) {
		       JSONObject jsonObj = new JSONObject();
		       jsonObj.put("content", cmtvo.getContent());
		       jsonObj.put("name", cmtvo.getName());
	   		   jsonObj.put("regDate", cmtvo.getRegDate());
		    		
		       jsonArr.put(jsonObj);
		    }
	   }
	    
	   return jsonArr.toString();
   }
   
   
   // === #130. 원게시물에 딸린 댓글 조회해오기(Ajax로 처리) ===
   @ResponseBody
   @RequestMapping(value="/getCommentTotalPage.action")
   public String getCommentTotalPage(HttpServletRequest request) {
	   
	   String parentSeq = request.getParameter("parentSeq");
	   String sizePerPage = request.getParameter("sizePerPage");
	   
	   HashMap<String,String> paraMap = new HashMap<>();
	   paraMap.put("parentSeq", parentSeq);
	   paraMap.put("sizePerPage", sizePerPage);
	   
	   // 원글 글번호(parentSeq)에 해당하는 댓글의 총갯수 알아오기
	   int totalCount = service.getCommentTotalCount(paraMap);
	   

	   // 총페이지수(totalPage) 구하기
	   // 만약에 총 게시물 건수(totalCount)가 23개 이라면
	   // 총 페이지 수(totalPage)는 5개가 되어야 한다.
	   int totalPage = (int) Math.ceil( (double)totalCount/Integer.parseInt(sizePerPage));  
	   // (double)23/5 ==> 4.6 ==> Math.ceil(4.6) ==> (int)5.0  ==> 5
	   // (double)20/5 ==> 4.0 ==> Math.ceil(4.0) ==> (int)4.0  ==> 4
	   
	   JSONObject jsonObj = new JSONObject();
	   jsonObj.put("totalPage", totalPage);
	   
	   return jsonObj.toString();
   }
   
   
   // ===== #159. 첨부파일 다운로드 받기 =====
   @RequestMapping(value="/download.action") 
   public void requiredLogin_download(HttpServletRequest request, HttpServletResponse response) {
		
	   String seq = request.getParameter("seq"); 
	   // 첨부파일이 있는 글번호
		
	   // 첨부파일이 있는 글번호에서 
	   // 201907250930481985323774614.png 처럼
	   // 이러한 fileName 값을 DB에서 가져와야 한다. 
	   // 또한 orgFileName 값도 DB에서 가져와야 한다.
		
	   BoardVO vo = service.getViewWithNoAddCount(seq);
	   // 조회수 증가 없이 1개 글 가져오기
	   // 먼저 board.xml 에 가서 id가 getView 인것에서
	   // select 절에 fileName, orgFilename, fileSize 컬럼을
	   // 추가해주어야 한다.
	   
	   String fileName = vo.getFileName(); 
	   // 201907250930481985323774614.png 와 같은 것이다.
	   // 이것이 바로 WAS(톰캣) 디스크에 저장된 파일명이다.
	   
	   String orgFilename = vo.getOrgFilename(); 
	   // 강아지.png 처럼 다운받을 사용자에게 보여줄 파일명.
	
		
	   // 첨부파일이 저장되어 있는 
	   // WAS(톰캣)의 디스크 경로명을 알아와야만 다운로드를 해줄수 있다. 
	   // 이 경로는 우리가 파일첨부를 위해서
	   //    /addEnd.action 에서 설정해두었던 경로와 똑같아야 한다.
	   // WAS 의 webapp 의 절대경로를 알아와야 한다. 
	   HttpSession session = request.getSession();
	
	   String root = session.getServletContext().getRealPath("/"); 
	   String path = root + "resources"+File.separator+"files";
	   // path 가 첨부파일들을 저장할 WAS(톰캣)의 폴더가 된다. 
	 
	   // **** 다운로드 하기 **** //
	   // 다운로드가 실패할 경우 메시지를 띄워주기 위해서
	   // boolean 타입 변수 flag 를 선언한다.
	   boolean flag = false;
		
	   flag = fileManager.doFileDownload(fileName, orgFilename, path, response);
	   // 다운로드가 성공이면 true 를 반납해주고,
	   // 다운로드가 실패이면 false 를 반납해준다.
		
	   if(!flag) {
		   // 다운로드가 실패할 경우 메시지를 띄워준다.
			
		   response.setContentType("text/html; charset=UTF-8"); 
		   PrintWriter writer = null;
			
		   try {
			   writer = response.getWriter();
			   // 웹브라우저상에 메시지를 쓰기 위한 객체생성.
		   } catch (IOException e) {
				
		   }
			
		   writer.println("<script type='text/javascript'>alert('파일 다운로드가 불가능합니다.!!')</script>");       
			
	   }
		 
   } // end of void download(HttpServletRequest req, HttpServletResponse res)---------
   
   
   // === #162. 스마트에디터. 드래그앤드롭을 사용한 다중사진 파일 업로드 === //
   @RequestMapping(value="/image/multiplePhotoUpload.action", method= {RequestMethod.POST}) 
   public void multiplePhotoUpload(HttpServletRequest request, HttpServletResponse response) {
	   
	   /*
	   		1. 사용자가 보낸 파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
	   		>>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
	   		우리는 WAS 의 webapp/resources/photo_upload 라는 폴더로 지정해준다.
	   */
	   
	   // WAS의 webapp 의 절대경로를 알아와야 한다.
	   HttpSession session = request.getSession();
	   String root = session.getServletContext().getRealPath("/");
	   String path = root + "resources" + File.separator + "photo_upload";
	   /*  	File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
	     	운영체제가 Windows 이라면 File.separator 는 "\" 이고,
	     	운영체제가 UNIX, Linux 이라면 File.separator 는 "/" 이다.
	   */
	   // path 가 첨부파일을 저장할 WAS(톰캣)의 폴더가 된다.
	   // System.out.println("BoardController path => " + path);
	   // BoardController path => C:\springworkspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\photo_upload
	   
	   File dir = new File(path);
	   if(!dir.exists()) {
		   dir.mkdir();
	   }
	   
	   String strURL = "";
		
	   try {
		   if(!"OPTIONS".equals(request.getMethod().toUpperCase())) {
			   String filename = request.getHeader("file-name"); //파일명을 받는다 - 일반 원본파일명
		    		
			   // System.out.println(">>>> 확인용 filename ==> " + filename); 
		       // >>>> 확인용 filename ==> berkelekle%ED%8A%B8%EB%9E%9C%EB%94%9405.jpg

			   InputStream is = request.getInputStream();
		    	
			   /*
		          	요청 헤더의 content-type이 application/json 이거나 multipart/form-data 형식일 때,
		          	혹은 이름 없이 값만 전달될 때 이 값은 요청 헤더가 아닌 바디를 통해 전달된다. 
		          	이러한 형태의 값을 'payload body'라고 하는데 요청 바디에 직접 쓰여진다 하여 'request body post data'라고도 한다.

	               	서블릿에서 payload body는 Request.getParameter()가 아니라 
	            	Request.getInputStream() 혹은 Request.getReader()를 통해 body를 직접 읽는 방식으로 가져온다. 	
			   */

			   String newFilename = fileManager.doFileUpload(is, filename, path);
		    	
			   int width = fileManager.getImageWidth(path+File.separator+newFilename);
				
			   if(width > 600)
				   width = 600;
					
			   // System.out.println(">>>> 확인용 width ==> " + width);
			   // >>>> 확인용 width ==> 600
			   // >>>> 확인용 width ==> 121
		    	
			   String CP = request.getContextPath(); // board
				
			   strURL += "&bNewLine=true&sFileName="; 
			   strURL += newFilename;
			   strURL += "&sWidth="+width;
			   strURL += "&sFileURL="+CP+"/resources/photo_upload/"+newFilename;
		   }
			
		   /// 웹브라우저상에 사진 이미지를 쓰기 ///
		   PrintWriter out = response.getWriter();
		   out.print(strURL);
	   } catch(Exception e){
		   e.printStackTrace();
	   }
	   
   } // end of public void multiplePhotoUpload(HttpServletRequest request, HttpServletResponse response)
   
   //=== #166. (웹채팅관련4) ===
   @RequestMapping(value="/chatting/multichat.action", method= {RequestMethod.GET}) 
   public String requiredLogin_multichat(HttpServletRequest request, HttpServletResponse response) { 
	   return "chatting/multichat.tiles1";
   }

   
}







