package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonSetter;

@Entity // This tells Hibernate to make a table out of this class
public class Device extends CommonAttributes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6551880686809014813L;

	@Id
	@Column(columnDefinition = "BINARY(6)", updatable = false, nullable = false)
	private byte[] id;

	@Column(columnDefinition = "TINYINT", updatable = false, nullable = false)
	private DeviceType type;

	@Column(columnDefinition = "TINYINT", updatable = true, nullable = false)
	private int rssi;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "TIMESTAMP", updatable = true, nullable = false)
	private Date lastBeatTime;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deviceSensorPK.deviceId")
	@OrderBy("id")
	private List<DeviceSensor> sensors;

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

	public void setId(String id) {
		this.id = HexFormat.of().parseHex(id);
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

	@JsonSetter
	public void setLastBeatTime(long lastBeatTimeSeconds) {
		this.lastBeatTime = new Date(lastBeatTimeSeconds * 1000);
	}

	public List<DeviceSensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<DeviceSensor> sensors) {
		this.sensors = sensors;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}