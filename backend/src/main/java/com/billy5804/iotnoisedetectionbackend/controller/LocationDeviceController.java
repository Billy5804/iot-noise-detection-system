package com.billy5804.iotnoisedetectionbackend.controller;

import java.util.HexFormat;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.billy5804.iotnoisedetectionbackend.helper.updateHelper;
import com.billy5804.iotnoisedetectionbackend.model.AuthUser;
import com.billy5804.iotnoisedetectionbackend.model.Location;
import com.billy5804.iotnoisedetectionbackend.model.LocationDevice;
import com.billy5804.iotnoisedetectionbackend.model.LocationDevicePK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.repository.LocationDeviceRepository;
import com.billy5804.iotnoisedetectionbackend.repository.LocationRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@RestController
@CrossOrigin({ "http://localhost:3000", "http://localhost:5050" })
@RequestMapping(value = "/api/v1/location-devices", produces = { MediaType.APPLICATION_JSON_VALUE })
public class LocationDeviceController {

	@Autowired
	private LocationDeviceRepository locationDeviceRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private SiteUserRepository siteUserRepository;

	@GetMapping
	public ResponseEntity<Iterable<LocationDevice>> getLocationDevices(@RequestParam UUID locationId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		Location location = null;
		try {
			location = locationRepository.findById(locationId).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		final boolean isAuthorised = siteUserRepository
				.existsByIdAndAuthorised(new SiteUserPK(location.getSiteId(), authUser.getId()));
		if (!isAuthorised) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		return ResponseEntity.ok(locationDeviceRepository.findByLocationDevicePKLocationId(location.getId()));
	}

	@PutMapping
	public ResponseEntity<LocationDevice> updateLocationDevice(@RequestBody LocationDevice updateLocationDevice) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		LocationDevice currentLocationDevice = null;
		try {
			currentLocationDevice = locationDeviceRepository.findById(updateLocationDevice.getLocationDevicePK()).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(currentLocationDevice.getSiteId(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER && authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		updateLocationDevice.setSiteId(currentLocationDevice.getSiteId());
		updateHelper.copyNonNullProperties(updateLocationDevice, currentLocationDevice);

		return ResponseEntity.ok(locationDeviceRepository.save(currentLocationDevice));
	}

	@PostMapping
	public ResponseEntity<LocationDevice> createLocationDevice(@RequestBody LocationDevice newLocationDevice) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		Location location = null;
		try {
			location = locationRepository.findById(newLocationDevice.getLocationId()).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(location.getSiteId(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER && authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		newLocationDevice.setSiteId(location.getSiteId());

		return ResponseEntity.ok(locationDeviceRepository.save(newLocationDevice));
	}

	@DeleteMapping
	public ResponseEntity<Object> deleteLocation(@RequestParam UUID locationId, @RequestParam String deviceId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		LocationDevice locationDevice = null;
		try {
			final byte[] deviceIdBytes = HexFormat.of().parseHex(deviceId);
			locationDevice = locationDeviceRepository.findById(new LocationDevicePK(locationId, deviceIdBytes)).get();
		} catch (NoSuchElementException e) {
			// Should be fine if this is thrown as record doesn't exist which is the outcome
			// we wanted anyway.
			e.printStackTrace();
			return ResponseEntity.ok(null);
		}

		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(locationDevice.getSiteId(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER && authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		locationDeviceRepository.delete(locationDevice);
		return ResponseEntity.ok(null);
	}
}
