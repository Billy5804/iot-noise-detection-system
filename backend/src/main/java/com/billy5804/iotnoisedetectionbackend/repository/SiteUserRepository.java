package com.billy5804.iotnoisedetectionbackend.repository;

import java.util.Base64;

import org.springframework.data.repository.CrudRepository;

import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteUserRepository
// CRUD refers Create, Read, Update, Delete

public interface SiteUserRepository extends CrudRepository<SiteUser, SiteUserPK> {
	Iterable<SiteUser> findBySiteUserPKUserId(byte[] userId);
	
	default Iterable<SiteUser> getByUserId(String userId) {
		return findBySiteUserPKUserId(Base64.getDecoder().decode(userId));
	}
}
