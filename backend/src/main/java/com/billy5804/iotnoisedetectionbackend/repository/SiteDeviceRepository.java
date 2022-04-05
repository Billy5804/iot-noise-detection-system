package com.billy5804.iotnoisedetectionbackend.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.billy5804.iotnoisedetectionbackend.model.Device;
import com.billy5804.iotnoisedetectionbackend.model.SiteDevice;
import com.billy5804.iotnoisedetectionbackend.model.SiteDevicePK;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceExpandDeviceExcludeSiteProjection;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteRepository
// CRUD refers Create, Read, Update, Delete

public interface SiteDeviceRepository extends CrudRepository<SiteDevice, SiteDevicePK> {

	SiteDevice findBySiteDevicePKDevice(Device device);

	Iterable<SiteDeviceExpandDeviceExcludeSiteProjection> findBySiteDevicePKSiteId(UUID siteId);

	default Iterable<SiteDeviceExpandDeviceExcludeSiteProjection> getExpandedDeviceBySiteId(UUID siteId) {
		return findBySiteDevicePKSiteId(siteId);
	}

	SiteDevice findBySiteDevicePKDeviceId(byte[] deviceId);

}
