package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;

public interface SiteUserExcludeSiteProjection {
	@Value("#{target.userId}")
	public String getUserId();
	public SiteUserRole getRole();
	public Date getCreatedAt();
	public Date getUpdatedAt();
}
