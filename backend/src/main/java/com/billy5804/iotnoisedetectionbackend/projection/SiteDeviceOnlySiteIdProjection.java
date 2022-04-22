package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.UUID;

import com.billy5804.iotnoisedetectionbackend.model.Site;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface SiteDeviceOnlySiteIdProjection extends CommonAttributesProjection {
	@JsonProperty("siteId")
	public UUID getSiteDevicePKSiteId();
}
