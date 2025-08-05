package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.dto.SalariesDto;

public class GetSalaryByEmployeeIdYYMMRes extends BasicRes {

	// 查詢到的該位員工的薪水資料。
	private SalariesDto salary;

	public GetSalaryByEmployeeIdYYMMRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetSalaryByEmployeeIdYYMMRes(int code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}

	public GetSalaryByEmployeeIdYYMMRes(int code, String message, SalariesDto salary) {
		super(code, message);
		this.salary = salary;
		// TODO Auto-generated constructor stub
	}

	public SalariesDto getSalary() {
		return salary;
	}

	public void setSalary(SalariesDto salary) {
		this.salary = salary;
	}
}
