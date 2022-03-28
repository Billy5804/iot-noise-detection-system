package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.Date;

import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SiteUserExcludeSiteProjection {
	@JsonIgnore
	public SiteUserPK getSiteUserPK();

	public SiteUserRole getRole();

	public Date getCreatedAt();

	public Date getUpdatedAt();

	default User getUser() {
		return new User(getSiteUserPK().getUserId());
	}
}
