package com.finalpos.POSsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PoSsystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(PoSsystemApplication.class, args);
	}
}

@RestController
class DemoController {
	@GetMapping("/hello")
	public String hello() {
		return "Hello, You are staying on Render Server.";
	}
}