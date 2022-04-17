package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

	@ManyToOne
	@JoinColumn(name = "site_id", referencedColumnName = "id")
	private Site site;

	@Column(columnDefinition = "FLOAT(5,2)", updatable = false, nullable = true)
	private float value;

	@JsonIgnore
	public SiteDeviceSensorHistoryPK getSiteDeviceSensorHistoryPK() {
		return siteDeviceSensorHistoryPK;
	}

	public void setSiteDeviceSensorHistoryPK(SiteDeviceSensorHistoryPK siteDeviceSensorHistoryPK) {
		this.siteDeviceSensorHistoryPK = siteDeviceSensorHistoryPK;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	@JsonSetter
	public void setSiteId(UUID siteId) {
		site.setId(siteId);
	}

	@JsonIgnore
	public UUID getSiteId() {
		return site.getId();
	}

	public DeviceSensor getDeviceSensor() {
		return siteDeviceSensorHistoryPK.getDeviceSensor();
	}

	public void setDeviceSensor(DeviceSensor deviceSensor) {
		siteDeviceSensorHistoryPK.setDeviceSensor(deviceSensor);
	}

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
		return siteDeviceSensorHistoryPK.getDeviceSensor().getId();
	}

	@JsonSetter
	public void setSensorId(int sensorId) {
		siteDeviceSensorHistoryPK.getDeviceSensor().setId(sensorId);
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