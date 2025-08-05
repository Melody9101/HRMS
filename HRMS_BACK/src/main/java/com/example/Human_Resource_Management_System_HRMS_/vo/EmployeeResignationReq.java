package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDate;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

// 方法5: 透過員編處理員工離職。(1)需填寫離職日期、離職理由。(2)離職後薪水將被自動歸零salaries = 0、雇傭狀態也將自動更新為離職is_employed = false。
public class EmployeeResignationReq extends UpdateEmployeeApplicationData {

	private int id;
	// 離職日期不能為Null，也不能是未來日期。
	@NotNull(message = ConstantsMessage.RESIGNATION_DATE_IS_NECESSARY)
	@PastOrPresent(message = ConstantsMessage.PARAM_RESIGNATIONDATE_ERROR)
	private LocalDate resignationDate;

	// 離職理由不能為空。
	@NotBlank(message = ConstantsMessage.RESIGNATION_REASON_IS_NECESSARY)
	private String resignationReason;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getResignationDate() {
		return resignationDate;
	}

	public void setResignationDate(LocalDate resignationDate) {
		this.resignationDate = resignationDate;
	}

	public String getResignationReason() {
		return resignationReason;
	}

	public void setResignationReason(String resignationReason) {
		this.resignationReason = resignationReason;
	}

}
