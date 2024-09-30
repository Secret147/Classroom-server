package com.project.classroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ClassroomProjectApplication {
	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(ClassroomProjectApplication.class, args);
	}

}
