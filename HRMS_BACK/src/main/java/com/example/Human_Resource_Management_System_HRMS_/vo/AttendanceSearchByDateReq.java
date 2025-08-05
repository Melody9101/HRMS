package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDate;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AttendanceSearchByDateReq {

	// 查詢對象員編
	@Min(value = 1, message = ConstantsMessage.PARAMETER_EMPLOYEE_ID_ERROR)
	private int employeeId;

	// 欲查詢之開始時間
	@NotNull(message = ConstantsMessage.PARAMETER_START_TIME_ERROR)
	private LocalDate startTime;

	// 欲查詢之結束時間
	@NotNull(message = ConstantsMessage.PARAMETER_END_TIME_ERROR)
	private LocalDate endTime;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public LocalDate getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDate startTime) {
		this.startTime = startTime;
	}

	public LocalDate getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDate endTime) {
		this.endTime = endTime;
	}

}
