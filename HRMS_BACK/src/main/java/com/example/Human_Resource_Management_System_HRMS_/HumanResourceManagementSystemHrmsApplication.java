package com.example.Human_Resource_Management_System_HRMS_;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class HumanResourceManagementSystemHrmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HumanResourceManagementSystemHrmsApplication.class, args);
	}

}
