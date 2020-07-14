package com.spring.model;

public class HrVO {

	private String employee_id;
	private String ename;
	private String salary;
	private String jubun;
	
	public String getEmployee_id() {
		return employee_id;
	}
	public void setEmployee_id(String employee_id) {
		this.employee_id = employee_id;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getSalary() {
		return salary;
	}
	public void setSalary(String salary) {
		this.salary = salary;
	}
	public String getJubun() {
		return jubun;
	}
	public void setJubun(String jubun) {
		this.jubun = jubun;
	}
	public int getAge() {
		int n = Integer.parseInt(jubun.substring(6,7));
		int birth = Integer.parseInt(jubun.substring(0,2));
		int age = 0;
		
		if( n == 1 || n == 2) {
			age = 2020 - 1900 - birth + 1;
		}
		else {
			age = 2020 - 2000 - birth + 1;
		}
		
		return age;
	}
	public String getGender() {
		String gender = "";
		int n = Integer.parseInt(jubun.substring(6,7));
		
		if(n == 1 || n == 3) {
			gender = "남자";
		}
		else if(n == 2 || n == 4) {
			gender = "여자";
		}
		else {
			gender = "고자";
		}
		
		return gender;
	}
	
	
}
