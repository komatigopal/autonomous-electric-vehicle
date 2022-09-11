package com.mercedesbenz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeLevelResponse {
	private String vin;
	private int currentChargeLevel;
	private String error;
}
