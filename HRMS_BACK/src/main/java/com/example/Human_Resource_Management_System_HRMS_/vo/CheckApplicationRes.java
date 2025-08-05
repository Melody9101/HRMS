package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveApplicationDto;

public class CheckApplicationRes extends BasicRes {

	private List<LeaveApplicationDto> leaveApplicationList;

	public CheckApplicationRes(int code, String message, List<LeaveApplicationDto> leaveApplicationList) {
		super(code, message);
		this.leaveApplicationList = leaveApplicationList;
	}

	public CheckApplicationRes() {
		super();
	}

	public CheckApplicationRes(int code, String message) {
		super(code, message);
	}

	public List<LeaveApplicationDto> getLeaveApplicationList() {
		return leaveApplicationList;
	}

	public void setLeaveApplicationList(List<LeaveApplicationDto> leaveApplicationList) {
		this.leaveApplicationList = leaveApplicationList;
	}

}
