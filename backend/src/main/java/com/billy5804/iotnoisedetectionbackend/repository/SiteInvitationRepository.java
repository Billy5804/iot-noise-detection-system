package com.billy5804.iotnoisedetectionbackend.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.billy5804.iotnoisedetectionbackend.model.SiteInvitation;
import com.billy5804.iotnoisedetectionbackend.projection.SiteInvitationExcludeSiteProjection;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteUserRepository
// CRUD refers Create, Read, Update, Delete

public interface SiteInvitationRepository extends CrudRepository<SiteInvitation, UUID> {
	Iterable<SiteInvitationExcludeSiteProjection> findBySiteId(UUID siteId);
}
