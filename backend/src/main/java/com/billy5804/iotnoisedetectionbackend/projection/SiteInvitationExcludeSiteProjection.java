package com.billy5804.iotnoisedetectionbackend.projection;

import java.util.Date;
import java.util.UUID;

public interface SiteInvitationExcludeSiteProjection extends CommonAttributesProjection {
	public UUID getId();

	public String getDisplayName();

	public Integer getAvailableUses();

	public Date getExpiresAt();
}
