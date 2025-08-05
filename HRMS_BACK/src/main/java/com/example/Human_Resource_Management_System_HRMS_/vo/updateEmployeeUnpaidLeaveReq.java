package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDate;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class updateEmployeeUnpaidLeaveReq {

	// 因為依據 id 做更新，故 id 必須輸入不能為空。
	@Min(value = 1, message = ConstantsMessage.PARAMETER_EMPLOYEE_ID_ERROR)
	private int id;

	@NotNull(message = ConstantsMessage.UNPAID_LEAVE_START_DATE_IS_NECESSARY)
	private LocalDate unpaidLeaveStartDate;

	@NotNull(message = ConstantsMessage.UNPAID_LEAVE_END_DATE_IS_NECESSARY)
	private LocalDate unpaidLeaveEndDate;

	// 無薪假理由字數應限制在200字內(因資料庫長度限VARCHAR(200))。
	@NotBlank(message = ConstantsMessage.UNPAID_LEAVE_REASON_IS_NECESSARY)
	@Size(max = 200, message = ConstantsMessage.UNPAID_LEAVE_REASON_TOO_LONG)
	private String unpaidLeaveReason;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getUnpaidLeaveStartDate() {
		return unpaidLeaveStartDate;
	}

	public void setUnpaidLeaveStartDate(LocalDate unpaidLeaveStartDate) {
		this.unpaidLeaveStartDate = unpaidLeaveStartDate;
	}

	public LocalDate getUnpaidLeaveEndDate() {
		return unpaidLeaveEndDate;
	}

	public void setUnpaidLeaveEndDate(LocalDate unpaidLeaveEndDate) {
		this.unpaidLeaveEndDate = unpaidLeaveEndDate;
	}

	public String getUnpaidLeaveReason() {
		return unpaidLeaveReason;
	}

	public void setUnpaidLeaveReason(String unpaidLeaveReason) {
		this.unpaidLeaveReason = unpaidLeaveReason;
	}
}
