package com.billy5804.iotnoisedetectionbackend.repository;

import org.springframework.data.repository.CrudRepository;

import com.billy5804.iotnoisedetectionbackend.model.SiteUser;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteUserRepository
// CRUD refers Create, Read, Update, Delete

public interface SiteUserRepository extends CrudRepository<SiteUser, SiteUserPK> {

}
