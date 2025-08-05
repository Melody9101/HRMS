package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 方法4: 透過員編編輯員工職等、調部門和薪資資訊。
public class UpdateEmployeeJobReq extends UpdateEmployeeApplicationData {

	private int id;

	@NotBlank(message = ConstantsMessage.DEPARTMENT_IS_NECESSARY)
	private String department;

	// 職等設定限定為 1~50。
	@Min(value = 1, message = ConstantsMessage.GRADE_OUT_OF_RANGE_ERROR)
	@Max(value = 50, message = ConstantsMessage.GRADE_OUT_OF_RANGE_ERROR)
	@NotNull(message = ConstantsMessage.GRADE_CANNOT_BE_NULL)
	private int grade;

	// 最低薪資不可少於1。
	@Min(value = 1, message = ConstantsMessage.PARAM_SALARIES_ERROR)
	@NotNull(message = ConstantsMessage.PARAM_SALARIES_ERROR)
	private int salaries;

	// 職等Type檢查在 EmployeeServiceImpl 中。
	@NotBlank(message = ConstantsMessage.PARAM_POSITION_ERROR)
	private String position;

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
