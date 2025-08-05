package com.example.Human_Resource_Management_System_HRMS_.vo;

public class CalculateMonthlySalaryRes extends BasicRes {

	// 月薪細節、計算結果列表。
	private CalculateMonthlySalaryVo monthlySalaryDetails;

	public CalculateMonthlySalaryRes() {
		super();
	}

	public CalculateMonthlySalaryRes(int code, String message) {
		super(code, message);
	}

	public CalculateMonthlySalaryRes(int code, String message, CalculateMonthlySalaryVo monthlySalaryDetails) {
		super(code, message);
		this.monthlySalaryDetails = monthlySalaryDetails;
	}

	public CalculateMonthlySalaryRes(CalculateMonthlySalaryVo monthlySalaryDetails) {
		super();
		this.monthlySalaryDetails = monthlySalaryDetails;
	}

	public CalculateMonthlySalaryVo getMonthlySalaryDetails() {
		return monthlySalaryDetails;
	}

	public void setMonthlySalaryDetails(CalculateMonthlySalaryVo monthlySalaryDetails) {
		this.monthlySalaryDetails = monthlySalaryDetails;
	}
}
