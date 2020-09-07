package com.consumerdataservice.dto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan( basePackages = { "com.consumerdataservice"})
public class ConsumerDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerDataServiceApplication.class, args);
	}

}
