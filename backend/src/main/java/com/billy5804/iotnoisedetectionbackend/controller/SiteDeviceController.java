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
import com.billy5804.iotnoisedetectionbackend.model.SiteDevice;
import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceExpandDeviceExcludeSiteProjection;
import com.billy5804.iotnoisedetectionbackend.repository.DeviceRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteDeviceRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@RestController
@CrossOrigin({ "http://localhost:3000", "http://localhost:5050" })
@RequestMapping(value = "/api/v1/site-devices", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SiteDeviceController {

	@Autowired
	private SiteDeviceRepository siteDeviceRepository;

	@Autowired
	private SiteUserRepository siteUserRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;

	@GetMapping
	public ResponseEntity<Iterable<SiteDeviceExpandDeviceExcludeSiteProjection>> getSiteDevices(
			@RequestParam UUID siteId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		final boolean isAuthorised = siteUserRepository
				.existsByIdAndAuthorised(new SiteUserPK(siteId, authUser.getId()));
		if (!isAuthorised) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		return ResponseEntity.ok(siteDeviceRepository.getExpandedDeviceBySiteId(siteId));
	}

	@PutMapping
	public ResponseEntity<SiteDeviceExpandDeviceExcludeSiteProjection> updateSiteDevice(@RequestBody SiteDevice updateSiteDevice) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(updateSiteDevice.getSite(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER && authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		SiteDevice currentSiteDevice = null;
		try {
			currentSiteDevice = siteDeviceRepository.findById(updateSiteDevice.getSiteDevicePK()).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		updateHelper.copyNonNullProperties(updateSiteDevice, currentSiteDevice);
		siteDeviceRepository.save(currentSiteDevice);
		
		return ResponseEntity.ok(siteDeviceRepository.findBySiteDevicePK(currentSiteDevice.getSiteDevicePK()));
	}

	@PostMapping
	public ResponseEntity<SiteDeviceExpandDeviceExcludeSiteProjection> createSiteUser(@RequestBody SiteDevice newSiteDevice) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(newSiteDevice.getSite(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER && authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		final boolean deviceExists = deviceRepository.existsById(newSiteDevice.getDeviceId());
		if (!deviceExists) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		final boolean alreadyAdded = siteDeviceRepository.existsBySiteDevicePKDeviceId(newSiteDevice.getDeviceId());
		if (alreadyAdded) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}
		
		siteDeviceRepository.save(newSiteDevice);
		return ResponseEntity.ok(siteDeviceRepository.findBySiteDevicePK(newSiteDevice.getSiteDevicePK()));
	}

	@DeleteMapping
	public ResponseEntity<Object> deleteSiteDevice(@RequestParam String deviceId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		SiteDevice siteDevice = null;
		try {
			final byte[] deviceIdBytes = HexFormat.of().parseHex(deviceId); 
			siteDevice = siteDeviceRepository.findBySiteDevicePKDeviceId(deviceIdBytes);
		} catch (NoSuchElementException e) {
			// Should be fine if this is thrown as record doesn't exist which is the outcome
			// we wanted anyway.
			e.printStackTrace();
			return ResponseEntity.ok(null);
		}
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(siteDevice.getSite(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER && authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			// Should be fine if this is thrown as record doesn't exist which is the outcome
			// we wanted anyway.
			e.printStackTrace();
		}

		siteDeviceRepository.delete(siteDevice);
		return ResponseEntity.ok(null);
	}
}
