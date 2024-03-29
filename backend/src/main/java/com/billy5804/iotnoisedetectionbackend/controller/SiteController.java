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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.billy5804.iotnoisedetectionbackend.helper.updateHelper;
import com.billy5804.iotnoisedetectionbackend.model.Site;
import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.model.AuthUser;
import com.billy5804.iotnoisedetectionbackend.repository.SiteRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@RestController
@CrossOrigin({ "http://localhost:3000", "http://localhost:5050" })
@RequestMapping(value = "/api/v1/sites", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SiteController {

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private SiteUserRepository siteUserRepository;

	@PutMapping
	public ResponseEntity<Site> updateSite(@RequestBody Site updateSite) {
		final AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
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
	public Site createSite(@RequestBody Site newSite) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		final Site site = siteRepository.save(newSite);
		final SiteUser siteUser = new SiteUser();
		siteUser.setSiteUserPK(new SiteUserPK(site, authUser.getName()));
		siteUser.setRole(SiteUserRole.OWNER);
		siteUserRepository.save(siteUser);
		return site;
	}

	@DeleteMapping
	public ResponseEntity<String> deleteSite(@RequestParam UUID siteId) {
		final AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		SiteUser currentSiteUser = null;
		try {
			currentSiteUser = siteUserRepository.findById(new SiteUserPK(siteId, user.getName())).get();
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
