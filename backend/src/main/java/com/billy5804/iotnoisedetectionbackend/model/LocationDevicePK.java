package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

@Embeddable
public class LocationDevicePK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8332515402681719818L;

	@JoinColumn(name = "location_id", referencedColumnName = "id", table = "location")
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID locationId;

	@JoinColumn(name = "device_id", referencedColumnName = "device_id", table = "siteDevice")
	@Column(columnDefinition = "BINARY(6)", updatable = false, nullable = false)
	private byte[] deviceId;

	public LocationDevicePK() {
	}

	public LocationDevicePK(UUID locationId, byte[] deviceId) {
		this.locationId = locationId;
		this.deviceId = deviceId;
	}

	public UUID getLocationId() {
		return locationId;
	}

	public void setlocationIdd(UUID locationId) {
		this.locationId = locationId;
	}

	public byte[] getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(byte[] deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(locationId, deviceId);
	}
}
