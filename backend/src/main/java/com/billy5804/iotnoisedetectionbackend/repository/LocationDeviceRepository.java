package com.billy5804.iotnoisedetectionbackend.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.billy5804.iotnoisedetectionbackend.model.LocationDevice;
import com.billy5804.iotnoisedetectionbackend.model.LocationDevicePK;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteRepository
// CRUD refers Create, Read, Update, Delete

public interface LocationDeviceRepository extends CrudRepository<LocationDevice, LocationDevicePK> {

	Iterable<LocationDevice> findByLocationDevicePKLocationId(UUID locationId);

}
