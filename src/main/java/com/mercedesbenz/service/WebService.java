package com.mercedesbenz.service;

import java.util.LinkedHashMap;

import com.mercedesbenz.model.Request;

public interface WebService {
	public LinkedHashMap<String, Object> request(Request request);
}
