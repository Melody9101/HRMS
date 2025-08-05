package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

public class AttendanceSearchRes extends BasicRes {

	private List<AttendanceVo> recordList;

	private List<AttendanceExceptionVo> exceptionList;

	public AttendanceSearchRes() {
		super();
	}

	public AttendanceSearchRes(int code, String message) {
		super(code, message);
	}

	public AttendanceSearchRes(int code, String message, List<AttendanceVo> recordList,
			List<AttendanceExceptionVo> exceptionList) {
		super(code, message);
		this.recordList = recordList;
		this.exceptionList = exceptionList;
	}

	public List<AttendanceVo> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<AttendanceVo> recordList) {
		this.recordList = recordList;
	}

	public List<AttendanceExceptionVo> getExceptionList() {
		return exceptionList;
	}

	public void setExceptionList(List<AttendanceExceptionVo> exceptionList) {
		this.exceptionList = exceptionList;
	}

}
