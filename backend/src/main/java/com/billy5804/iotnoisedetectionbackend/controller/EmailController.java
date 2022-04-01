package com.billy5804.iotnoisedetectionbackend.controller;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billy5804.iotnoisedetectionbackend.handler.EmailInvitationArgsHandler;
import com.billy5804.iotnoisedetectionbackend.model.AuthUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteInvitation;
import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.repository.SiteInvitationRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;
import com.billy5804.iotnoisedetectionbackend.service.EmailService;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Personalization;

@RestController
@RequestMapping(value = "/api/v1/email")
public class EmailController {

	@Autowired
	EmailService emailService;

	@Autowired
	private SiteInvitationRepository siteInvitationRepository;

	@Autowired
	private SiteUserRepository siteUserRepository;

	@Value("${server.baseURL}")
	String baseURL;

	@PostMapping("/invitation")
	public ResponseEntity<Object> emailInvitation(@RequestBody EmailInvitationArgsHandler args) throws IOException {
		final AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication();
		SiteInvitation siteInvitation = null;
		try {
			siteInvitation = siteInvitationRepository.findById(args.getInvitationId()).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
		final String invitationURL = String.format("%ssites/invitation/%s", baseURL, siteInvitation.getId());
		final Content emailContent = new Content("text/html",
				String.format(
						"<p>Hello,</p>" + "<p>%s has invited you to join the site \"%s\".</p>"
								+ "<p>If you wish to join the site you can do so by following the link.<p>"
								+ "<p><a href='%s'>%<s</a></p>"
								+ "<p>If you don’t want to join this site, you can ignore this email.</p>"
								+ "<p>Thanks,</p>" + "<p>Your IoT Noise Detection Platform team</p>",
						authUser.getDisplayName(), siteInvitation.getSite().getDisplayName(), invitationURL));
		final String emailSubject = "You've been invited to join a site";
		final Mail mail = new Mail();
		mail.addContent(emailContent);
		mail.setSubject(emailSubject);
		args.getEmails().forEach(email -> {
			final Personalization personalization = new Personalization();
			personalization.addTo(email);
			mail.addPersonalization(personalization);
		});

		return ResponseEntity.ok(emailService.sendEmail(mail));
	}

}
