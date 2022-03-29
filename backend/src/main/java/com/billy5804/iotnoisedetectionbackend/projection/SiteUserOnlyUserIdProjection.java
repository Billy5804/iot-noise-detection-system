package com.billy5804.iotnoisedetectionbackend.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface SiteUserOnlyUserIdProjection {
	@JsonProperty("userId")
	public byte[] getSiteUserPKUserId();
}
