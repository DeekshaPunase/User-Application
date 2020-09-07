package com.consumerinterface.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsDto {

	List<UserDto> userDetails = new ArrayList<UserDto>();
	
	String fileType;

	String operation;
	
	public List<UserDto> getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(List<UserDto> userDetails) {
		this.userDetails = userDetails;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	
}
