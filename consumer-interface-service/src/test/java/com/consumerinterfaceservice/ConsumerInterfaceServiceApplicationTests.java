package com.consumerinterfaceservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.consumerinterface.ConsumerInterfaceServiceApplication;
import com.consumerinterface.dto.UserDto;
import com.consumerinterface.service.ConsumerInterfaceService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {ConsumerInterfaceServiceApplication.class})
@AutoConfigureMockMvc
public class ConsumerInterfaceServiceApplicationTests {

		@Autowired
		private MockMvc mockMvc;

		@MockBean
		private ConsumerInterfaceService service;

		private ObjectMapper mapper = new ObjectMapper();
		
		private static final Logger log= LoggerFactory.getLogger(ConsumerInterfaceServiceApplicationTests.class);
		
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

			this.mockMvc.perform(get("/user/details/getUserDetails/1")
					.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
			}

		@Test
		public void saveUserData() throws Exception {
			List<UserDto> userDto = new ArrayList<>();
			UserDto user = new UserDto();
			user.setUserId(1);
			user.setFirstName("John");
			user.setLastName("Keil");
			user.setAge(26);
			user.setSalary(new BigDecimal(10000.00));
			userDto.add(user);
			log.info("User dto :: {} ",mapper.writeValueAsString(userDto));
			String msg = "User Data Stored Successfully";
			
			String fileType="XML";
			Mockito.when(service.storeUserData(Mockito.anyList(), Mockito.anyString())).thenReturn(msg);

			this.mockMvc.perform(post("/user/details/store?fileType="+fileType).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userDto))).andExpect(status().isOk());
			
		}

		@Test
		public void updateUserData() throws Exception {
			List<UserDto> userDto = new ArrayList<>();
			UserDto user = new UserDto();
			user.setUserId(1);
			user.setFirstName("JohnUpdated");
			user.setLastName("Keil");
			userDto.add(user);
			log.info("User dto :: {} ",mapper.writeValueAsString(userDto));
			String msg = "User Data Updated Successfully";
			
			String fileType="XML";
			Mockito.when(service.updateUserData(Mockito.anyList(), Mockito.anyString())).thenReturn(msg);

			this.mockMvc.perform(put("/user/details/update?fileType="+fileType).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userDto))).andExpect(status().isOk());
			
		}

		@Test
		public void saveUserData_useridfail() throws Exception {
			List<UserDto> userDto = new ArrayList<>();
			UserDto user = new UserDto();
			user.setUserId(null);
			user.setFirstName("JohnUpdated");
			user.setLastName("Keil");
			userDto.add(user);
			userDto.add(user);
			log.info("User dto :: {} ",mapper.writeValueAsString(userDto));
			String msg = "UserId not valid(Please enter ID greater than 0)";
			
			String fileType="XML";
			Mockito.when(service.storeUserData(Mockito.anyList(), Mockito.anyString())).thenReturn(msg);

			this.mockMvc.perform(post("/user/details/store?fileType="+fileType)
					.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(userDto)))
					.andExpect(status().isUnprocessableEntity());
			
		}

}
