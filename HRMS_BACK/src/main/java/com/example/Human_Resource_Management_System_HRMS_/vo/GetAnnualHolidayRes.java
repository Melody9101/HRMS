package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

import com.example.Human_Resource_Management_System_HRMS_.dto.AnnualHolidayDto;

public class GetAnnualHolidayRes extends BasicRes {

	private List<AnnualHolidayDto> annualHolidayList;

	public GetAnnualHolidayRes() {
		super();
	}

	public GetAnnualHolidayRes(int code, String message) {
		super(code, message);
	}

	public GetAnnualHolidayRes(int code, String message, List<AnnualHolidayDto> annualHolidayList) {
		super(code, message);
		this.annualHolidayList = annualHolidayList;
	}

	public List<AnnualHolidayDto> getAnnualHolidayList() {
		return annualHolidayList;
	}

	public void setAnnualHolidayList(List<AnnualHolidayDto> annualHolidayList) {
		this.annualHolidayList = annualHolidayList;
	}

}
