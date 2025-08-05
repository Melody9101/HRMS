package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.AttendanceDto;

@Mapper
public interface AttendanceDao {

	/**
	 * 透過員編新增上班打卡時間
	 * 
	 * @param employeeId
	 * @param clockIn
	 */
	public void clockIn(@Param("employeeId") int employeeId, @Param("clockIn") LocalDateTime clockIn);

	/**
	 * 透過id更新下班時間
	 * 
	 * @param id
	 * @param clockOut
	 */
	public void clockOutById(@Param("id") int id, @Param("clockOut") LocalDateTime clockOut);

	/**
	 * 透過員編新增下班打卡時間
	 * 
	 * @param employeeId
	 * @param clockOut
	 */
	public void clockOut(@Param("employeeId") int employeeId, @Param("clockOut") LocalDateTime clockOut);

	/**
	 * 透過員編查詢當天打卡紀錄
	 * 
	 * @param employeeId
	 * @return
	 */
	public AttendanceDto findTodayClockInRecordsByEmployeeId(@Param("employeeId") int employeeId);

	/**
	 * 
	 * @param employeeId
	 * @return
	 */
	public AttendanceDto findTodayClockOutRecordsByEmployeeId(@Param("employeeId") int employeeId);

	/**
	 * 透過員工編號查詢期限內打卡紀錄
	 * 
	 * @param employeeId
	 * @return
	 */
	public List<AttendanceDto> selectByEmployeeIdAndDate(//
			@Param("employeeId") int employeeId, //
			@Param("startTime") LocalDateTime startTime, //
			@Param("endTime") LocalDateTime endTime);

	/**
	 * 透過日期時間查詢所有人打卡紀錄
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<AttendanceDto> selectAllAttendanceByDate(//
			@Param("startTime") LocalDateTime startTime, //
			@Param("endTime") LocalDateTime endTime);

	/**
	 * 透過id更新上下班打卡時間
	 * 
	 * @param id
	 * @param clockIn
	 * @param clockOut
	 */
	public int updateClockTime(//
			@Param("id") int id, //
			@Param("clockIn") LocalDateTime clockIn, //
			@Param("clockOut") LocalDateTime clockOut, //
			@Param("finalUpdateDate") LocalDate finalUpdateDate, //
			@Param("finalUpdateEmployeeId") int finalUpdateEmployeeId);

	/**
	 * 透過id查詢打卡紀錄
	 * 
	 * @param id
	 * @return
	 */
	public AttendanceDto selectAttendanceById(@Param("id") int id);

}
