package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDateTime;

public class AbnormalAttendanceVo {

	private int employeeId; // 出勤異常的員工ID。

	private LocalDateTime clockIn; // 上班卡時間。

	private LocalDateTime clockOut; // 下班卡時間。

	// 出缺勤異常狀態abnormalType分為:
	// (1)上班遲到isLate: 有打上班卡但上班卡晚於規定的上班時間。
	// (2)早退isEarlyLeave: 有打上班卡但下班卡時間早於規定的下班時間。
	// (3)當天缺勤isAbsent: 上班卡和下班卡皆未打卡。
	// (4)上班卡沒打isMissingClockIn。
	// (5)下班卡沒打isMissingClockOut。
	private String abnormalType;
	
	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public LocalDateTime getClockIn() {
		return clockIn;
	}

	public void setClockIn(LocalDateTime clockIn) {
		this.clockIn = clockIn;
	}

	public LocalDateTime getClockOut() {
		return clockOut;
	}

	public void setClockOut(LocalDateTime clockOut) {
		this.clockOut = clockOut;
	}

	public String getAbnormalType() {
		return abnormalType;
	}

	public void setAbnormalType(String abnormalType) {
		this.abnormalType = abnormalType;
	}
}
