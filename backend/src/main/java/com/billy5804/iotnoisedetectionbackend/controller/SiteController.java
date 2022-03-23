package com.billy5804.iotnoisedetectionbackend.controller;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billy5804.iotnoisedetectionbackend.helper.updateHelper;
import com.billy5804.iotnoisedetectionbackend.model.Site;
import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.model.User;
import com.billy5804.iotnoisedetectionbackend.repository.SiteRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@RestController
@RequestMapping(value = "/api/v1/sites", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SiteController {

	@Autowired // This means to get the bean called userRepository
	// Which is auto-generated by Spring, we will use it to handle the data
	private SiteRepository siteRepository;

	@Autowired
	private SiteUserRepository siteUserRepository;

	@GetMapping
	public Iterable<SiteUser> getSites() {
		return siteUserRepository.findAll();
	}

	@PutMapping
	public ResponseEntity<Site> updateSite(@RequestBody Site updateSite) {
		final User user = (User) SecurityContextHolder.getContext().getAuthentication();
		SiteUser currentSiteUser = null;
		try {
			currentSiteUser = siteUserRepository.findById(new SiteUserPK(updateSite, user.getName())).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		if (currentSiteUser.getRole() != SiteUserRole.OWNER && currentSiteUser.getRole() != SiteUserRole.EDITOR) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
		final Site currentSite = currentSiteUser.getSite();
		updateHelper.copyNonNullProperties(updateSite, currentSite);
		return ResponseEntity.ok(siteRepository.save(currentSite));
	}

	@PostMapping
	public SiteUser createSite(@RequestBody Site newSite) {
		final User user = (User) SecurityContextHolder.getContext().getAuthentication();
		final Site site = siteRepository.save(newSite);
		final SiteUser siteUser = new SiteUser();
		siteUser.setSite(site);
		siteUser.setUserId(user.getName());
		siteUser.setRole(SiteUserRole.OWNER);
		return siteUserRepository.save(siteUser);
	}
	
	@DeleteMapping
	public ResponseEntity<String> deleteSite(@RequestBody Site deleteSite) {
		final User user = (User) SecurityContextHolder.getContext().getAuthentication();
		SiteUser currentSiteUser = null;
		try {
			currentSiteUser = siteUserRepository.findById(new SiteUserPK(deleteSite, user.getName())).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		if (currentSiteUser.getRole() != SiteUserRole.OWNER) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
		siteRepository.delete(currentSiteUser.getSite());
		return ResponseEntity.ok(null);
	}
}