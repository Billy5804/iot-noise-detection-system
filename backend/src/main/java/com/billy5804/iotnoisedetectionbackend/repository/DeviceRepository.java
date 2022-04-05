package com.billy5804.iotnoisedetectionbackend.repository;

import org.springframework.data.repository.CrudRepository;

import com.billy5804.iotnoisedetectionbackend.model.Device;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteRepository
// CRUD refers Create, Read, Update, Delete

public interface DeviceRepository extends CrudRepository<Device, byte[]> {

}
