package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.AnnualHolidayDto;

@Mapper
public interface AnnualHolidayDao {

	public void addAnnualHoliday(@Param("holidayList") List<AnnualHolidayDto> holidayList);
	
	public List<AnnualHolidayDto> getAllAnnualHoliday();
	
	public int updateAnnualHoliday(AnnualHolidayDto dto);
	
	public int deleteAnnualHoliday(@Param("date") LocalDate date);
	
	public void deleteAllAnnualHoliday();

}
