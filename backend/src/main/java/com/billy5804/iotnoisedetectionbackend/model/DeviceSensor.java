package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity // This tells Hibernate to make a table out of this class
public class DeviceSensor extends CommonAttributes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2523397135751491230L;

	@Id
	private DeviceSensorPK deviceSensorPK = new DeviceSensorPK();

	@Column(columnDefinition = "TINYINT", updatable = false, nullable = false)
	private SensorUnit unit;

	@Column(columnDefinition = "FLOAT(5,2)", updatable = true, nullable = true)
	private float latestValue;

	public DeviceSensor() {
	}

	public DeviceSensor(int id, byte[] deviceId) {
		this.deviceSensorPK = new DeviceSensorPK(id, deviceId);
	}

	@JsonIgnore
	public DeviceSensorPK getDeviceSensorPK() {
		return deviceSensorPK;
	}

	public void setDeviceSensorPK(DeviceSensorPK deviceSensorPK) {
		this.deviceSensorPK = deviceSensorPK;
	}

//	public Device getDevice() {
//		return deviceSensorPK.getDevice();
//	}
//
//	public void setDevice(Device device) {
//		deviceSensorPK.setDevice(device);
//	}

	@JsonIgnore
	public byte[] getDeviceId() {
		return deviceSensorPK.getDeviceId();
	}

	public void setDeviceId(byte[] deviceId) {
		deviceSensorPK.setDeviceId(deviceId);
	}

//	@JsonIgnore
//	public byte[] getDeviceId() {
//		return deviceSensorPK.getDevice().getId();
//	}
//
//	public void setDeviceId(byte[] deviceId) {
//		deviceSensorPK.getDevice().setId(deviceId);
//	}

	public int getId() {
		return deviceSensorPK.getId();
	}

	public void setId(int id) {
		deviceSensorPK.setId(id);
	}

	public SensorUnit getUnit() {
		return unit;
	}

	public void setUnit(SensorUnit unit) {
		this.unit = unit;
	}

	public float getLatestValue() {
		return latestValue;
	}

	public void setLatestValue(float latestValue) {
		this.latestValue = latestValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(deviceSensorPK);
	}
}