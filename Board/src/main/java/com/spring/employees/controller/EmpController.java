package com.spring.employees.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.spring.employees.service.InterEmpService;

@Controller
public class EmpController {

	@Autowired
	private InterEmpService service;
	
	
	@RequestMapping(value="/emp/empList.action")
	public ModelAndView empList(HttpServletRequest request, ModelAndView mav) {
		
		List<String> deptIdList = service.deptIdList();
		
		String sDeptIdes = request.getParameter("sDeptIdes");
		// sDeptIdes ==> "-9999,50,110"
		// sDeptIdes ==> 
		// sDeptIdes ==> "10,30,50,80,110"
		
		String gender = request.getParameter("gender");
		
		HashMap<String, Object> paraMap = new HashMap<>();
		
		if(sDeptIdes != null && !"".equals(sDeptIdes)) {
			String[] deptIdArr = sDeptIdes.split(",");
			paraMap.put("deptIdArr", deptIdArr);
			
			mav.addObject("sDeptIdes", sDeptIdes);
			// 체크되어진 값을 유지시키기 위한 것이다.
		}
		
		if(gender != null && !"".equals(gender) && !"성별".equals(gender)) {
			paraMap.put("gender", gender);
			mav.addObject("gender", gender);
		}
		
		List<HashMap<String, String>> empList = service.empList(paraMap);

		mav.addObject("empList", empList);
		mav.addObject("deptIdList", deptIdList);
		mav.setViewName("emp/empList.tiles2");
		
		return mav;
	}
	
	
	// >>> 차트를 보여주는 view단 <<< //
	@RequestMapping(value="emp/chart.action")
	public ModelAndView chart(ModelAndView mav) {
		mav.setViewName("emp/chart.tiles2");
		return mav;
	}
	
	// >>> 차트그리기(Ajax) 부서번호별 인원수 <<< //
	
	
	// >>> 차트그리기(Ajax) 성별 인원수 <<< //
}
