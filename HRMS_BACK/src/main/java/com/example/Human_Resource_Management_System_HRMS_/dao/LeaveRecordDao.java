package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveRecordDto;

@Mapper
public interface LeaveRecordDao {

	public void addRecord(LeaveRecordDto dto);

	public List<LeaveRecordDto> selectByLeaveIdList(@Param("leaveIdList") List<Integer> leaveIdList);

	public List<LeaveRecordDto> selectByEmployeeIdAndDate(//
			@Param("employeeId") int employeeId, //
			@Param("startTime") LocalDateTime startTime, //
			@Param("endTime") LocalDateTime endTime);

	public void updateRecordToCancel(//
			@Param("status") String status, //
			@Param("approved") Boolean approved, //
			@Param("approvedDateTime") LocalDateTime approvedDateTime, //
			@Param("approverId") int approverId, //
			@Param("leaveIdList") List<Integer> leaveIdList);
	
	// 新增: 當月請假成功的紀錄。
	public List<LeaveRecordDto> getApprovedMonthlyLeaveByEmpIdAndDate( //
			@Param("employeeId") int employeeId, //
			@Param("startDateOfMonth") LocalDateTime startDateOfMonth, //
			@Param("endDateOfMonth") LocalDateTime endDateOfMonth);

	public List<LeaveRecordDto> selectApprovedRecordByEmployeeIdAndYYMM(//
			@Param("employeeId") int employeeId, //
			@Param("startTime") LocalDateTime startTime, //
			@Param("endTime") LocalDateTime endTime);

	public List<LeaveRecordDto> selectByEmployeeIdAndApplyDateTime(//
			@Param("employeeId") int employeeId, //
			@Param("applyDateTime") LocalDateTime applyDateTime);
	
	public List<LeaveRecordDto> selectAllMonthlyApprovedRecordByYYMM(//
			@Param("startTime") LocalDateTime startTime, //
			@Param("endTime") LocalDateTime endTime);
}
