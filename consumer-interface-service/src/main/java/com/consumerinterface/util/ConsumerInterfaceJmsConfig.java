package com.consumerinterface.util;

import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
public class ConsumerInterfaceJmsConfig {

	private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";

	@Bean
	public Queue queue(){
	return new ActiveMQQueue("user-data-queue");
	}

	@Bean
	public ActiveMQConnectionFactory connectionFactory(){
	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
	connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
	return connectionFactory;
	}

}
