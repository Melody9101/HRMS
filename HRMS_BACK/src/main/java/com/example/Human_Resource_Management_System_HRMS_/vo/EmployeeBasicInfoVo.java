package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDate;

public class EmployeeBasicInfoVo {

	private int id;

	private String department;

	private String name;

	private String email;

	private String phone;

	private Boolean gender;

	private int grade;

	private LocalDate entryDate;

	private LocalDate unpaidLeaveStartDate;

	private LocalDate unpaidLeaveEndDate;

	private String unpaidLeaveReason;

	private String position;

	private LocalDate finalUpdateDate;

	private int finalUpdateEmployeeId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean getGender() {
		return gender;
	}

	public void setGender(Boolean gender) {
		this.gender = gender;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public LocalDate getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(LocalDate entryDate) {
		this.entryDate = entryDate;
	}

	public LocalDate getUnpaidLeaveStartDate() {
		return unpaidLeaveStartDate;
	}

	public void setUnpaidLeaveStartDate(LocalDate unpaidLeaveStartDate) {
		this.unpaidLeaveStartDate = unpaidLeaveStartDate;
	}

	public LocalDate getUnpaidLeaveEndDate() {
		return unpaidLeaveEndDate;
	}

	public void setUnpaidLeaveEndDate(LocalDate unpaidLeaveEndDate) {
		this.unpaidLeaveEndDate = unpaidLeaveEndDate;
	}

	public String getUnpaidLeaveReason() {
		return unpaidLeaveReason;
	}

	public void setUnpaidLeaveReason(String unpaidLeaveReason) {
		this.unpaidLeaveReason = unpaidLeaveReason;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public LocalDate getFinalUpdateDate() {
		return finalUpdateDate;
	}

	public void setFinalUpdateDate(LocalDate finalUpdateDate) {
		this.finalUpdateDate = finalUpdateDate;
	}

	public int getFinalUpdateEmployeeId() {
		return finalUpdateEmployeeId;
	}

	public void setFinalUpdateEmployeeId(int finalUpdateEmployeeId) {
		this.finalUpdateEmployeeId = finalUpdateEmployeeId;
	}

}
