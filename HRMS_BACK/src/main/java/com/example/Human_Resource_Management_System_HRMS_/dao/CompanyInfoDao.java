package com.example.Human_Resource_Management_System_HRMS_.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.Human_Resource_Management_System_HRMS_.dto.CompanyInfoDto;

@Mapper
public interface CompanyInfoDao {

	// 更新公司資訊
	public void updateCompanyInfo(CompanyInfoDto dto);

	// 查詢公司資訊
	public CompanyInfoDto checkCompanyInfo();
}
