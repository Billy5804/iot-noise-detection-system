package com.billy5804.iotnoisedetectionbackend.repository;

import java.util.Base64;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserRole;
import com.billy5804.iotnoisedetectionbackend.projection.SiteUserExcludeSiteProjection;
import com.billy5804.iotnoisedetectionbackend.projection.SiteUserExcludeUserProjection;
import com.billy5804.iotnoisedetectionbackend.projection.SiteUserOnlyUserIdProjection;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteUserRepository
// CRUD refers Create, Read, Update, Delete

public interface SiteUserRepository extends CrudRepository<SiteUser, SiteUserPK> {
	Iterable<SiteUserExcludeUserProjection> findBySiteUserPKUserId(byte[] userId);
	
	default Iterable<SiteUserExcludeUserProjection> getByUserId(String userId) {
		return findBySiteUserPKUserId(Base64.getDecoder().decode(userId));
	}
	
	Iterable<SiteUserExcludeSiteProjection> findBySiteUserPKSiteIdAndRoleNot(UUID siteId, SiteUserRole role);
	
	default Iterable<SiteUserExcludeSiteProjection> getNonOwnersBySiteId(UUID siteId) {
		return findBySiteUserPKSiteIdAndRoleNot(siteId, SiteUserRole.OWNER);
	}
	
	Iterable<SiteUserOnlyUserIdProjection> findBySiteUserPKSiteIdAndRole(UUID siteId, SiteUserRole role);
	@Transactional
	void deleteBySiteUserPKSiteIdAndRole(UUID siteId, SiteUserRole role);

	default Iterable<SiteUserOnlyUserIdProjection> deleteUnauthorisedBySiteId(UUID siteId) {
		final Iterable<SiteUserOnlyUserIdProjection> usersToBeDeletedIds = findBySiteUserPKSiteIdAndRole(siteId, SiteUserRole.UNAUTHORISED);
		deleteBySiteUserPKSiteIdAndRole(siteId, SiteUserRole.UNAUTHORISED);
		return usersToBeDeletedIds;
	}
}
