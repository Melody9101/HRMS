package com.example.Human_Resource_Management_System_HRMS_.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeDto {

	private int id;

	private String department;

	private String name;

	private String email;

	private String password;

	private String phone;

	private Boolean gender;

	private int grade;

	private LocalDate entryDate;

	private LocalDate resignationDate;

	private String resignationReason;

	private int salaries;

	private Boolean employed;

	private BigDecimal remainingPreviousAnnualLeave;

	private BigDecimal remainingCurrentAnnualLeave;

	private BigDecimal remainingPaidSickLeave;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public LocalDate getResignationDate() {
		return resignationDate;
	}

	public void setResignationDate(LocalDate resignationDate) {
		this.resignationDate = resignationDate;
	}

	public String getResignationReason() {
		return resignationReason;
	}

	public void setResignationReason(String resignationReason) {
		this.resignationReason = resignationReason;
	}

	public int getSalaries() {
		return salaries;
	}

	public void setSalaries(int salaries) {
		this.salaries = salaries;
	}

	public Boolean getEmployed() {
		return employed;
	}

	public void setEmployed(Boolean employed) {
		this.employed = employed;
	}

	public BigDecimal getRemainingPreviousAnnualLeave() {
		return remainingPreviousAnnualLeave;
	}

	public void setRemainingPreviousAnnualLeave(BigDecimal remainingPreviousAnnualLeave) {
		this.remainingPreviousAnnualLeave = remainingPreviousAnnualLeave;
	}

	public BigDecimal getRemainingCurrentAnnualLeave() {
		return remainingCurrentAnnualLeave;
	}

	public void setRemainingCurrentAnnualLeave(BigDecimal remainingCurrentAnnualLeave) {
		this.remainingCurrentAnnualLeave = remainingCurrentAnnualLeave;
	}

	public BigDecimal getRemainingPaidSickLeave() {
		return remainingPaidSickLeave;
	}

	public void setRemainingPaidSickLeave(BigDecimal remainingPaidSickLeave) {
		this.remainingPaidSickLeave = remainingPaidSickLeave;
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
