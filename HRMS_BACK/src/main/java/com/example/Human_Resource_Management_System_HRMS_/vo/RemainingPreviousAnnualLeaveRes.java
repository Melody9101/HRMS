package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

public class RemainingPreviousAnnualLeaveRes extends BasicRes {

	List<RemainingPreviousAnnualLeaveVo> reminderList;

	public List<RemainingPreviousAnnualLeaveVo> getReminderList() {
		return reminderList;
	}

	public void setReminderList(List<RemainingPreviousAnnualLeaveVo> reminderList) {
		this.reminderList = reminderList;
	}

	public RemainingPreviousAnnualLeaveRes() {
		super();
	}

	public RemainingPreviousAnnualLeaveRes(int code, String message) {
		super(code, message);
	}

	public RemainingPreviousAnnualLeaveRes(int code, String message, //
			List<RemainingPreviousAnnualLeaveVo> reminderList) {
		super(code, message);
		this.reminderList = reminderList;
	}

}
