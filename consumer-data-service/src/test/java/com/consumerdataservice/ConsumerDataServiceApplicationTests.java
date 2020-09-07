package com.consumerdataservice;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.consumerdataservice.dto.UserDto;
import com.consumerdataservice.service.ConsumerDataSaveAndUpdate;
import com.consumerdataservice.util.DecryptionAESConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {ConsumerDataServiceApplication.class})
@AutoConfigureMockMvc
public class ConsumerDataServiceApplicationTests {

	@Value("${spring.encrypt.secret}")
	String secret;
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ConsumerDataSaveAndUpdate service;

	@Autowired
	DecryptionAESConfig decryption;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private static final Logger log= LoggerFactory.getLogger(ConsumerDataServiceApplicationTests.class);
	
	@Test
	public void getUserData() throws Exception {
		UserDto user = new UserDto();
		user.setUserId(1);
		user.setFirstName("John");
		user.setLastName("Keil");
		user.setAge(26);
		user.setSalary(new BigDecimal(10000.00));
		log.info("User dto :: {} ",mapper.writeValueAsString(user));
		Mockito.when(service.getUserDetails(Mockito.anyInt())).thenReturn(user);
	
		String expceted=decryption.encrypt(mapper.writeValueAsString(user),secret);
		log.info("test ecnryption:: {}",expceted);
		String output = this.mockMvc.perform(get("/getUserDetails/1")
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();
		
		assertEquals(expceted,output);
	}

}
