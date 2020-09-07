package com.consumerinterface.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.consumerinterface.dto.UserDto;
import com.consumerinterface.service.ConsumerInterfaceService;
import com.consumerinterface.util.ConsumerInterfaceException;

@RestController
@RequestMapping("/user/details")
public class ConsumerInterfaceController {

	@Autowired
	ConsumerInterfaceService service;

	private static final Logger log= LoggerFactory.getLogger(ConsumerInterfaceService.class);
	
	@PostMapping(value = "/store")
	public ResponseEntity<String> storeUserData(@RequestBody List<UserDto> userDto,
			@RequestParam(value = "fileType") String fileType) {
		/**
		 * Validating the request body params
		 */
		userDto.stream().forEach(data -> {
			if (null==data.getUserId() || "".equals(data.getUserId()) || data.getUserId()<0){
				log.error("UserId is empty or null");
				throw new ConsumerInterfaceException("UserId not valid(Please enter ID greater than 0)");
			}
			if (null!=data.getFirstName() &&("".equals(data.getFirstName()) || !(data.getFirstName().getClass().getName() instanceof String))) {
				log.error("FirstName is empty or null");
				throw new ConsumerInterfaceException("FirstName not valid");
			}
			
			if (null!=data.getDob() && !(data.getDob().getClass().getName().contains("Date"))) {
				log.error("DOB is empty/null or invalid format");
				throw new ConsumerInterfaceException("Please enter correct date format");
			}
		});
		String msg = service.storeUserData(userDto, fileType);

		return new ResponseEntity<>(msg, HttpStatus.OK);
	}

	@GetMapping("/getUserDetails/{userId}")
	public UserDto storeData(@PathVariable("userId") Integer userId) {

		UserDto dto = service.getUserDetails(userId);
		return dto;
	}

	@PutMapping(value = "/update")
	public ResponseEntity<String> updateUserData(@RequestBody List<UserDto> userDto,
			@RequestParam(value = "fileType",required=true) String fileType) {

		String msg = service.updateUserData(userDto, fileType);

		return new ResponseEntity<>(msg, HttpStatus.OK);
	}

	@ExceptionHandler(value = ConsumerInterfaceException.class)
	public ResponseEntity<Object> exception(ConsumerInterfaceException exception) {
	        Map<String,String> errorMap = new HashMap<>();
	        errorMap.put("ErrorMessage",exception.getLocalizedMessage());
	        return new ResponseEntity<Object>(errorMap, HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
