package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class AttendanceSearchByYYMMReq {

	// 欲查詢的員編
	@Min(value = 1, message = ConstantsMessage.PARAMETER_EMPLOYEE_ID_ERROR)
	private int employeeId;

	// 欲查詢的年份
	@Min(value = 1, message = ConstantsMessage.PARAMETER_YEAR_ERROR)
	private int year;

	// 欲查詢的月份
	@Min(value = 1, message = ConstantsMessage.PARAMETER_MONTH_ERROR)
	@Max(value = 12, message = ConstantsMessage.PARAMETER_MONTH_ERROR)
	private int month;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

}
