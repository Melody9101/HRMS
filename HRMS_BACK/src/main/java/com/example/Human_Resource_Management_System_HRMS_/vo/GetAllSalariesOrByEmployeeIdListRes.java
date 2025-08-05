package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

import com.example.Human_Resource_Management_System_HRMS_.dto.SalariesDto;

public class GetAllSalariesOrByEmployeeIdListRes extends BasicRes {
	
	// 查詢到的薪水列表。
	private List<SalariesDto> salariesList;

	// 有權限查詢的員工ID列表。
	private List<Integer> finalAuthorizedIds;

	public GetAllSalariesOrByEmployeeIdListRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetAllSalariesOrByEmployeeIdListRes(int code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}

	public GetAllSalariesOrByEmployeeIdListRes(int code, String message, List<SalariesDto> salariesList,
			List<Integer> finalAuthorizedIds) {
		super(code, message);
		this.salariesList = salariesList;
		this.finalAuthorizedIds = finalAuthorizedIds;
	}

	public List<SalariesDto> getSalariesList() {
		return salariesList;
	}

	public void setSalariesList(List<SalariesDto> salariesList) {
		this.salariesList = salariesList;
	}

	public List<Integer> getFinalAuthorizedIds() {
		return finalAuthorizedIds;
	}

	public void setFinalAuthorizedIds(List<Integer> finalAuthorizedIds) {
		this.finalAuthorizedIds = finalAuthorizedIds;
	}
}
