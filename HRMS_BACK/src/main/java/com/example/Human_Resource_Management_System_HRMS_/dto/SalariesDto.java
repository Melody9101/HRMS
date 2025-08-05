package com.example.Human_Resource_Management_System_HRMS_.dto;

import java.time.LocalDate;

public class SalariesDto {

	private Integer id; // 該筆薪資單ID。

	private int year; // 薪資單年份。

	private int month; // 薪資單月份。

	private Integer employeeId; // 該筆薪資單所屬員工的員工ID。

	private int salary; // 當月薪資。

	private int bonus; // 當月獎金。

	private LocalDate payDate; // 支薪日(新增該筆薪資單的日期)。

	private Integer payerId; // 支薪者(第一位新增者)。

	private LocalDate finalUpdateDate; // 最後更改日期(更新用)。

	private Integer finalUpdateEmployeeId; // 最後更改者(更新用)。

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
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

	public LocalDate getPayDate() {
		return payDate;
	}

	public void setPayDate(LocalDate payDate) {
		this.payDate = payDate;
	}

	public Integer getPayerId() {
		return payerId;
	}

	public void setPayerId(Integer payerId) {
		this.payerId = payerId;
	}

	public LocalDate getFinalUpdateDate() {
		return finalUpdateDate;
	}

	public void setFinalUpdateDate(LocalDate finalUpdateDate) {
		this.finalUpdateDate = finalUpdateDate;
	}

	public Integer getFinalUpdateEmployeeId() {
		return finalUpdateEmployeeId;
	}

	public void setFinalUpdateEmployeeId(Integer finalUpdateEmployeeId) {
		this.finalUpdateEmployeeId = finalUpdateEmployeeId;
	}
}
