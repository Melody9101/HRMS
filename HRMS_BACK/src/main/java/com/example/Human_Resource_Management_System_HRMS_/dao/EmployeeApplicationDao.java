package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeApplicationDto;

@Mapper
public interface EmployeeApplicationDao {

	public void insertApplication(EmployeeApplicationDto dto);

	public void updateApplicationGroupIdById(//
			@Param("id") int id, //
			@Param("applicationGroup") int applicationGroup);

	public List<EmployeeApplicationDto> checkAllUpdateApplicationList();

	public EmployeeApplicationDto getByApplicationId(@Param("id") int id);

	public void deleteByApplicationId(@Param("id") int id);
}
