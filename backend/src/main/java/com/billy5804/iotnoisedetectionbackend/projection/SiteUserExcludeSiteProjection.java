package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.Date;

import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.fasterxml.jackson.annotation.JsonProperty;


public interface SiteUserExcludeSiteProjection {
	@JsonProperty("userId")
	public byte[] getSiteUserPKUserId();
	public SiteUserRole getRole();
	public Date getCreatedAt();
	public Date getUpdatedAt();
}
