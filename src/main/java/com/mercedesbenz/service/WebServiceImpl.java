package com.mercedesbenz.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mercedesbenz.model.ChargeLevelRequest;
import com.mercedesbenz.model.ChargeLevelResponse;
import com.mercedesbenz.model.ChargingStation;
import com.mercedesbenz.model.ChargingStationsRequest;
import com.mercedesbenz.model.ChargingStationsResponse;
import com.mercedesbenz.model.DistanceRequest;
import com.mercedesbenz.model.DistanceResponse;
import com.mercedesbenz.model.Error;
import com.mercedesbenz.model.Request;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class WebServiceImpl implements WebService {
	private static long transactionId = 1;
	RestTemplate restTemplate = new RestTemplate();
	List<ChargingStation> chargingStationList = new ArrayList<ChargingStation>();
	List<ChargingStation> actualChargingStationList = new ArrayList<ChargingStation>();
	long distance = 0, currentChargeLevel = 0, sumDistance = 0;

	@Value("${mercedesbenz.get.charge_level}")
	private String getChargeLevelUrl;

	@Value("${mercedesbenz.get.travel.distance}")
	private String getDistanceUrl;

	@Value("${mercedesbenz.get.charging_stations}")
	private String getChargingStationsUrl;

	public LinkedHashMap<String, Object> request(Request request) {
		log.info("Request at Service Layer request - " + request);
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
		response.put("transactionId", transactionId++);
		try {
			String vin = request.getVin();
			String destination = request.getDestination();
			String source = request.getSource();
			ChargeLevelRequest chargeLevelRequest = ChargeLevelRequest.builder().vin(vin).build();
			ChargeLevelResponse chargeLevelResponse = this.getChargeLevel(chargeLevelRequest);
			DistanceRequest distanceRequest = DistanceRequest.builder().destination(destination).source(source).build();
			DistanceResponse distanceResponse = this.getDistance(distanceRequest);
			boolean isChargingRequired = false;
			if (null == chargeLevelResponse.getError() && null == distanceResponse.getError()) {
				long currentChargeLevel = chargeLevelResponse.getCurrentChargeLevel();
				long distance = distanceResponse.getDistance();
				response.put("vin", vin);
				response.put("source", source);
				response.put("destination", destination);
				response.put("distance", distance);
				response.put("currentChargeLevel", currentChargeLevel);
				response.put("isChargingRequired", isChargingRequired);
				if (currentChargeLevel < distance) {
					isChargingRequired = true;
					response.put("isChargingRequired", isChargingRequired);
					ChargingStationsRequest chargingStationsRequest = ChargingStationsRequest.builder()
							.destination(destination).source(source).build();
					ChargingStationsResponse chargingStationsResponse = this
							.getChargingStations(chargingStationsRequest);
					log.info("chargingStationsResponse - " + chargingStationsResponse);
					if (chargingStationsResponse.getChargingStations().size() > 0) {
						chargingStationList = new ArrayList<ChargingStation>();
						this.distance = distance;
						this.currentChargeLevel = currentChargeLevel;
						this.sumDistance = 0;
						actualChargingStationList = chargingStationsResponse.getChargingStations();
						chargingStationList = this.getChargingStationList(actualChargingStationList);
						if (chargingStationList.size() > 0) {
							Collections.sort(chargingStationList);
							response.put("chargingStations", chargingStationList);
						} else {
							List<Error> errors = new ArrayList<Error>();
							errors.add(Error.builder().id(8888)
									.description("Unable to reach the destination with the current fuel level")
									.build());
							response.put("errors", errors);
						}
					} else {
						List<Error> errors = new ArrayList<Error>();
						errors.add(Error.builder().id(8888)
								.description("Unable to reach the destination with the current fuel level").build());
						response.put("errors", errors);
					}
				}
			} else {
				List<Error> errors = new ArrayList<Error>();
				errors.add(Error.builder().id(9999).description("Technical Exception").build());
				response.put("errors", errors);
			}
		} catch (Exception e) {
			List<Error> errors = new ArrayList<Error>();
			errors.add(Error.builder().id(9999).description("Technical Exception").build());
			response.put("errors", errors);
			e.printStackTrace();
		}
		log.info("response - " + response);
		return response;
	}

	private List<ChargingStation> getChargingStationList(List<ChargingStation> chargingStations) {
		log.info("in getChargingStationList method chargingStations - " + chargingStations);
		int size = chargingStations.size();
		if (size > 0) {
			for (int i = 0; i < size;) {
				ChargingStation chargingStation = chargingStations.get(i);
				this.currentChargeLevel += sumDistance;
				this.sumDistance = chargingStation.getDistance();
				this.currentChargeLevel -= chargingStation.getDistance();
				if (this.currentChargeLevel >= 0) {
					if (this.distance <= chargingStation.getLimit()) {
						chargingStationList = new ArrayList<ChargingStation>();
						chargingStationList.add(chargingStation);
						return chargingStationList;
					} else {
						if (currentChargeLevel + sumDistance >= this.distance) {
							return chargingStationList;
						} else {
							chargingStationList.add(chargingStation);
							this.currentChargeLevel += chargingStation.getLimit();
							chargingStations.remove(i);
							return this.getChargingStationList(chargingStations);
						}
					}
				} else {
					chargingStationList = new ArrayList<ChargingStation>();
					actualChargingStationList.remove(i);
					return this.getChargingStationList(this.actualChargingStationList);
				}
			}
		} else {
			chargingStationList = new ArrayList<ChargingStation>();
			return chargingStationList;
		}
		return chargingStationList;
	}

	private DistanceResponse getDistance(DistanceRequest distanceRequest) {
		log.info("in getDistance method distanceRequest - " + distanceRequest);
		DistanceResponse distanceResponse = null;
		try {
			distanceResponse = restTemplate.postForObject(getDistanceUrl, distanceRequest, DistanceResponse.class);
			log.info("distanceResponse  - " + distanceResponse);
		} catch (Exception e) {
			distanceResponse = new DistanceResponse();
			e.printStackTrace();
		}
		return distanceResponse;
	}

	private ChargeLevelResponse getChargeLevel(ChargeLevelRequest chargeLevelRequest) {
		log.info("in getChargeLevel method chargeLevelRequest - " + chargeLevelRequest);
		ChargeLevelResponse chargeLevelResponse = null;
		try {
			chargeLevelResponse = restTemplate.postForObject(getChargeLevelUrl, chargeLevelRequest,
					ChargeLevelResponse.class);
			log.info("chargeLevelResponse  - " + chargeLevelResponse);
		} catch (Exception e) {
			chargeLevelResponse = new ChargeLevelResponse();
			e.printStackTrace();
		}
		return chargeLevelResponse;
	}

	private ChargingStationsResponse getChargingStations(ChargingStationsRequest chargingStationsRequest) {
		log.info("in getChargingStations method chargingStationsRequest - " + chargingStationsRequest);
		ChargingStationsResponse chargingStationsResponse = null;
		try {
			chargingStationsResponse = restTemplate.postForObject(getChargingStationsUrl, chargingStationsRequest,
					ChargingStationsResponse.class);
			log.info("chargingStationsResponse   - " + chargingStationsResponse);
			List<ChargingStation> chargeStations = chargingStationsResponse.getChargingStations();
			Collections.sort(chargeStations);
			chargingStationsResponse.setChargingStations(chargeStations);
		} catch (Exception e) {
			chargingStationsResponse = new ChargingStationsResponse();
			e.printStackTrace();
		}
		return chargingStationsResponse;
	}

}
