package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity // This tells Hibernate to make a table out of this class
public class SiteInvitation extends CommonAttributes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2674357561866711276L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "site_id", columnDefinition = "BINARY(16)", referencedColumnName = "id", updatable = false, nullable = false)
	private Site site;

	@Column(columnDefinition = "INT", updatable = true, nullable = true)
	private Integer availableUses;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = true, nullable = false)
	private Date expiresAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	@JsonIgnore
	public UUID getSiteId() {
		return site.getId();
	}

	@JsonSetter
	public void setSiteId(UUID siteId) {
		if (this.site == null) {
			this.site = new Site();
		}
		this.site.setId(siteId);
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Integer getAvailableUses() {
		return availableUses;
	}

	public void setAvailableUses(Integer availableUses) {
		this.availableUses = availableUses;
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}