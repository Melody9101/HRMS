package com.example.Human_Resource_Management_System_HRMS_.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AnnualHolidayDto {

	private LocalDate date;

	private String name;

	private boolean holiday;

	private int createBy;

	private LocalDateTime createAt;

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

	public int getCeateBy() {
		return createBy;
	}

	public void setCeateBy(int ceateBy) {
		this.createBy = ceateBy;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

}
