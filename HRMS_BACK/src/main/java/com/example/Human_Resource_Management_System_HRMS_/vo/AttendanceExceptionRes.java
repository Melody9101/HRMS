package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

public class AttendanceExceptionRes extends BasicRes {

	private List<AttendanceExceptionVo> exceptionList;

	public AttendanceExceptionRes(int code, String message, List<AttendanceExceptionVo> exceptionList) {
		super(code, message);
		this.exceptionList = exceptionList;
	}

	public AttendanceExceptionRes() {
		super();
	}

	public AttendanceExceptionRes(int code, String message) {
		super(code, message);
	}

	public List<AttendanceExceptionVo> getExceptionList() {
		return exceptionList;
	}

	public void setExceptionList(List<AttendanceExceptionVo> exceptionList) {
		this.exceptionList = exceptionList;
	}

}
