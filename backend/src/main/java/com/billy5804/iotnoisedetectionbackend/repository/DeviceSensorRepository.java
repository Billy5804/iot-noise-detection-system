package com.billy5804.iotnoisedetectionbackend.repository;

import org.springframework.data.repository.CrudRepository;

import com.billy5804.iotnoisedetectionbackend.model.DeviceSensor;
import com.billy5804.iotnoisedetectionbackend.model.DeviceSensorPK;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteRepository
// CRUD refers Create, Read, Update, Delete

public interface DeviceSensorRepository extends CrudRepository<DeviceSensor, DeviceSensorPK> {

}
