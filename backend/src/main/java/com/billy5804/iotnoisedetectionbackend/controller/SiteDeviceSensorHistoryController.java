package com.billy5804.iotnoisedetectionbackend.controller;

import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.billy5804.iotnoisedetectionbackend.model.AuthUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceSensorHistoryOnlyTimestampAndValueAndSensorIdProjection;
import com.billy5804.iotnoisedetectionbackend.repository.SiteDeviceSensorHistoryRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@RestController
@CrossOrigin({ "http://localhost:3000", "http://localhost:5050" })
@RequestMapping(value = "/api/v1/site-device-sensor-history", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SiteDeviceSensorHistoryController {

	@Autowired
	private SiteDeviceSensorHistoryRepository siteDeviceSensorHistoryRepository;

	@Autowired
	private SiteUserRepository siteUserRepository;

	@GetMapping(params = { "deviceId", "sensorId", "siteId" })
	public ResponseEntity<Iterable<SiteDeviceSensorHistoryOnlyTimestampAndValueAndSensorIdProjection>> getSiteDeviceSensorHistory(
			@RequestParam(name = "deviceId") String deviceIdHex, @RequestParam int sensorId,
			@RequestParam UUID siteId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		final boolean isAuthorised = siteUserRepository
				.existsByIdAndAuthorised(new SiteUserPK(siteId, authUser.getId()));
		if (!isAuthorised) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		final byte[] deviceId = HexFormat.of().parseHex(deviceIdHex);
		return ResponseEntity.ok(siteDeviceSensorHistoryRepository
				.findBySiteDeviceSensorHistoryPKDeviceIdAndSiteDeviceSensorHistoryPKSensorIdAndSiteId(deviceId,
						sensorId, siteId));
	}
}
