package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.Date;

import com.billy5804.iotnoisedetectionbackend.model.DeviceType;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface SiteDeviceExpandDeviceExcludeSiteProjection extends CommonAttributesProjection {
	@JsonProperty("deviceId")
	public byte[] getSiteDevicePKDeviceId();

	@JsonProperty("type")
	public DeviceType getSiteDevicePKDeviceType();

	@JsonProperty("rssi")
	public DeviceType getSiteDevicePKDeviceRssi();

	@JsonProperty("lastBeatTime")
	public Date getSiteDevicePKDeviceLastBeatTime();

	@JsonProperty("sensors")
	public Date getSiteDevicePKDeviceSensors();

	public String getDisplayName();
}
