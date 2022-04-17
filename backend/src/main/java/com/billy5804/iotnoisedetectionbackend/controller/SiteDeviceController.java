package com.billy5804.iotnoisedetectionbackend.controller;

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

	@GetMapping
	public ResponseEntity<Iterable<SiteDeviceExpandDeviceExcludeSiteProjection>> getSitesSiteUsers(
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
	public ResponseEntity<SiteDevice> updateSiteDevice(@RequestBody SiteDevice updateSiteDevice) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(updateSiteDevice.getSite(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER || authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
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
		return ResponseEntity.ok(siteDeviceRepository.save(currentSiteDevice));
	}

	@PostMapping
	public ResponseEntity<SiteDevice> createSiteUser(@RequestBody SiteDevice newSiteDevice) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(newSiteDevice.getSite(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER || authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		return ResponseEntity.ok(siteDeviceRepository.save(newSiteDevice));
	}

	@DeleteMapping
	public ResponseEntity<Object> deleteCurrentUsersSiteUser(@RequestParam byte[] deviceId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		SiteDevice siteDevice = null;
		try {
			siteDevice = siteDeviceRepository.findBySiteDevicePKDeviceId(deviceId);
		} catch (NoSuchElementException e) {
			// Should be fine if this is thrown as record doesn't exist which is the outcome
			// we wanted anyway.
			e.printStackTrace();
			return ResponseEntity.ok(null);
		}
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(siteDevice.getSite(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER || authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
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