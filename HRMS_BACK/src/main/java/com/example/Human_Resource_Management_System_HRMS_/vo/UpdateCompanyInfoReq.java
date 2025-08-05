package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateCompanyInfoReq {

	@NotBlank(message = ConstantsMessage.NAME_REQUIRED)
	private String name;

	private int taxIdNumber;

	@NotBlank(message = ConstantsMessage.OWNER_NAME_REQUIRED)
	private String ownerName;

	@NotBlank(message = ConstantsMessage.PHONE_REQUIRED)
	private String phone;

	@NotBlank(message = ConstantsMessage.EMAIL_REQUIRED)
	private String email;

	@NotBlank(message = ConstantsMessage.ADDRESS_REQUIRED)
	private String address;

	@NotBlank(message = ConstantsMessage.WEBSITE_REQUIRED)
	private String website;

	@NotNull(message = ConstantsMessage.ESTABLISHMENT_DATE_REQUIRED)
	private LocalDate establishmentDate;

	@Min(value = 1, message = ConstantsMessage.CAPITAL_AMOUNT_ERROR)
	private int capitalAmount;
	
	@Min(value = 1, message = ConstantsMessage.EMPLOYEE_COUNT_REQUIRED)
	private int employeeCount;

	@NotBlank(message = ConstantsMessage.STATUS_REQUIRED)
	private String status;

	@NotNull(message = ConstantsMessage.WORK_START_TIME_REQUIRED)
	private LocalTime workStartTime;

	@NotNull(message = ConstantsMessage.LUNCH_START_TIME_REQUIRED)
	private LocalTime lunchStartTime;

	@NotNull(message = ConstantsMessage.LUNCH_END_TIME_REQUIRED)
	private LocalTime lunchEndTime;

	@NotBlank(message = ConstantsMessage.TIMEZONE_REQUIRED)
	private String timezone;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTaxIdNumber() {
		return taxIdNumber;
	}

	public void setTaxIdNumber(Integer taxIdNumber) {
		this.taxIdNumber = taxIdNumber;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public LocalDate getEstablishmentDate() {
		return establishmentDate;
	}

	public void setEstablishmentDate(LocalDate establishmentDate) {
		this.establishmentDate = establishmentDate;
	}

	public Integer getCapitalAmount() {
		return capitalAmount;
	}

	public void setCapitalAmount(Integer capitalAmount) {
		this.capitalAmount = capitalAmount;
	}

	public Integer getEmployeeCount() {
		return employeeCount;
	}

	public void setEmployeeCount(Integer employeeCount) {
		this.employeeCount = employeeCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalTime getWorkStartTime() {
		return workStartTime;
	}

	public void setWorkStartTime(LocalTime workStartTime) {
		this.workStartTime = workStartTime;
	}

	public LocalTime getLunchStartTime() {
		return lunchStartTime;
	}

	public void setLunchStartTime(LocalTime lunchStartTime) {
		this.lunchStartTime = lunchStartTime;
	}

	public LocalTime getLunchEndTime() {
		return lunchEndTime;
	}

	public void setLunchEndTime(LocalTime lunchEndTime) {
		this.lunchEndTime = lunchEndTime;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

}
