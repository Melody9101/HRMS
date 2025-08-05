package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

public class EmployeeListRes extends BasicRes {

	// 用於存放查詢到的員工列表。
	private List<EmployeeInfoVo> employeeList;

	public EmployeeListRes() {
		super();
	}

	public EmployeeListRes(int code, String massage) {
		super(code, massage);
	}

	public EmployeeListRes(int code, String massage, List<EmployeeInfoVo> employeeList) {
		super(code, massage);
		this.employeeList = employeeList;
	}

	public List<EmployeeInfoVo> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<EmployeeInfoVo> employeeList) {
		this.employeeList = employeeList;
	}

}
