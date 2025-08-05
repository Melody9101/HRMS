package com.example.Human_Resource_Management_System_HRMS_.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceDto {

	private int id;

	private int employeeId;

	private LocalDateTime clockIn;

	private LocalDateTime clockOut;

	private LocalDate finalUpdateDate;

	private int finalUpdateEmployeeId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public LocalDate getFinalUpdateDate() {
		return finalUpdateDate;
	}

	public void setFinalUpdateDate(LocalDate finalUpdateDate) {
		this.finalUpdateDate = finalUpdateDate;
	}
	public int getFinalUpdateEmployeeId() {
		return finalUpdateEmployeeId;
	}

	public void setFinalUpdateEmployeeId(int finalUpdateEmployeeId) {
		this.finalUpdateEmployeeId = finalUpdateEmployeeId;
	}

}
