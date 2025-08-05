package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.NotNull;

public class UpdateSalariesByIdReq {
	
	// 因需透過薪資單ID才能做更新，故ID不可為 null。
	@NotNull(message = ConstantsMessage.ID_IS_NECESSARY)
	private Integer id;
	
	// 為確保薪資單資料的正確性與一致性，employee_id 不可被修改。
	
	// 因不是每次都需更新，salary 和 bonus 可為 null。
	private Integer salary;
	
	private Integer bonus;
	
	// 最後更新日期(finalUpdateDate): 帶入後端寫入時間。
	
	// 最後支薪人(finalUpdateEmployeeId): 帶入session中儲存的資訊，避免人為惡意操作。
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSalary() {
		return salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	public Integer getBonus() {
		return bonus;
	}

	public void setBonus(Integer bonus) {
		this.bonus = bonus;
	}
}
