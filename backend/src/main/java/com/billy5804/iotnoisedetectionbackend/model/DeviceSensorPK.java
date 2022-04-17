package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

@Embeddable
public class DeviceSensorPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 541183890999181474L;

	@Column(columnDefinition = "TINYINT UNSIGNED", updatable = false, nullable = false)
	private int id;

	@JoinColumn(name = "device_id", referencedColumnName = "id", table = "device")
	@Column(columnDefinition = "BINARY(6)", updatable = false, nullable = false)
	private byte[] deviceId;

//	public DeviceSensorPK() {
//		this.device = new Device();
//	}

	public DeviceSensorPK() {
	}

//	public DeviceSensorPK(int id, Device device) {
//		this.id = id;
//		this.device = device;
//	}
//
//	public DeviceSensorPK(int id, byte[] deviceId) {
//		this(id, new Device(deviceId));
//	}

	public DeviceSensorPK(int id, byte[] deviceId) {
		this.id = id;
		this.deviceId = deviceId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

//	public Device getDevice() {
//		return device;
//	}
//
//	public void setDevice(Device device) {
//		this.device = device;
//	}

	public byte[] getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(byte[] deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, deviceId);
	}

}