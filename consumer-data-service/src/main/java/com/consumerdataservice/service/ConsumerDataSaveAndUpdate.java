package com.consumerdataservice.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.consumerdataservice.dto.UserDetailsDto;
import com.consumerdataservice.dto.UserDto;
import com.consumerdataservice.model.UserDetail;
import com.consumerdataservice.repository.ConsumerDataRepository;
import com.consumerdataservice.util.ConsumerDataListener;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.CSVReader;
import com.sun.xml.bind.v2.runtime.XMLSerializer;

@Service
public class ConsumerDataSaveAndUpdate {

	@Autowired
	ConsumerDataRepository repository;

	private static final Logger log = LoggerFactory.getLogger(ConsumerDataListener.class);

	private ModelMapper modelMapper = new ModelMapper();

	public void saveDataFile(UserDetailsDto userDtoList) {
		try {
			if ("CSV".equals(userDtoList.getFileType())) {

				CsvMapper mapper = new CsvMapper();
				CsvSchema schema = mapper.schemaFor(UserDto.class);
				schema = schema.withColumnSeparator(',').withHeader();

				ObjectWriter myObjectWriter = mapper.writer(schema);
				File directory = createDir("\\CSV");
				if (directory.exists()) {
					for (UserDto user : userDtoList.getUserDetails()) {
						log.info("File Created for user :: {} ", user.getUserId());
						File tempFile = new File("D:\\UserFiles\\CSV\\users_" + user.getUserId() + ".csv");
						tempFile.createNewFile();
						log.info("New File Created {} ", tempFile);
						FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(tempFileOutputStream,
								1024);
						OutputStreamWriter writerOutputStream = new OutputStreamWriter(bufferedOutputStream, "UTF-8");
						log.info("Writing data to file {} ", tempFile);
						myObjectWriter.writeValue(writerOutputStream, user);
						log.info("Data saved to file for user {} ", user.getUserId());

						// Save info to table for user

						UserDetail userDomain = new UserDetail();
						userDomain.setFileUrl(tempFile.getPath());
						userDomain.setUserId(user.getUserId());
						repository.saveAndFlush(userDomain);
						log.info("Data saved user table for user {} ", user.getUserId());
					}

				} else {
					log.info("No directory found !! Please create one");
				}

			} else if ("XML".equals(userDtoList.getFileType())) {
				File directory = createDir("\\XML");
				// XML creation
				if (directory.exists()) {
					for (UserDto user : userDtoList.getUserDetails()) {
						File xmlFile = new File("D:\\UserFiles\\XML\\users_" + user.getUserId() + ".xml");
						xmlFile.createNewFile();
						log.info("Writing data to xml file {} ", xmlFile);
						XmlMapper xmlMapper = new XmlMapper();
						xmlMapper.writeValue(xmlFile, user);
						log.info("Data saved to file {} ", xmlFile);

						UserDetail userDomain = new UserDetail();
						userDomain.setFileUrl(xmlFile.getPath());
						userDomain.setUserId(user.getUserId());
						repository.saveAndFlush(userDomain);
						log.info("Data saved user table for user {} ", user.getUserId());
					}
				} else {
					log.info("No directory found !! Please create one");
				}
			}
		} catch (Exception e) {
			log.error("Exception :: {} ", e);
		}
	}

	private File createDir(String type) {
		File directory = new File("D:\\UserFiles" + type);
		if (!directory.exists()) {
			if (directory.mkdirs()) {
				System.out.println("Directory is created");
			} else {
				System.out.println("Directory not created");
			}
		}
		return directory;
	}

