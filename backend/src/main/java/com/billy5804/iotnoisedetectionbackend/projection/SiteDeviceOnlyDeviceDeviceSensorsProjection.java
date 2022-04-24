package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.List;

import com.billy5804.iotnoisedetectionbackend.model.DeviceSensor;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SiteDeviceOnlyDeviceDeviceSensorsProjection {
	@JsonIgnore
	public DeviceOnlyDeviceSensorsProjection getSiteDevicePKDevice();
		
	default List<DeviceSensor> getSensors() {
		return getSiteDevicePKDevice().getSensors();
	}
	
	interface DeviceOnlyDeviceSensorsProjection {
		public List<DeviceSensor> getSensors();
	}
}
