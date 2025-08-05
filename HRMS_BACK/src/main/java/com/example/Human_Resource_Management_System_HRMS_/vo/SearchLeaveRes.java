package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveApplicationDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveRecordDto;

public class SearchLeaveRes extends BasicRes {

	private List<LeaveApplicationDto> leaveApplicationList;

	private List<LeaveRecordDto> leaveRecordList;

	public SearchLeaveRes() {
		super();
	}

	public SearchLeaveRes(int code, String message) {
		super(code, message);
	}

	public SearchLeaveRes(int code, String message, List<LeaveApplicationDto> leaveApplicationList,
			List<LeaveRecordDto> leaveRecordList) {
		super(code, message);
		this.leaveApplicationList = leaveApplicationList;
		this.leaveRecordList = leaveRecordList;
	}

	public List<LeaveApplicationDto> getLeaveApplicationList() {
		return leaveApplicationList;
	}

	public void setLeaveApplicationList(List<LeaveApplicationDto> leaveApplicationList) {
		this.leaveApplicationList = leaveApplicationList;
	}

	public List<LeaveRecordDto> getLeaveRecordList() {
		return leaveRecordList;
	}

	public void setLeaveRecordList(List<LeaveRecordDto> leaveRecordList) {
		this.leaveRecordList = leaveRecordList;
	}

}
