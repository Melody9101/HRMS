package com.example.Human_Resource_Management_System_HRMS_.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")//
				.allowedOrigins("http://localhost:4200")//
				.allowedMethods("*")//
				.allowedHeaders("*")//
				// 這行讓 cookie (session) 被接受
				.allowCredentials(true);
	}
}