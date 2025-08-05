package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

import com.example.Human_Resource_Management_System_HRMS_.dto.FixedHolidayDto;

public class FixedHolidayRes extends BasicRes {

	List<FixedHolidayDto> fixedHolidayList;

	public List<FixedHolidayDto> getFixedHolidayList() {
		return fixedHolidayList;
	}

	public void setFixedHolidayList(List<FixedHolidayDto> fixedHolidayList) {
		this.fixedHolidayList = fixedHolidayList;
	}

	public FixedHolidayRes() {
		super();
	}

	public FixedHolidayRes(int code, String message) {
		super(code, message);
	}

	public FixedHolidayRes(int code, String message, List<FixedHolidayDto> fixedHolidayList) {
		super(code, message);
		this.fixedHolidayList = fixedHolidayList;
	}

}
