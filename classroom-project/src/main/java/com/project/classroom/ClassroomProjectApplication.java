package com.project.classroom;

import com.project.classroom.schedule.TaskCheck;
import com.project.classroom.security.jwt.AuthTokenFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ClassroomProjectApplication implements CommandLineRunner {


	private static final Logger logger = LoggerFactory.getLogger(ClassroomProjectApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ClassroomProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Server start...");
		TaskCheck.runTask();
	}
}
