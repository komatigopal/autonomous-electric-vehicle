package com.mercedesbenz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStation implements Comparable<ChargingStation> {
	private String name;
	private long distance;
	private long limit;

	@Override
	public int compareTo(ChargingStation o) {
		if (this.distance > o.distance) {
			return 1;
		} else if (this.distance < o.distance) {
			return -1;
		} else {
			return 0;
		}
	}
}
