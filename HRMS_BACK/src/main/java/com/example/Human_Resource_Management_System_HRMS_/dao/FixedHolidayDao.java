package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.FixedHolidayDto;

@Mapper
public interface FixedHolidayDao {

	public void insertFixedHoliday(@Param("dtoList") List<FixedHolidayDto> dtoList);

	public List<FixedHolidayDto> getAllFixedHoliday();

	public int deleteHoliday(//
			@Param("month") int month, //
			@Param("day") int day);
}
