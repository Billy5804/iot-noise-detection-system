package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity // This tells Hibernate to make a table out of this class
public class Device extends CommonAttributes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2674357561866711276L;

	@Id
	@Column(columnDefinition = "BINARY(4)", updatable = false, nullable = false)
	private byte[] id;

	@Column(columnDefinition = "TINYINT", updatable = false, nullable = false)
	private DeviceType type;

	@Column(columnDefinition = "TINYINT", updatable = true, nullable = false)
	private int rssi;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "TIMESTAMP", updatable = true, nullable = false)
	private Date lastBeatTime;

	@OneToMany
	@JoinColumn(referencedColumnName = "device_id")
	private Iterable<DeviceSensor> sensors;

	public Device() {
	}

	public Device(byte[] id) {
		this.id = id;
	}

	public byte[] getId() {
		return id;
	}

	public void setId(byte[] id) {
		this.id = id;
	}

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public Date getLastBeatTime() {
		return lastBeatTime;
	}

	public void setLastBeatTime(Date lastBeatTime) {
		this.lastBeatTime = lastBeatTime;
	}

	public Iterable<DeviceSensor> getSensors() {
		return sensors;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}