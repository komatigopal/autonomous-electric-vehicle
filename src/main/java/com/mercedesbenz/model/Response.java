package com.mercedesbenz.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {
	private long transactionId;
	private String vin;
	private String source;
	private String destination;
	private long distance;
	private int currentChargeLevel;
	private boolean isChargingRequired;
	private List<Error> errors;
}
