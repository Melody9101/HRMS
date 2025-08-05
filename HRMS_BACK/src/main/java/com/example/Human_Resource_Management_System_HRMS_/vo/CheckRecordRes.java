package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveRecordDto;

public class CheckRecordRes extends BasicRes {

	private List<LeaveRecordDto> leaveApplicationList;

	public CheckRecordRes() {
		super();
	}

	public CheckRecordRes(int code, String message) {
		super(code, message);
	}

	public CheckRecordRes(int code, String message, List<LeaveRecordDto> leaveApplicationList) {
		super(code, message);
		this.leaveApplicationList = leaveApplicationList;
	}

	public List<LeaveRecordDto> getLeaveApplicationList() {
		return leaveApplicationList;
	}

	public void setLeaveApplicationList(List<LeaveRecordDto> leaveApplicationList) {
		this.leaveApplicationList = leaveApplicationList;
	}

}
