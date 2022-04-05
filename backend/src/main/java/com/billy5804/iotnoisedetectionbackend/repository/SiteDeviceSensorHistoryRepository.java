package com.billy5804.iotnoisedetectionbackend.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.repository.Repository;

import com.billy5804.iotnoisedetectionbackend.model.SiteDeviceSensorHistory;
import com.billy5804.iotnoisedetectionbackend.model.SiteDeviceSensorHistoryPK;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceSensorHistroyExculdeSiteDeviceAndSiteProjection;

// This will be AUTO IMPLEMENTED by Spring into a Bean called siteRepository
// CRUD refers Create, Read, Update, Delete

public interface SiteDeviceSensorHistoryRepository
		extends Repository<SiteDeviceSensorHistory, SiteDeviceSensorHistoryPK> {

	Iterable<SiteDeviceSensorHistroyExculdeSiteDeviceAndSiteProjection> findAll(Example<SiteDeviceSensorHistory> siteDeviceSensorHistory);
}
