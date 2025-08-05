package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeApplicationRecordDto;

@Mapper
public interface EmployeeApplicationRecordDao {

	public void insertApplicationRecord(EmployeeApplicationRecordDto dto);

	public List<EmployeeApplicationRecordDto> searchRecordList(@Param("employeeId") int employeeId);
}
