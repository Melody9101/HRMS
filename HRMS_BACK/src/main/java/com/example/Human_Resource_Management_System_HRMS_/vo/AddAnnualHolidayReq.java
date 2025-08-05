package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDate;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddAnnualHolidayReq {

	@NotNull(message = ConstantsMessage.PARAMETER_DATE_ERROR)
	private LocalDate date;

	@NotBlank(message = ConstantsMessage.NAME_IS_NECESSARY)
	private String name;

	private boolean holiday;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isHoliday() {
		return holiday;
	}

	public void setHoliday(boolean holiday) {
		this.holiday = holiday;
	}

}
