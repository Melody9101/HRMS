package com.example.Human_Resource_Management_System_HRMS_;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class HumanResourceManagementSystemHrmsApplicationTests {

	@Test
	void contextLoads() {
		LocalDate yesterday = LocalDate.now().minusDays(1);

		LocalDateTime yesterdayStartTime = yesterday.atStartOfDay();
		LocalDateTime yesterdayEndTime = yesterday.atTime(23, 59, 59);

		System.out.println(yesterdayStartTime);
		System.out.println(yesterdayEndTime);
	}

	@Test
	void test() {
		boolean A = true;
		boolean B = true;
		boolean C = false;
		boolean D = false;

		if (A && B) {
			System.out.println("AB");
		}

		if (A && C) {
			System.out.println("AC");
		}
	}
}
