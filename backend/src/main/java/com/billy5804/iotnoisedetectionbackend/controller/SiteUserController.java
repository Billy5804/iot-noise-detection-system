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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.billy5804.iotnoisedetectionbackend.helper.updateHelper;
import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.model.AuthUser;
import com.billy5804.iotnoisedetectionbackend.projection.SiteUserExcludeSiteProjection;
import com.billy5804.iotnoisedetectionbackend.projection.SiteUserExcludeUserProjection;
import com.billy5804.iotnoisedetectionbackend.projection.SiteUserOnlyUserIdProjection;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping(value = "/api/v1/site-users", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SiteUserController {

	@Autowired
	private SiteUserRepository siteUserRepository;

	@GetMapping
	public Iterable<SiteUserExcludeUserProjection> getCurrentUsersSiteUsers() {
		final AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		return siteUserRepository.getByUserId(user.getName());
	}

	@GetMapping(params = "siteId")
	public ResponseEntity<Iterable<SiteUserExcludeSiteProjection>> getSitesSiteUsers(@RequestParam UUID siteId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		SiteUser authUserSiteUser = null;
		try {
			authUserSiteUser = siteUserRepository.findById(new SiteUserPK(siteId, authUser.getName())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		return ResponseEntity.ok(siteUserRepository.getNonOwnersBySiteId(siteId));
	}

	@PutMapping
	public ResponseEntity<SiteUser> updateSpecifiedUsersSiteUser(@RequestBody SiteUser updateSiteUser) {
		if (updateSiteUser.getRole() == SiteUserRole.OWNER) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(updateSiteUser.getSite(), authUser.getName())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		SiteUser currentSpecifiedUsersSiteUser = null;
		try {
			currentSpecifiedUsersSiteUser = siteUserRepository.findById(updateSiteUser.getSiteUserPK()).get();
			if (currentSpecifiedUsersSiteUser.getRole() == SiteUserRole.OWNER) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		updateSiteUser.setSite(currentSpecifiedUsersSiteUser.getSite());
		updateHelper.copyNonNullProperties(updateSiteUser, currentSpecifiedUsersSiteUser);
		return ResponseEntity.ok(siteUserRepository.save(currentSpecifiedUsersSiteUser));
	}

	@DeleteMapping(params = "siteId")
	public ResponseEntity<String> deleteCurrentUsersSiteUser(@RequestParam UUID siteId) {
		final AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		SiteUser currentSiteUser = null;
		try {
			currentSiteUser = siteUserRepository.findById(new SiteUserPK(siteId, user.getName())).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		if (currentSiteUser.getRole() == SiteUserRole.OWNER) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
		siteUserRepository.delete(currentSiteUser);
		return ResponseEntity.ok(null);
	}

	@DeleteMapping(params = { "siteId", "userId" })
	public ResponseEntity<String> deleteSpecifiedUsersSiteUser(@RequestParam UUID siteId, @RequestParam String userId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository.findById(new SiteUserPK(siteId, authUser.getName()))
					.get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		try {
			siteUserRepository.deleteById(new SiteUserPK(siteId, userId));
		} catch (IllegalArgumentException e) {
			// Should be fine if this is thrown as record doesn't exist which is the outcome
			// we wanted anyway.
			e.printStackTrace();
		}
		return ResponseEntity.ok(null);
	}

	@DeleteMapping(params = { "siteId", "unauthorised" })
	public ResponseEntity<Iterable<SiteUserOnlyUserIdProjection>> deleteUnauthorisedSiteUser(
			@RequestParam UUID siteId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository.findById(new SiteUserPK(siteId, authUser.getName()))
					.get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		try {
			return ResponseEntity.ok(siteUserRepository.deleteUnauthorisedBySiteId(siteId));
		} catch (IllegalArgumentException e) {
			// Should be fine if this is thrown as record doesn't exist which is the outcome
			// we wanted anyway.
			e.printStackTrace();
		}
		return ResponseEntity.ok(null);
	}
}
