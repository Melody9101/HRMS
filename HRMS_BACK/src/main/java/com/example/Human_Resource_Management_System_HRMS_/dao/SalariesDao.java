package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.SalariesDto;

@Mapper
public interface SalariesDao {

	/**
	 * 方法1-addSalaries: 新增薪資單。 限會計部門、老闆使用。
	 * 
	 * @param year
	 * @param month
	 * @param employeeId
	 * @param salary
	 * @param bonus
	 * @param payDate
	 * @param payerId
	 */
	public void addSalaries( //
			@Param("year") int year, //
			@Param("month") int month, //
			@Param("employeeId") Integer employeeId, //
			@Param("salary") int salary, //
			@Param("bonus") int bonus, //
			@Param("payDate") LocalDate payDate, //
			@Param("payerId") Integer payerId);

	/**
	 * 方法2-countSalariesIdByEmpIdYYMM: 檢查當月薪資單是否有重複，每月僅一張薪資單。
	 * 
	 * @param employeeId
	 * @param year
	 * @param month
	 * @return
	 */
	public int countSalariesIdByEmpIdYYMM( //
			@Param("employeeId") int employeeId, //
			@Param("year") Integer year, //
			@Param("month") Integer month);

	/**
	 * 方法3: updateSalariesById更新(修改)薪資單。<br>
	 * 限會計"主管"使用。
	 * 
	 * @param id
	 * @param salary
	 * @param bonus
	 * @param finalUpdateDate
	 * @param finalUpdateEmployeeId
	 * @return
	 */
	public int updateSalariesById( //
			@Param("id") Integer id, //
			@Param("salary") Integer salary, //
			@Param("bonus") Integer bonus, //
			@Param("finalUpdateDate") LocalDate finalUpdateDate, //
			@Param("finalUpdateEmployeeId") Integer finalUpdateEmployeeId);

	/**
	 * 方法4: getAllSalariesOrByEmployeeIdList查詢薪資單。<br>
	 * 限會計部門成員及老闆可使用。
	 * 
	 * @param employeeIdList
	 * @param year
	 * @param month
	 * @return
	 */
	public List<SalariesDto> getAllSalariesOrByEmployeeIdList( //
			@Param("employeeIdList") List<Integer> employeeIdList, //
			@Param("year") Integer year, //
			@Param("month") Integer month);

	/**
	 * 方法5: getSalaryByEmployeeIdYYMM查詢薪資單-帶入員工ID、年度和月份。<br>
	 * 限員工本人使用，但需先登入。
	 * 
	 * @param employeeId
	 * @param year
	 * @param month
	 * @return
	 */
	public SalariesDto getSalaryByEmployeeIdYYMM( //
			@Param("employeeId") int employeeId, //
			@Param("year") Integer year, //
			@Param("month") Integer month);
}
