package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceVo {

	private int employeeId;

	private String employeeName;

	private String department;

	private LocalDateTime clockIn;

	private LocalDateTime clockOut;

	private LocalDate finalUpdateDate;

	private int finalUpdateEmployeeId;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
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
