package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Base64;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class SiteUserPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5048091949800198200L;

	@ManyToOne
	@JoinColumn(name = "site_id", referencedColumnName = "id")
	private Site site;
	private byte[] userId;

	public SiteUserPK() {
	}

	public SiteUserPK(Site site, byte[] userId) {
		this.site = site;
		this.userId = userId;
	}

	public SiteUserPK(Site site, String userId) {
		this.site = site;
		this.userId = Base64.getDecoder().decode(userId);
		;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getUserId() {
		return Base64.getEncoder().encodeToString(userId);
	}

	public void setUserId(String userId) {
		this.userId = Base64.getDecoder().decode(userId);
		;
	}

	public void setUserId(byte[] userId) {
		this.userId = userId;
	}
}