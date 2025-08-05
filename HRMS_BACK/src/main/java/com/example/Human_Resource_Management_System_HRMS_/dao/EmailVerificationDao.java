package com.example.Human_Resource_Management_System_HRMS_.dao;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.Human_Resource_Management_System_HRMS_.dto.EmailVerificationDto;

@Mapper
public interface EmailVerificationDao {

	public EmailVerificationDto selectByEmail(String email);

	public void insertOrUpdate(//
			@Param("email") String email, //
			@Param("code") String code, //
			@Param("expireAt") LocalDateTime expireAt);

	public void updateVerified(@Param("email") String email);

	public void deleteAll();
}