	public void updateDataFile(UserDetailsDto userDtoList) {

		if (userDtoList.getFileType().equals("CSV")) {
			for (UserDto user : userDtoList.getUserDetails()) {

				String fileUrl = repository.findUrlByUserId(user.getUserId());

				File userFile = new File(fileUrl);

				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> userMap = objectMapper.convertValue(user, new TypeReference<Map<String, Object>>() {
				});
				Map<String, Object> allMap = new HashMap<String, Object>();
				List<String[]> csvBody = null;
				UserDto userData = new UserDto();

				log.info("userMap :: {} ", userMap);
				try(CSVReader reader = new CSVReader(new FileReader(userFile), ',')){

					csvBody = reader.readAll();

					String[] headers = csvBody.get(0);
					String[] values = csvBody.get(1);

					for (int i = 0; i < headers.length; i++) {
						allMap.put(headers[i], values[i]);
					}
					log.info("Before updated Json :: {} ", allMap);
					for (Map.Entry<String, Object> map : userMap.entrySet()) {
						for (String key : allMap.keySet()) {
							if (map.getKey().equals(key)) {
								allMap.put(key, map.getValue());
								break;
							}
						}
					}
					log.info("Updated Json :: {} ", allMap);
					userData = objectMapper.convertValue(allMap, UserDto.class);
					log.info("userData :: {} ", userData);
					reader.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				CsvMapper mapper = new CsvMapper();
				CsvSchema schema = mapper.schemaFor(UserDto.class);
				schema = schema.withColumnSeparator(',').withHeader();

				ObjectWriter myObjectWriter = mapper.writer(schema);
				try {
					myObjectWriter.writeValue(userFile, userData);
				} catch (JsonGenerationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else if (userDtoList.getFileType().equals("XML")) {
			
			for (UserDto user : userDtoList.getUserDetails()) {
				
				String fileUrl = repository.findUrlByUserId(user.getUserId());

				File userFile = new File(fileUrl);

				ObjectMapper objectMapper = new ObjectMapper();

				XmlMapper xmlMapper = new XmlMapper();
				String line = "", str = "";

				try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
					while ((line = br.readLine()) != null) {
						str += line;
					}

				Map<String, Object> userMap = objectMapper.convertValue(user, new TypeReference<Map<String, Object>>() {
				});
				
				UserDto userDto =  xmlMapper.readValue(str, UserDto.class);

				Map<String, Object> xmlmap = objectMapper.convertValue(userDto, new TypeReference<Map<String, Object>>() {
				});

				log.info("Before updated Json :: {} ", xmlmap);
				for (Map.Entry<String, Object> map : userMap.entrySet()) {
					for (String key : xmlmap.keySet()) {
						if (map.getKey().equals(key)) {
							xmlmap.put(key, map.getValue());
							break;
						}
					}
				}
				log.info("Updated Json :: {} ", xmlmap);
				UserDto userData = objectMapper.convertValue(xmlmap, UserDto.class);
				xmlMapper.writeValue(userFile, userData);
				log.info("User Data after updation :: {}",userData);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public UserDto getUserDetails(Integer userId) {

		String fileUrl = repository.findUrlByUserId(userId);

		File userFile = new File(fileUrl);

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, Object> allMap = new HashMap<String, Object>();
		UserDto userData = new UserDto();

		if (fileUrl.contains(".csv")) {

			List<String[]> csvBody = null;

			try (CSVReader reader = new CSVReader(new FileReader(userFile), ',')) {

				csvBody = reader.readAll();

				String[] headers = csvBody.get(0);
				String[] values = csvBody.get(1);

				for (int i = 0; i < headers.length; i++) {
					allMap.put(headers[i], values[i]);
				}
				userData = objectMapper.convertValue(allMap, UserDto.class);
				log.info("userData :: {} ", userData);
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (fileUrl.contains(".xml")) {
			XmlMapper xmlMapper = new XmlMapper();
			String line = "", str = "";

			try (BufferedReader br = new BufferedReader(new FileReader(fileUrl))) {
				while ((line = br.readLine()) != null) {
					str += line;
				}
				userData = xmlMapper.readValue(str, UserDto.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return userData;
	}

}
