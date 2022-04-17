package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

@Embeddable
public class SiteDeviceSensorHistoryPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3152352929776506950L;

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "sensor_id", referencedColumnName = "id"),
			@JoinColumn(name = "device_id", referencedColumnName = "deviceId") })
	private DeviceSensor deviceSensor;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "TIMESTAMP", updatable = false, nullable = false)
	private Date timestamp;

	public SiteDeviceSensorHistoryPK() {
		this.deviceSensor = new DeviceSensor();
	}

	public SiteDeviceSensorHistoryPK(DeviceSensor deviceSensor, Date timestamp) {
		this.deviceSensor = deviceSensor;
		this.timestamp = timestamp;
	}

	public SiteDeviceSensorHistoryPK(byte[] deviceId, int sensorId) {
		this(new DeviceSensor(sensorId, deviceId), null);
	}

	public SiteDeviceSensorHistoryPK(byte[] deviceId, int sensorId, Date timestamp) {
		this(new DeviceSensor(sensorId, deviceId), timestamp);
	}

	public DeviceSensor getDeviceSensor() {
		return deviceSensor;
	}

	public void setDeviceSensor(DeviceSensor deviceSensor) {
		this.deviceSensor = deviceSensor;
	}

//	public Device getDevice() {
//		return deviceSensor.getDevice();
//	}
//
//	public void setDevice(Device device) {
//		this.deviceSensor.setDevice(device);
//	}

	public byte[] getDeviceId() {
		return deviceSensor.getDeviceId();
	}

	public void setDeviceId(byte[] deviceId) {
		this.deviceSensor.setDeviceId(deviceId);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}