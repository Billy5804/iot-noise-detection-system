package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity // This tells Hibernate to make a table out of this class
public class SiteDevice extends CommonAttributes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2308390079317933921L;

	@Id
	private SiteDevicePK siteDevicePK = new SiteDevicePK();

	@Column(columnDefinition = "VARCHAR(32)", updatable = true, nullable = false)
	private String displayName;

	@JsonIgnore
	public SiteDevicePK getSiteDevicePK() {
		return siteDevicePK;
	}

	public void setSiteDevicePK(SiteDevicePK siteDevicePK) {
		this.siteDevicePK = siteDevicePK;
	}

	public Site getSite() {
		return siteDevicePK.getSite();
	}

	public void setSite(Site site) {
		siteDevicePK.setSite(site);
	}

	@JsonSetter
	public void setSiteId(UUID siteId) {
		siteDevicePK.getSite().setId(siteId);
	}

	@JsonIgnore
	public UUID getSiteId() {
		return siteDevicePK.getSite().getId();
	}
	
	public Device getDevice() {
		return siteDevicePK.getDevice();
	}

	public void setDevice(Device device) {
		siteDevicePK.setDevice(device);
	}
	
	@JsonIgnore
	public byte[] getDeviceId() {
		return siteDevicePK.getDevice().getId();
	}

	@JsonSetter
	public void setDeviceId(byte[] deviceId) {
		siteDevicePK.getDevice().setId(deviceId);
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(siteDevicePK);
	}
}