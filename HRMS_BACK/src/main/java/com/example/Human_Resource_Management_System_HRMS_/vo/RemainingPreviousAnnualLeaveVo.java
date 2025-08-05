package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.math.BigDecimal;

public class RemainingPreviousAnnualLeaveVo {

	private int employeeId;

	private String employeeName;

	private String employeeDepartment;

	private BigDecimal remainingPreviousAnnualLeave;

	private BigDecimal remainingPaidSickLeave;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public BigDecimal getRemainingPreviousAnnualLeave() {
		return remainingPreviousAnnualLeave;
	}

	public void setRemainingPreviousAnnualLeave(BigDecimal remainingPreviousAnnualLeave) {
		this.remainingPreviousAnnualLeave = remainingPreviousAnnualLeave;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeDepartment() {
		return employeeDepartment;
	}

	public void setEmployeeDepartment(String employeeDepartment) {
		this.employeeDepartment = employeeDepartment;
	}

	public BigDecimal getRemainingPaidSickLeave() {
		return remainingPaidSickLeave;
	}

	public void setRemainingPaidSickLeave(BigDecimal remainingPaidSickLeave) {
		this.remainingPaidSickLeave = remainingPaidSickLeave;
	}

}
