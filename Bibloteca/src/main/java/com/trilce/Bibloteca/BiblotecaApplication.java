package com.trilce.Bibloteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BiblotecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiblotecaApplication.class, args);
	}

}
