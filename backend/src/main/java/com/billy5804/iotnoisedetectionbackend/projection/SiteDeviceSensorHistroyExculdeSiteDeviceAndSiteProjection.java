package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface SiteDeviceSensorHistroyExculdeSiteDeviceAndSiteProjection extends CommonAttributesProjection {
	@JsonProperty("timestamp")
	public Date getSiteDeviceSensorHistoryPKTimestamp();

	public float getValue();
}
