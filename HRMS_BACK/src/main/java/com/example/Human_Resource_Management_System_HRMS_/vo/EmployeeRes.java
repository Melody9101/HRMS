package com.example.Human_Resource_Management_System_HRMS_.vo;

public class EmployeeRes extends BasicRes {
	
	private EmployeeInfoVo employeeInfo;

	public EmployeeRes(int code, String message, EmployeeInfoVo employeeInfo) {
		super(code, message);
		this.employeeInfo = employeeInfo;
	}

	public EmployeeRes() {
		super();
	}

	public EmployeeRes(int code, String message) {
		super(code, message);
	}

	public EmployeeInfoVo getEmployeeInfo() {
		return employeeInfo;
	}

	public void setEmployeeInfo(EmployeeInfoVo employeeInfo) {
		this.employeeInfo = employeeInfo;
	}

}
