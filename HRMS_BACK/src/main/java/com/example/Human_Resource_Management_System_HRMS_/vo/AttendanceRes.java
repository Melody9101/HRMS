package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

public class AttendanceRes extends BasicRes {

	private List<AttendanceVo> attendanceRecordList;

	public AttendanceRes() {
		super();
	}

	public AttendanceRes(int code, String message) {
		super(code, message);
	}

	public AttendanceRes(int code, String message, List<AttendanceVo> attendanceRecordList) {
		super(code, message);
		this.attendanceRecordList = attendanceRecordList;
	}

	public List<AttendanceVo> getAttendanceRecordList() {
		return attendanceRecordList;
	}

	public void setAttendanceRecordList(List<AttendanceVo> attendanceRecordList) {
		this.attendanceRecordList = attendanceRecordList;
	}

}
