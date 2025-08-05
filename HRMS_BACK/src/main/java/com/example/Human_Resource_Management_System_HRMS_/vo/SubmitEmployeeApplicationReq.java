package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ApplicationType;
import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SubmitEmployeeApplicationReq {

	@Min(value = 1, message = ConstantsMessage.PARAMETER_EMPLOYEE_ID_ERROR)
	private int employeeId;

	@NotNull(message = ConstantsMessage.APPLICATION_TYPE_IS_REQUIRED)
	private ApplicationType type;

	private String comment;

	// 用 applicationType 來決定使用哪個類型
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
	@JsonSubTypes({ @JsonSubTypes.Type(value = UpdateEmployeeBasicInfoReq.class, name = "basic_info"),
			@JsonSubTypes.Type(value = UpdateEmployeeJobReq.class, name = "job"),
			@JsonSubTypes.Type(value = EmployeeResignationReq.class, name = "resign"),
			@JsonSubTypes.Type(value = UpdateClockTimeReq.class, name = "clock"),
			@JsonSubTypes.Type(value = OvertimeApplicationReq.class, name = "overtime") })
	@NotNull(message = ConstantsMessage.DATA_IS_NECESSARY)
	@Valid
	private UpdateEmployeeApplicationData data;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public ApplicationType getType() {
		return type;
	}

	public void setType(ApplicationType type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public UpdateEmployeeApplicationData getData() {
		return data;
	}

	public void setData(UpdateEmployeeApplicationData data) {
		this.data = data;
	}

}
