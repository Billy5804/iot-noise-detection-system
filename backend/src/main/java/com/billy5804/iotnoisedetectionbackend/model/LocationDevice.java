package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity
public class LocationDevice extends CommonAttributes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6641260583715589068L;

	@Id
	private LocationDevicePK locationDevicePK = new LocationDevicePK();

	@JoinColumn(name = "siteId", referencedColumnName = "siteId", table = "location")
	@JoinColumn(name = "siteId", referencedColumnName = "siteId", table = "siteDevice")
	@Column(columnDefinition = "BINARY(6)", updatable = false, nullable = false)
	private UUID siteId;

	@Column(columnDefinition = "int", updatable = true, nullable = true)
	private Integer positionX;

	@Column(columnDefinition = "int", updatable = true, nullable = true)
	private Integer positionY;

	@JsonIgnore
	public LocationDevicePK getLocationDevicePK() {
		return locationDevicePK;
	}

	public void setLocationDevicePK(LocationDevicePK locationDevicePK) {
		this.locationDevicePK = locationDevicePK;
	}

	public UUID getLocationId() {
		return locationDevicePK.getLocationId();
	}

	public void setLocationId(UUID locationId) {
		locationDevicePK.setlocationIdd(locationId);
	}

	@JsonIgnore
	public byte[] getDeviceId() {
		return locationDevicePK.getDeviceId();
	}

	@JsonGetter(value = "deviceId")
	public String getDeviceIdHex() {
		return HexFormat.of().formatHex(getDeviceId());
	}

	public void setDeviceId(byte[] deviceId) {
		locationDevicePK.setDeviceId(deviceId);
	}

	@JsonSetter
	public void setDeviceId(String deviceId) {
		setDeviceId(HexFormat.of().parseHex(deviceId));
	}

	public UUID getSiteId() {
		return siteId;
	}

	public void setSiteId(UUID siteId) {
		this.siteId = siteId;
	}

	public Integer getPositionX() {
		return positionX;
	}

	public void setPositionX(Integer positionX) {
		this.positionX = positionX;
	}

	public Integer getPositionY() {
		return positionY;
	}

	public void setPositionY(Integer positionY) {
		this.positionY = positionY;
	}

	@Override
	public int hashCode() {
		return Objects.hash(locationDevicePK);
	}
}
