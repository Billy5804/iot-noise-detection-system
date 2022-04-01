package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.Base64;

import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SiteUserExcludeSiteProjection extends CommonAttributesProjection {
	@JsonIgnore
	public byte[] getSiteUserPKUserId();

	public SiteUserRole getRole();

	default User getUser() {
		return new User(Base64.getEncoder().encodeToString(getSiteUserPKUserId()));
	}
}
