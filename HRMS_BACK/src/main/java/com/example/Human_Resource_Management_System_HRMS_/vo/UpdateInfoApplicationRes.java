package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

public class UpdateInfoApplicationRes extends BasicRes {

	private List<UpdateEmployeeInfoApplicationVo> applicationList;

	public UpdateInfoApplicationRes() {
		super();
	}

	public UpdateInfoApplicationRes(int code, String message) {
		super(code, message);
	}

	public UpdateInfoApplicationRes(int code, String message, List<UpdateEmployeeInfoApplicationVo> applicationList) {
		super(code, message);
		this.applicationList = applicationList;
	}

	public List<UpdateEmployeeInfoApplicationVo> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<UpdateEmployeeInfoApplicationVo> applicationList) {
		this.applicationList = applicationList;
	}

}
