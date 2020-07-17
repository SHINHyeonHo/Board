package com.spring.board.aop;

import java.util.HashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.service.InterBoardService;

//=== #53. 공통관심사 클래스(Aspect 클래스)생성하기 === //
@Aspect		//	1공통관심사 클래스 객체로 등록된다.
@Component	//	bean 으로 등록된다. 
public class PointPlus {
	
	@Autowired
	InterBoardService service;
	
	// == Pointcut(주업무) 을 생성한다. == 
	//	  Pointcut 이란 공통관심사를 필요로 하는 메소드를 말한다.
	@Pointcut("execution(public * com.spring..*Controller.pointPlus_*(..)  )")
	public void pointPlus() {}
	
	// == After Advice(공통관심사, 보조업무) 를 구현한다. ==
	@SuppressWarnings("unchecked") // 앞으로는 노란줄 경고 표시를 하지말라는 뜻이다.
	@After("pointPlus()")
	public void userPointPlus(JoinPoint joinPoint) {
		// JoinPoint joinPoint 는 포인트컷 되어진 주업무의 메소드이다.
		
		HashMap<String, String> paraMap = (HashMap<String, String>) joinPoint.getArgs()[0];
		
		service.pointPlus(paraMap);
		
	}
	

}
