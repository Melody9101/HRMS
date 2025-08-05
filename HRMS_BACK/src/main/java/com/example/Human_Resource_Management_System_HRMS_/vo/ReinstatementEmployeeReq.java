package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDate;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ReinstatementEmployeeReq {

	private int employeeId;

	// 部門不得為空值或空字串。
	@NotBlank(message = ConstantsMessage.DEPARTMENT_IS_NECESSARY)
	private String department;

	// 1. 信箱不得為空值或空字串。2. 信箱格式限制。
	@NotBlank(message = ConstantsMessage.EMAIL_IS_NECESSARY)
	@Email(regexp = ConstantsMessage.EMAIL_PATTERN, message = ConstantsMessage.EMAIL_ADDRESS_IS_INVALID)
	private String email;

	// 1. 手機不得為空值或空字串。2. 手機格式限制。
	@NotBlank(message = ConstantsMessage.PHONE_IS_NECESSARY)
	@Pattern(regexp = ConstantsMessage.PHONE_PATTERN, message = ConstantsMessage.PARAM_PHONE_ERROR)
	private String phone;

	// 職等設定限定為 1~11。
	@Min(value = 1, message = ConstantsMessage.GRADE_OUT_OF_RANGE_ERROR)
	@Max(value = 11, message = ConstantsMessage.GRADE_OUT_OF_RANGE_ERROR)
	private int grade;

	// 最低薪資不可少於1。
	@Min(value = 1, message = ConstantsMessage.PARAM_SALARIES_ERROR)
	private int salaries;

	// 職位不得為空，職等Type檢查在 EmployeeServiceImpl 中。
	@NotBlank(message = ConstantsMessage.PARAM_POSITION_ERROR)
	private String position;
	
	// 復職日期。
	@NotNull(message = ConstantsMessage.PARAM_REINSTATEMENT_ERROR)
	private LocalDate reinstatementDate;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public LocalDate getReinstatementDate() {
		return reinstatementDate;
	}

	public void setReinstatementDate(LocalDate reinstatementDate) {
		this.reinstatementDate = reinstatementDate;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
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

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getSalaries() {
		return salaries;
	}

	public void setSalaries(int salaries) {
		this.salaries = salaries;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

}
