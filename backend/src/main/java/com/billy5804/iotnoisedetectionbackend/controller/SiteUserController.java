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
import com.billy5804.iotnoisedetectionbackend.model.User;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping(value = "/api/v1/site-users", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SiteUserController {

	@Autowired
	private SiteUserRepository siteUserRepository;

	@GetMapping
	public Iterable<SiteUser> getCurrentUsersSiteUsers() {
		final User user = (User) SecurityContextHolder.getContext().getAuthentication();
		return siteUserRepository.getByUserId(user.getName());
	}

	@PutMapping
	public ResponseEntity<SiteUser> updateSpecifiedUsersSiteUser(@RequestBody SiteUser updateSiteUser) {
		final User authUser = (User) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository.findById(new SiteUserPK(updateSiteUser.getSite(), authUser.getName())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		SiteUser currentSpecifiedUsersSiteUser = null;
		try {
			currentSpecifiedUsersSiteUser = siteUserRepository.findById(updateSiteUser.getPK()).get();
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
	
	@DeleteMapping
	public ResponseEntity<String> deleteCurrentUsersSiteUser(@RequestParam UUID siteId) {
		final User user = (User) SecurityContextHolder.getContext().getAuthentication();
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
}
