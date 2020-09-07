package com.consumerdataservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.consumerdataservice.dto.UserDetailsDto;
import com.consumerdataservice.service.ConsumerDataSaveAndUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@EnableJms
public class ConsumerDataListener {

	@Autowired
	ConsumerDataSaveAndUpdate service;

	private static final Logger log = LoggerFactory.getLogger(ConsumerDataListener.class);

	@Autowired
	DecryptionAESConfig decryption;
	
	@Value("${spring.encrypt.secret}")
	String secret;
	
	private ObjectMapper objectMapper = new ObjectMapper();

	@JmsListener(destination = "user-data-queue")
	public void listener(String userDTOJson) {
		try {
			log.info("Decrypted Message received {} ", userDTOJson);
			log.info("Decrypted Message received {} ", userDTOJson.length());
			userDTOJson=decryption.decrypt(userDTOJson, secret);
			UserDetailsDto userDtoList = objectMapper.readValue(userDTOJson, UserDetailsDto.class);
			log.info("Message received {} ", userDtoList);
			if ("save".equals(userDtoList.getOperation())) {
				log.info("Saving data");
				service.saveDataFile(userDtoList);
				
			} else if ("update".equals(userDtoList.getOperation())) {
				log.info("Updating data");
				service.updateDataFile(userDtoList);
			}

		} catch (Exception e) {
			log.error("Exception :: {} ", e);
		}
	}

}
