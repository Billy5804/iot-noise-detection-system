package com.billy5804.iotnoisedetectionbackend.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.billy5804.iotnoisedetectionbackend.model.Device;
import com.billy5804.iotnoisedetectionbackend.model.SiteDevice;
import com.billy5804.iotnoisedetectionbackend.model.SiteDevicePK;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceExpandDeviceExcludeSiteProjection;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceOnlyDeviceDeviceSensorsProjection;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceOnlySiteIdProjection;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteRepository
// CRUD refers Create, Read, Update, Delete

public interface SiteDeviceRepository extends CrudRepository<SiteDevice, SiteDevicePK> {

	SiteDeviceOnlySiteIdProjection findBySiteDevicePKDevice(Device device);

	Iterable<SiteDeviceExpandDeviceExcludeSiteProjection> findBySiteDevicePKSiteId(UUID siteId);

	default Iterable<SiteDeviceExpandDeviceExcludeSiteProjection> getExpandedDeviceBySiteId(UUID siteId) {
		return findBySiteDevicePKSiteId(siteId);
	}

	SiteDeviceExpandDeviceExcludeSiteProjection findBySiteDevicePK(SiteDevicePK siteDevicePK);

	SiteDevice findBySiteDevicePKDeviceId(byte[] deviceId);

	boolean existsBySiteDevicePKDeviceId(byte[] deviceId);

	SiteDeviceOnlyDeviceDeviceSensorsProjection findBySiteDevicePKSiteIdAndSiteDevicePKDeviceId(UUID siteId,
			byte[] deviceId);

}
