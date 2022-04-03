package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class SiteDevicePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5752365789031631613L;
	
	@ManyToOne
	@JoinColumn(name = "site_id", referencedColumnName = "id")
	private Site site;
	
	@ManyToOne
	@JoinColumn(name = "device_id", referencedColumnName = "id")
	private Device device;

	public SiteDevicePK() {
		this.site = new Site();
		this.device = new Device();
	}

	public SiteDevicePK(Site site, Device device) {
		this.site = site;
		this.device = device;
	}

	public SiteDevicePK(Site site, byte[] deviceId) {
		final Device device = new Device();
		device.setId(deviceId);
		
		this.site = site;
		this.device = device;
	}

	public SiteDevicePK(UUID siteId, Device device) {
		final Site site = new Site();
		site.setId(siteId);

		this.site = site;
		this.device = device;
	}
	
	public SiteDevicePK(UUID siteId, byte[] deviceId) {
		final Site site = new Site();
		site.setId(siteId);
		
		final Device device = new Device();
		device.setId(deviceId);

		this.site = site;
		this.device = device;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}	
}