package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeDto;

@Mapper
public interface EmployeeDao {

	// 方法1-addEmployee: 新增員工資訊(新進員工)。
	public void addEmployee( //
			@Param("department") String department, //
			@Param("name") String name, //
			@Param("email") String email, //
			@Param("password") String password, //
			@Param("phone") String phone, //
			@Param("gender") Boolean gender, //
			@Param("grade") int grade, //
			@Param("entryDate") LocalDate entryDate, //
			@Param("salaries") int salaries, //
			@Param("employed") Boolean employed, //
			@Param("position") String position, //
			@Param("finalUpdateDate") LocalDate finalUpdateDate, //
			@Param("finalUpdateEmployeeId") int finalUpdateEmployeeId);

	// 方法3-1-updateEmployeeBasicInfo: 透過員編編輯員工基本資訊。
	public int updateEmployeeBasicInfo( //
			@Param("id") int id, //
			@Param("name") String name, //
			@Param("email") String email, //
			@Param("phone") String phone, //
			@Param("gender") Boolean gender, //
			@Param("finalUpdateDate") LocalDate finalUpdateDate, //
			@Param("finalUpdateEmployeeId") int finalUpdateEmployeeId);

	// 方法3-updateEmployeeJob: 透過員編編輯員工調部門、職等、薪資和權等的資訊。
	public int updateEmployeeJob( //
			@Param("id") int id, //
			@Param("department") String department, //
			@Param("grade") int grade, //
			@Param("salaries") int salaries, //
			@Param("position") String position, //
			@Param("finalUpdateDate") LocalDate finalUpdateDate, //
			@Param("finalUpdateEmployeeId") int finalUpdateEmployeeId);

	// 方法4-resignEmployee: 透過員編處理員工離職。(1)需填寫離職日期、離職理由。(2)離職後薪水將被自動歸零salaries =
	// 0、雇傭狀態也將自動更新為離職employed = false。
	public int resignEmployee(
			@Param("id") int id, //
			@Param("resignationDate") LocalDate resignationDate, //
			@Param("resignationReason") String resignationReason, //
			@Param("finalUpdateDate") LocalDate finalUpdateDate, //
			@Param("finalUpdateEmployeeId") int finalUpdateEmployeeId);

	// 方法5-updateEmployeePassword: 透過信箱更新員工密碼。
	public int updateEmployeePassword( //
			@Param("email") String email, //
			@Param("newPassword") String newPassword);

	// 方法8-updateEmployeeUnpaidLeaveById: 透過員編編輯員工無薪假起始日期、結束日期與無薪假、留職停薪。
	public int updateEmployeeUnpaidLeaveById( //
			@Param("id") int id, //
			@Param("unpaidLeaveStartDate") LocalDate unpaidLeaveStartDate, //
			@Param("unpaidLeaveEndDate") LocalDate unpaidLeaveEndDate, //
			@Param("unpaidLeaveReason") String unpaidLeaveReason, //
			@Param("finalUpdateDate") LocalDate finalUpdateDate, //
			@Param("finalUpdateEmployeeId") int finalUpdateEmployeeId);

	// 方法9-getAllEmployees: 取得所有在職員工資料。
	public List<EmployeeDto> getAllEmployees();

	// 方法10-selectByEmployeeIdList: 透過員編查詢員工資料(可一次查詢多位員工資料)。
	public List<EmployeeDto> selectByEmployeeIdList(@Param("idList") List<Integer> idList);

	// 方法11-selectByEmployeeId: 透過員編查詢員工資料(檢查比對用)。
	public EmployeeDto selectByEmployeeId(@Param("id") int id);
	
	// 方法12-selectByEmployeeEmail: 透過員工信箱查詢員工資料(檢查比對用)。
	public EmployeeDto selectByEmployeeEmail(@Param("email") String email);

	// 方法13-countByEmail: 比對email信箱是否重複。
	public int countByEmail(@Param("email") String email);
	
	// 方法14-countById: 比對id員編是否存在。
	public int countById(@Param("id") int id);
	
	public void updateAnnualLeave(EmployeeDto dto);
	
	public void updateRemainingLeave(EmployeeDto dto);
	
	public EmployeeDto selectByEmailAndPhone(@Param("email") String email, @Param("phone") String phone);

	public int reinstatementEmployee(EmployeeDto dto);
}
