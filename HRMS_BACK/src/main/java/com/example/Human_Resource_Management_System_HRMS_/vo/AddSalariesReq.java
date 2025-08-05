package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class AddSalariesReq {
	
	// 該筆薪資單所屬年份，必須輸入且不可為負數。
	@Min(value = 1, message = ConstantsMessage.PARAMETER_YEAR_ERROR)
	private int year;
	
	// 該筆薪資單所屬月份，必須輸入且不可小於1和大於12。
	@Min(value = 1, message = ConstantsMessage.PARAMETER_MONTH_ERROR)
	@Max(value = 12, message = ConstantsMessage.PARAMETER_MONTH_ERROR)
	private int month;
	
	// 必須輸入該筆薪資單所屬員工的員工編號ID。
	private int employeeId;
	
	// 當月薪水必須輸入且不得為負數。
	@Min(value = 0, message = ConstantsMessage.SALARY_CANNOT_BE_NEGATIVE)
	private int salary;
	
	// 當月獎金必須輸入且不得為負數。
	@Min(value = 0, message = ConstantsMessage.BONUS_CANNOT_BE_NEGATIVE)
	private int bonus;
	
	// 支付日期(pay_date): 帶入後端寫入時間。
	
	// 支薪人(payerId): 帶入session中儲存的資訊，避免人為惡意操作。
	
	// 最後更改日期(finalUpdateDate)、最後操作者(finalUpdateEmployeeId): 新增薪資單時不記入。
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
