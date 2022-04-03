package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class DeviceSensorPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 541183890999181474L;

	private int id;
	
	@ManyToOne
	@JoinColumn(name = "device_id", referencedColumnName = "id")
	private Device device;

	public DeviceSensorPK() {
		this.device = new Device();
	}

	public DeviceSensorPK(int id, Device device) {
		this.id = id;
		this.device = device;
	}

	public DeviceSensorPK(int id, byte[] deviceId) {
		final Device device = new Device();
		device.setId(deviceId);
		
		this.id = id;
		this.device = device;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	
}