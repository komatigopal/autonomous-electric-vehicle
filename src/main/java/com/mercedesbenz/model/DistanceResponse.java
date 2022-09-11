package com.mercedesbenz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistanceResponse {
	private String source;
	private String destination;
	private long distance;
	private String error;
}
