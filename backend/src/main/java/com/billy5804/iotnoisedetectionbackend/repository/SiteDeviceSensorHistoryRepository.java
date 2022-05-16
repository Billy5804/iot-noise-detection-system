package com.billy5804.iotnoisedetectionbackend.repository;

import java.util.UUID;

import org.springframework.data.repository.Repository;

import com.billy5804.iotnoisedetectionbackend.model.SiteDeviceSensorHistory;
import com.billy5804.iotnoisedetectionbackend.model.SiteDeviceSensorHistoryPK;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceSensorHistoryOnlyTimestampAndValueAndSensorIdProjection;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteRepository
// CRUD refers Create, Read, Update, Delete

public interface SiteDeviceSensorHistoryRepository
		extends Repository<SiteDeviceSensorHistory, SiteDeviceSensorHistoryPK> {

	Iterable<SiteDeviceSensorHistoryOnlyTimestampAndValueAndSensorIdProjection> findBySiteDeviceSensorHistoryPKDeviceIdAndSiteDeviceSensorHistoryPKSensorIdAndSiteId(
			byte[] deviceId, int sensorId, UUID siteId);

	Iterable<SiteDeviceSensorHistoryOnlyTimestampAndValueAndSensorIdProjection> findBySiteDeviceSensorHistoryPKDeviceIdAndSiteId(
			byte[] deviceId, UUID siteId);

	void save(SiteDeviceSensorHistory siteDeviceSensorHistory);
}
