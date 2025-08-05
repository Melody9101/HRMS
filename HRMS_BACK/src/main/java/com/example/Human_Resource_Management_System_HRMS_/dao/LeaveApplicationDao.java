package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveApplicationDto;

@Mapper
public interface LeaveApplicationDao {

	public void insertLeaveApplication(//
			@Param("employerId") int employerId, //
			@Param("employeeName") String employeeName, //
			@Param("startTime") LocalDateTime startTime, //
			@Param("endTime") LocalDateTime endTime, //
			@Param("leaveType") String leaveType, //
			@Param("certificate") byte[] certificate, //
			@Param("applyDateTime") LocalDateTime applyDateTime, //
			@Param("reason") String reason, //
			@Param("status") String status, //
			@Param("approvalPendingRole") String approvalPendingRole, //
			@Param("submitUp") Boolean submitUp, //
			@Param("certificateFileType") String certificateFileType);

	public void updateStatusByLeaveId(@Param("leaveId") int leaveId, @Param("status") String status);

	public LeaveApplicationDto selectByLeaveId(@Param("leaveId") int leaveId);

	public int deleteLeaveApplicationByLeaveId(@Param("leaveId") int leaveId);

	public int updateByLeaveId(//
			@Param("leaveId") int leaveId, //
			@Param("certificate") byte[] certificate, // //
			@Param("status") String status, //
			@Param("certificateFileType") String certificateFileType);

	public List<LeaveApplicationDto> selectByEmployeeIdAndDate(//
			@Param("employerId") int employerId, //
			@Param("startTime") LocalDateTime startTime, //
			@Param("endTime") LocalDateTime endTime);

	public List<LeaveApplicationDto> selectByLeaveIdList(@Param("leaveIdList") List<Integer> leaveIdList);

	public List<LeaveApplicationDto> selectAll();
}
