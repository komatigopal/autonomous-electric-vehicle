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
public class ChargingStationsResponse {
	private String source;
	private String destination;
	private List<ChargingStation> chargingStations;
	private String error;
}
