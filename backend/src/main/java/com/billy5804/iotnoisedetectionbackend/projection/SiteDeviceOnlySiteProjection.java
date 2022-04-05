package com.billy5804.iotnoisedetectionbackend.projection;

import com.billy5804.iotnoisedetectionbackend.model.Site;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface SiteDeviceOnlySiteProjection extends CommonAttributesProjection {
	@JsonProperty("site")
	public Site getSiteDevicePKSite();
}
