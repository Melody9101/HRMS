package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

public class EmployeeBasicInfoRes extends BasicRes {

	private List<EmployeeBasicInfoVo> EmployeeBasicInfoVoList;

	public EmployeeBasicInfoRes() {
		super();
	}

	public EmployeeBasicInfoRes(int code, String message) {
		super(code, message);
	}

	public EmployeeBasicInfoRes(int code, String message, List<EmployeeBasicInfoVo> employeeBasicInfoVoList) {
		super(code, message);
		EmployeeBasicInfoVoList = employeeBasicInfoVoList;
	}

	public List<EmployeeBasicInfoVo> getEmployeeBasicInfoVoList() {
		return EmployeeBasicInfoVoList;
	}

	public void setEmployeeBasicInfoVoList(List<EmployeeBasicInfoVo> employeeBasicInfoVoList) {
		EmployeeBasicInfoVoList = employeeBasicInfoVoList;
	}

}
