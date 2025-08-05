package com.example.Human_Resource_Management_System_HRMS_.service.ifs;

import java.util.List;

import com.example.Human_Resource_Management_System_HRMS_.vo.AddSalariesReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CalculateMonthlySalaryRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetAllSalariesOrByEmployeeIdListRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetSalaryByEmployeeIdYYMMRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateSalariesByIdReq;

public interface SalariesService {
	
	/**
	 * 方法1-addSalaries: 新增薪資單。
	 * **會計部門、老闆使用。**
	 * 
	 * @param req
	 * @return
	 */
	public BasicRes addSalaries(AddSalariesReq req);
	
	/**
	 * 方法2: updateSalariesById更新(修改)薪資單。
	 * **會計"主管"使用。**
	 * 
	 * @param req
	 * @return
	 */
	public BasicRes updateSalariesById(UpdateSalariesByIdReq req);
	
	/**
	 * 方法3: getAllSalariesOrByEmployeeIdList查詢薪資單。
	 * **會計部門成員及老闆可使用，但一般員工無法看到主管職和老闆薪水。**
	 * 
	 * @param idList
	 * @param year
	 * @param month
	 * @return
	 */
	public GetAllSalariesOrByEmployeeIdListRes getAllSalariesOrByEmployeeIdList(List<Integer> idList, int year, Integer month);
	
	/**
	 * 方法4: getSalaryByEmployeeIdYYMM查詢薪資單-帶入員工ID、年度和月份。
	 * **員工本人使用，但需先登入。**
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public GetSalaryByEmployeeIdYYMMRes getSalaryByEmployeeIdYYMM(int year, Integer month);
	
	/**
	 * 方法5: calculateMonthlySalaryByEmployeeIdYYMMBonus計算薪資。
	 * **僅會計部門可使用。一般員工無權力計算主管級以上員工薪資、主管級可計算全部員工薪資。**
	 * 
	 * @param employeeId 員工ID
	 * @param year 計算年份
	 * @param month 計算月份
	 * @param bonus 當月員工獎金
	 * @return CalculateMonthlySalaryRes
	 */
	public CalculateMonthlySalaryRes calculateMonthlySalaryByEmployeeIdYYMMBonus(int employeeId, int year, int month, int bonus);
}