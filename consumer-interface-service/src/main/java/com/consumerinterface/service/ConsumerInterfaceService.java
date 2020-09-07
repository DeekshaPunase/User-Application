package com.consumerinterface.service;

import java.util.List;

import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.consumerinterface.dto.UserDetailsDto;
import com.consumerinterface.dto.UserDto;
import com.consumerinterface.util.EncryptionAESConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConsumerInterfaceService {

	@Autowired
    private Queue queue;

	@Autowired 
	private JmsTemplate jmsTemplate;
	
	@Autowired
	EncryptionAESConfig encryption;
	
	@Value("${spring.encrypt.secret}")
	String secret;
	
	@Value("${consumer.url}")
	String consumerUrl;
	
	private ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger log= LoggerFactory.getLogger(ConsumerInterfaceService.class);
	
	public String storeUserData(List<UserDto> userData, String fileType) {
		
			try {
				UserDetailsDto userDetailsDto = new UserDetailsDto();
				userDetailsDto.setFileType(fileType);
				userDetailsDto.setUserDetails(userData);
				userDetailsDto.setOperation("save");
				String userDataStr = encryption.encrypt(objectMapper.writeValueAsString(userDetailsDto),secret);

				log.info("encrytped Message received {} ", userDataStr.length());
				log.info("UserData :: "+userDataStr);
				log.info(":: Sending data to consumer service SAVE  ::");
		        jmsTemplate.convertAndSend(queue, userDataStr);
			    log.info("User Data Successfully Pushed into Queue for consumer-service");
		        
			} catch (Exception e) {
				log.info(":: Exception occurred while sending data to consumer service :: "+e);
			} 
		
		return "User Data Stored Successfully";
	}

	public String updateUserData(List<UserDto> userDto, String fileType) {
		
		try {
			UserDetailsDto userDetailsDto = new UserDetailsDto();
			userDetailsDto.setFileType(fileType);
			userDetailsDto.setUserDetails(userDto);
			userDetailsDto.setOperation("update");
			String userDataStr = encryption.encrypt(objectMapper.writeValueAsString(userDetailsDto),secret);
		
			log.info("UserData :: "+userDataStr);
			log.info(":: Sending data to consumer service UPDATE :: ");
	        jmsTemplate.convertAndSend(queue, userDataStr);
		    log.info("User Data Successfully Pushed into Queue for consumer-service");
	        
		} catch (Exception e) {
			log.info(":: Exception occurred while sending data to consumer service :: "+e);
		} 
		return "User Data Updated Successfully";
	}

	public UserDto getUserDetails(Integer userId) {
	
		RestTemplate restTemplate = new RestTemplate();
		log.info("Calling consumer :: {} ",consumerUrl+userId); 
		ResponseEntity<String> result = restTemplate.getForEntity(consumerUrl+userId, String.class);
		
		UserDto user =null;
		try {
			log.info("Decrypted result :: {} ",result.getBody());
			String userDataStr = encryption.decrypt(result.getBody(),secret);
			log.info("result.getBody() :: {} ",userDataStr);
			user = objectMapper.readValue(userDataStr,UserDto.class);
			log.info("user :: {} ",user);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
	}

	
}
