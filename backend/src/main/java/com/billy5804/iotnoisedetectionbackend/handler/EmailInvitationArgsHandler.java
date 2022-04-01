package com.billy5804.iotnoisedetectionbackend.handler;

import java.util.UUID;

import com.sendgrid.helpers.mail.objects.Email;

public class EmailInvitationArgsHandler {
	private UUID invitationId;
	private Iterable<Email> emails;

	public UUID getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(UUID invitationId) {
		this.invitationId = invitationId;
	}

	public Iterable<Email> getEmails() {
		return emails;
	}

	public void setEmails(Iterable<Email> emails) {
		this.emails = emails;
	}
}
