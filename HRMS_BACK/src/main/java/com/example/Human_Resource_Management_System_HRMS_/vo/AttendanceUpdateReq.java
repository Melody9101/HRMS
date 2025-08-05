package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDateTime;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;

public class AttendanceUpdateReq {

	// 欲更新的打卡單編號
	@Min(value = 1, message = ConstantsMessage.PARAMETER_ID_ERROR)
	private int attendanceId;

	// 欲更新的上班時間(若為null則套用原本的紀錄)
	private LocalDateTime clockInTime;

	// 欲更新的下班時間(若為null則套用原本的紀錄)
	private LocalDateTime clockOutTime;

	public int getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(int attendanceId) {
		this.attendanceId = attendanceId;
	}

	public LocalDateTime getClockInTime() {
		return clockInTime;
	}

	public void setClockInTime(LocalDateTime clockInTime) {
		this.clockInTime = clockInTime;
	}

	public LocalDateTime getClockOutTime() {
		return clockOutTime;
	}

	public void setClockOutTime(LocalDateTime clockOutTime) {
		this.clockOutTime = clockOutTime;
	}

}
