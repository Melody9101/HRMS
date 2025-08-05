package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.Objects;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddFixedHolidayReq {

	@Min(value = 1, message = ConstantsMessage.INVALID_MONTH)
	@Max(value = 12, message = ConstantsMessage.INVALID_MONTH)
	private int month;

	@Min(value = 1, message = ConstantsMessage.INVALID_DAY)
	@Max(value = 31, message = ConstantsMessage.INVALID_DAY)
	private int day;

	@NotNull(message = ConstantsMessage.NAME_IS_NECESSARY)
	private String name;

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// 以下為用於 Set 比對用的，將三個參數的比對變為自訂的兩個參數
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AddFixedHolidayReq that = (AddFixedHolidayReq) o;
		return month == that.month && day == that.day;
	}

	@Override
	public int hashCode() {
		return Objects.hash(month, day);
	}

}
