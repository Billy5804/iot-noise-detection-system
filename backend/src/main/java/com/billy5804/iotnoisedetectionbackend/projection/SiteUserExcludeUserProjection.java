package com.billy5804.iotnoisedetectionbackend.projection;

import com.billy5804.iotnoisedetectionbackend.model.Site;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface SiteUserExcludeUserProjection extends CommonAttributesProjection {
	@JsonProperty("site")
	public Site getSiteUserPKSite();

	public SiteUserRole getRole();
}
