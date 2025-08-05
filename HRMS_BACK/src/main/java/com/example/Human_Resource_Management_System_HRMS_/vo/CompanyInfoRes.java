package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.dto.CompanyInfoDto;

public class CompanyInfoRes extends BasicRes {

	private CompanyInfoDto companyInfo;

	public CompanyInfoRes() {
		super();
	}

	public CompanyInfoRes(int code, String message) {
		super(code, message);
	}

	public CompanyInfoRes(int code, String message, CompanyInfoDto companyInfo) {
		super(code, message);
		this.companyInfo = companyInfo;
	}

	public CompanyInfoDto getCompanyInfo() {
		return companyInfo;
	}

	public void setCompanyInfo(CompanyInfoDto companyInfo) {
		this.companyInfo = companyInfo;
	}

}
