package com.billy5804.iotnoisedetectionbackend.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.billy5804.iotnoisedetectionbackend.model.Location;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteRepository
// CRUD refers Create, Read, Update, Delete

public interface LocationRepository extends CrudRepository<Location, UUID> {

	Iterable<Location> findBySiteId(UUID siteId);

}
