package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity // This tells Hibernate to make a table out of this class
public class SiteUser extends CommonAttributes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2308390079317933921L;

	@Id
	private SiteUserPK siteUserPK = new SiteUserPK();

	@Column(length = 1, nullable = false)
	private SiteUserRole role;

	public void setSiteUserPK(SiteUserPK siteUserPK) {
		this.siteUserPK = siteUserPK;
	}

	public Site getSite() {
		return siteUserPK.getSite();
	}

	public void setSite(Site site) {
		siteUserPK.setSite(site);
	}

	public User getUser() {
		return new User(siteUserPK.getUserId());
	}
	
	@JsonIgnore
	public String getUserId() {
		return siteUserPK.getUserId();
	}

	public void setUserId(String userId) {
		siteUserPK.setUserId(userId);
	}

	public void setUserId(byte[] userId) {
		siteUserPK.setUserId(userId);
	}

	public SiteUserRole getRole() {
		return role;
	}

	public void setRole(SiteUserRole role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		return Objects.hash(siteUserPK);
	}

	@JsonIgnore
	public SiteUserPK getPK() {
		return siteUserPK;
	}
}