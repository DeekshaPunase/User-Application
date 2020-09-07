package com.consumerdataservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.consumerdataservice.dto.UserDto;
import com.consumerdataservice.service.ConsumerDataSaveAndUpdate;
import com.consumerdataservice.util.DecryptionAESConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/getUserDetails")
public class ConsumerController {
	
	@Value("${spring.encrypt.secret}")
	String secret;
	
	@Autowired
	ConsumerDataSaveAndUpdate service;
	
	@Autowired
	DecryptionAESConfig decryption;

	private static final Logger log= LoggerFactory.getLogger(ConsumerController.class);

	
	@GetMapping("/{userId}")
	public String storeData(@PathVariable("userId") Integer userId) {
		ObjectMapper objectMapper = new ObjectMapper();
		UserDto dto = service.getUserDetails(userId);
		String result = null;
		try {
			result = decryption.encrypt(objectMapper.writeValueAsString(dto), secret);
			log.info("Encrypted data :: {} ",result);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result; 
	}
}
