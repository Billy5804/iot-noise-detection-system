package com.billy5804.iotnoisedetectionbackend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
import com.billy5804.iotnoisedetectionbackend.model.SiteDeviceSensorHistory;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceSensorHistroyExculdeSiteDeviceAndSiteProjection;
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

	@GetMapping
	public ResponseEntity<Iterable<SiteDeviceSensorHistroyExculdeSiteDeviceAndSiteProjection>> getSitesSiteUsers(
			@RequestParam byte[] deviceId, @RequestParam int sensorId, @RequestParam UUID siteId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		final boolean isAuthorised = siteUserRepository
				.existsByIdAndAuthorised(new SiteUserPK(siteId, authUser.getId()));
		if (!isAuthorised) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		final SiteDeviceSensorHistory siteDeviceSensorHistory = new SiteDeviceSensorHistory();
		siteDeviceSensorHistory.setDeviceId(deviceId);
		siteDeviceSensorHistory.setSensorId(sensorId);
		siteDeviceSensorHistory.setSiteId(siteId);
		return ResponseEntity.ok(siteDeviceSensorHistoryRepository.findAll(Example.of(siteDeviceSensorHistory)));
	}
}
