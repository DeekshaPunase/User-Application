package com.consumerinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan( basePackages = { "com.consumerinterface"} )
public class ConsumerInterfaceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerInterfaceServiceApplication.class, args);
	}

}
