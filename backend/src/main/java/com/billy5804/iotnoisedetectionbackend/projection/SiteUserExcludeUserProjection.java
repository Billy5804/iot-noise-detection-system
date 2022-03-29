package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.Date;

import com.billy5804.iotnoisedetectionbackend.model.Site;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface SiteUserExcludeUserProjection {
	@JsonProperty("site")
	public Site getSiteUserPKSite();

	public SiteUserRole getRole();

	public Date getCreatedAt();

	public Date getUpdatedAt();
}
