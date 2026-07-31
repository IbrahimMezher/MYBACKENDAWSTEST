package com.flutterbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlutterbackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(FlutterbackendApplication.class, args);
	}
}
