package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity // This tells Hibernate to make a table out of this class
public class SiteDeviceSensorHistory extends CommonAttributes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8343453285113986424L;

	@Id
	private SiteDeviceSensorHistoryPK siteDeviceSensorHistoryPK = new SiteDeviceSensorHistoryPK();

	@JoinColumn(name = "siteId", referencedColumnName = "id", table = "siteDevice")
	@Column(columnDefinition = "BINARY(6)", updatable = false, nullable = false)
	private UUID siteId;

	@Column(columnDefinition = "FLOAT(5,2)", updatable = false, nullable = true)
	private float value;

	@JsonIgnore
	public SiteDeviceSensorHistoryPK getSiteDeviceSensorHistoryPK() {
		return siteDeviceSensorHistoryPK;
	}

	public void setSiteDeviceSensorHistoryPK(SiteDeviceSensorHistoryPK siteDeviceSensorHistoryPK) {
		this.siteDeviceSensorHistoryPK = siteDeviceSensorHistoryPK;
	}

//	public Site getSite() {
//		return site;
//	}
//
//	public void setSite(Site site) {
//		this.site = site;
//	}

	@JsonSetter
	public void setSiteId(UUID siteId) {
		this.siteId = siteId;
	}

	@JsonIgnore
	public UUID getSiteId() {
		return this.siteId;
	}

//	public DeviceSensorPK getDeviceSensorPK() {
//		return siteDeviceSensorHistoryPK.getDeviceSensorPK();
//	}
//
//	public void setDeviceSensorPK(DeviceSensorPK deviceSensorPK) {
//		siteDeviceSensorHistoryPK.setDeviceSensorPK(deviceSensorPK);
//	}

//	@JsonIgnore
//	public byte[] getDeviceId() {
//		return siteDeviceSensorHistoryPK.getDevice().getId();
//	}
//
//	@JsonSetter
//	public void setDeviceId(byte[] deviceId) {
//		siteDeviceSensorHistoryPK.getDevice().setId(deviceId);
//	}

	@JsonIgnore
	public byte[] getDeviceId() {
		return siteDeviceSensorHistoryPK.getDeviceId();
	}

	@JsonSetter
	public void setDeviceId(byte[] deviceId) {
		siteDeviceSensorHistoryPK.setDeviceId(deviceId);
	}

	@JsonIgnore
	public int getSensorId() {
		return siteDeviceSensorHistoryPK.getSensorId();
	}

	@JsonSetter
	public void setSensorId(int sensorId) {
		siteDeviceSensorHistoryPK.setSensorId(sensorId);
	}

	public Date getTimestamp() {
		return siteDeviceSensorHistoryPK.getTimestamp();
	}

	public void setTimestamp(Date timestamp) {
		siteDeviceSensorHistoryPK.setTimestamp(timestamp);
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(siteDeviceSensorHistoryPK);
	}
}