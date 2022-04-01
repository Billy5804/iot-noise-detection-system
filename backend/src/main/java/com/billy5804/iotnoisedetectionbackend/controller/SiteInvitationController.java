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
import com.billy5804.iotnoisedetectionbackend.model.SiteInvitation;
import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.projection.SiteInvitationExcludeSiteProjection;
import com.billy5804.iotnoisedetectionbackend.repository.SiteInvitationRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping(value = "/api/v1/site-invitations", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SiteInvitationController {

	@Autowired
	private SiteUserRepository siteUserRepository;

	@Autowired
	private SiteInvitationRepository siteInvitationRepository;

	@GetMapping(params = "id")
	public ResponseEntity<SiteInvitation> getSiteInvitation(@RequestParam(name = "id") UUID invitationId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		SiteInvitation siteInvitation = null;
		try {
			siteInvitation = siteInvitationRepository.findById(invitationId).get();
			if (siteUserRepository.existsById(new SiteUserPK(siteInvitation.getSite(), authUser.getName()))) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(siteInvitation);
	}

	@GetMapping(params = "siteId")
	public ResponseEntity<Iterable<SiteInvitationExcludeSiteProjection>> getSiteInvitations(
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
		return ResponseEntity.ok(siteInvitationRepository.findBySiteId(siteId));
	}

	@PutMapping
	public ResponseEntity<SiteInvitation> updateSiteInvitation(@RequestBody SiteInvitation updateSiteInvitation) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		SiteInvitation currentSiteInvitation = null;
		try {
			currentSiteInvitation = siteInvitationRepository.findById(updateSiteInvitation.getId()).get();
		} catch (IllegalArgumentException | NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(currentSiteInvitation.getSite(), authUser.getName())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		updateSiteInvitation.setSite(currentSiteInvitation.getSite());
		updateHelper.copyNonNullProperties(updateSiteInvitation, currentSiteInvitation);
		return ResponseEntity.ok(siteInvitationRepository.save(currentSiteInvitation));
	}

	@PostMapping
	public ResponseEntity<SiteInvitation> createSiteInvitation(@RequestBody SiteInvitation newSiteInvitation) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(newSiteInvitation.getSite(), authUser.getName())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		newSiteInvitation.setId(null);
		return ResponseEntity.ok(siteInvitationRepository.save(newSiteInvitation));
	}

	@DeleteMapping
	public ResponseEntity<String> deleteSiteInvitation(@RequestParam(name = "id") UUID invitationId) {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		SiteInvitation siteInvitation = null;
		try {
			siteInvitation = siteInvitationRepository.findById(invitationId).get();
		} catch (NoSuchElementException e) {
			// Should be fine if this is thrown as record doesn't exist which is the outcome
			// we wanted anyway.
			e.printStackTrace();
			return ResponseEntity.ok(null);
		}
		try {
			final SiteUser authUserSiteUser = siteUserRepository
					.findById(new SiteUserPK(siteInvitation.getSite(), authUser.getName())).get();
			if (authUserSiteUser.getRole() != SiteUserRole.OWNER) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		try {
			siteInvitationRepository.delete(siteInvitation);
		} catch (IllegalArgumentException e) {
			// Should be fine if this is thrown as record doesn't exist which is the outcome
			// we wanted anyway.
			e.printStackTrace();
		}
		return ResponseEntity.ok(null);
	}
}
