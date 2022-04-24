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
import com.billy5804.iotnoisedetectionbackend.model.Location;
import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.repository.LocationRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@RestController
@CrossOrigin({ "http://localhost:3000", "http://localhost:5050" })
@RequestMapping(value = "/api/v1/locations", produces = { MediaType.APPLICATION_JSON_VALUE })
public class LocationController {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private SiteUserRepository siteUserRepository;

	@GetMapping
	public ResponseEntity<Iterable<Location>> getSiteLocations(@RequestParam UUID siteId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		final boolean isAuthorised = siteUserRepository
				.existsByIdAndAuthorised(new SiteUserPK(siteId, authUser.getId()));
		if (!isAuthorised) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		return ResponseEntity.ok(locationRepository.findBySiteId(siteId));
	}

	@PutMapping
	public ResponseEntity<Location> updateLocation(@RequestBody Location updateLocation) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(updateLocation.getSiteId(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER && authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		Location currentLocation = null;
		try {
			currentLocation = locationRepository.findById(updateLocation.getId()).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		updateHelper.copyNonNullProperties(updateLocation, currentLocation);

		return ResponseEntity.ok(locationRepository.save(currentLocation));
	}

	@PostMapping
	public ResponseEntity<Location> createLocation(@RequestBody Location newLocation) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(newLocation.getSiteId(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER && authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		return ResponseEntity.ok(locationRepository.save(newLocation));
	}

	@DeleteMapping
	public ResponseEntity<Object> deleteLocation(@RequestParam UUID locationId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		Location location = null;
		try {
			location = locationRepository.findById(locationId).get();
		} catch (NoSuchElementException e) {
			// Should be fine if this is thrown as record doesn't exist which is the outcome
			// we wanted anyway.
			e.printStackTrace();
			return ResponseEntity.ok(null);
		}
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(location.getSiteId(), authUser.getId())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER && authUserSiteUser.getRole() != SiteUserRole.EDITOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}

		locationRepository.delete(location);
		return ResponseEntity.ok(null);
	}
}
