package com.mercedesbenz.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercedesbenz.model.Error;
import com.mercedesbenz.model.Request;
import com.mercedesbenz.service.WebServiceImpl;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class WebController {

	@Autowired
	WebServiceImpl webService;

	@GetMapping(value = "/")
	public LinkedHashMap<String, Object> request(@RequestBody Request request) {
		log.info("Request at Controller Layer request - " + request);
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			response = webService.request(request);
		} catch (Exception e) {
			List<Error> errors = new ArrayList<Error>();
			errors.add(Error.builder().id(9999).description("Technical Exception").build());
			response.put("errors", errors);
			e.printStackTrace();
		}
		return response;
	}
}
