package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.Date;
import java.util.HexFormat;
import java.util.List;

import com.billy5804.iotnoisedetectionbackend.model.Device;
import com.billy5804.iotnoisedetectionbackend.model.DeviceSensor;
import com.billy5804.iotnoisedetectionbackend.model.DeviceType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SiteDeviceExpandDeviceExcludeSiteProjection extends CommonAttributesProjection {
	@JsonIgnore
	public Device getSiteDevicePKDevice();

	default String getId() {
		return HexFormat.of().formatHex(getSiteDevicePKDevice().getId());
	}

	default DeviceType getType() {
		return getSiteDevicePKDevice().getType();
	}

	default int getRSSI() {
		return getSiteDevicePKDevice().getRssi();
	}

	default Date getLastBeatTime() {
		return getSiteDevicePKDevice().getLastBeatTime();
	}

	default List<DeviceSensor> getSensors() {
		return getSiteDevicePKDevice().getSensors();
	}

	public String getDisplayName();
}
