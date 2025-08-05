package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDateTime;

public class TodayAttendanceRes extends BasicRes {

	private LocalDateTime clockInTime;

	private LocalDateTime clockOutTime;

	public TodayAttendanceRes() {
		super();
	}

	public TodayAttendanceRes(int code, String message) {
		super(code, message);
	}

	public TodayAttendanceRes(int code, String message, LocalDateTime clockInTime, LocalDateTime clockOutTime) {
		super(code, message);
		this.clockInTime = clockInTime;
		this.clockOutTime = clockOutTime;
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
