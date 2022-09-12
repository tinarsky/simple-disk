package com.github.tinarsky.simpledisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:db.properties")
public class SimpleDiskApplication {
	public static void main(String[] args) {
		SpringApplication.run(SimpleDiskApplication.class, args);
	}
}
