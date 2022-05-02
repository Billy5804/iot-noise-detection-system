package com.billy5804.iotnoisedetectionbackend.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Location extends CommonAttributes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6641260583715589068L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@JoinColumn(name = "siteId", referencedColumnName = "id", table = "site")
	@Column(columnDefinition = "BINARY(6)", updatable = false, nullable = false)
	private UUID siteId;

	@Column(columnDefinition = "VARCHAR(32)", updatable = true, nullable = false)
	private String displayName;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getSiteId() {
		return siteId;
	}

	public void setSiteId(UUID siteId) {
		this.siteId = siteId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
