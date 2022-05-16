package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

@Embeddable
public class SiteDeviceSensorHistoryPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3152352929776506950L;

//	@JoinColumns({ @JoinColumn(name = "sensorId", referencedColumnName = "id", table = "deviceSensor" ),
//			@JoinColumn(name = "deviceId", referencedColumnName = "deviceId", table = "deviceSensor") })
//	private DeviceSensorPK deviceSensorPK;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "TIMESTAMP", updatable = false, nullable = false)
	private Date timestamp;

	@JoinColumn(name = "sensorId", referencedColumnName = "id", table = "deviceSensor")
	@Column(columnDefinition = "BINARY(6)", updatable = false, nullable = false)
	private int sensorId;

	@JoinColumn(name = "deviceId", referencedColumnName = "deviceId", table = "deviceSensor")
	@JoinColumn(name = "deviceId", referencedColumnName = "deviceId", table = "siteDevice")
	@Column(columnDefinition = "BINARY(6)", updatable = false, nullable = false)
	private byte[] deviceId;

	public SiteDeviceSensorHistoryPK() {
//		this.deviceSensorPK = new DeviceSensorPK();
	}

//	public SiteDeviceSensorHistoryPK(DeviceSensorPK deviceSensorPK, Date timestamp) {
////		this.deviceSensorPK = deviceSensorPK;
//		this.timestamp = timestamp;
//	}

	public SiteDeviceSensorHistoryPK(byte[] deviceId, int sensorId) {
		this(deviceId, sensorId, null);
	}

	public SiteDeviceSensorHistoryPK(byte[] deviceId, int sensorId, Date timestamp) {
		this.deviceId = deviceId;
		this.sensorId = sensorId;
		this.timestamp = timestamp;
//		this(new DeviceSensorPK(sensorId, deviceId), timestamp);
	}

//	public DeviceSensorPK getDeviceSensorPK() {
//		return deviceSensorPK;
//	}
//
//	public void setDeviceSensorPK(DeviceSensorPK deviceSensorPK) {
//		this.deviceSensorPK = deviceSensorPK;
//	}

//	public Device getDevice() {
//		return deviceSensor.getDevice();
//	}
//
//	public void setDevice(Device device) {
//		this.deviceSensor.setDevice(device);
//	}

	public byte[] getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(byte[] deviceId) {
		this.deviceId = deviceId;
	}

	public int getSensorId() {
		return sensorId;
	}

	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}