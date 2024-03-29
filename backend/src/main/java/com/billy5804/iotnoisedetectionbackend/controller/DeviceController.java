package com.billy5804.iotnoisedetectionbackend.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billy5804.iotnoisedetectionbackend.helper.updateHelper;
import com.billy5804.iotnoisedetectionbackend.model.Device;
import com.billy5804.iotnoisedetectionbackend.model.DeviceSensor;
import com.billy5804.iotnoisedetectionbackend.model.DeviceSensorPK;
import com.billy5804.iotnoisedetectionbackend.model.SiteDeviceSensorHistory;
import com.billy5804.iotnoisedetectionbackend.model.SiteDeviceSensorHistoryPK;
import com.billy5804.iotnoisedetectionbackend.projection.SiteDeviceOnlySiteIdProjection;
import com.billy5804.iotnoisedetectionbackend.repository.DeviceRepository;
import com.billy5804.iotnoisedetectionbackend.repository.DeviceSensorRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteDeviceRepository;
import com.billy5804.iotnoisedetectionbackend.repository.SiteDeviceSensorHistoryRepository;

@RestController
@CrossOrigin({ "http://localhost:3000", "http://localhost:5050" })
@RequestMapping(value = "/api/v1/devices", produces = { MediaType.APPLICATION_JSON_VALUE })
public class DeviceController {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private DeviceSensorRepository deviceSensorRepository;

	@Autowired
	private SiteDeviceRepository siteDeviceRepository;

	@Autowired
	private SiteDeviceSensorHistoryRepository siteDeviceSensorHistoryRepository;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@PutMapping
	public ResponseEntity<String> updateDevice(@RequestBody Device updateDevice) {
		Device currentDevice = null;
		try {
			currentDevice = deviceRepository.findById(updateDevice.getId()).get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("UNKNOWN_DEVICE");
		}

		final List<DeviceSensor> updateSensors = updateDevice.getSensors();
		final List<DeviceSensor> currentSensors = currentDevice.getSensors();

		final SiteDeviceOnlySiteIdProjection siteDeviceOnlySiteId = siteDeviceRepository
				.findBySiteDevicePKDevice(currentDevice);

		final boolean inSite = siteDeviceOnlySiteId != null;

		UUID siteId = inSite ? siteDeviceOnlySiteId.getSiteDevicePKSiteId() : null;

		if (updateSensors != null && updateSensors.size() > 0) {
			for (DeviceSensor updateSensor : updateSensors) {
				if (updateSensor == null || updateSensor.getId() < 0 || updateSensor.getId() >= currentSensors.size()) {
					continue;
				}
				final DeviceSensor currentSensor = currentSensors.get(updateSensor.getId());

				if (currentSensor.getId() != updateSensor.getId()) {
					continue;
				}

				updateSensor.setDeviceId(currentDevice.getId());
				updateHelper.copyNonNullProperties(updateSensor, currentSensor);
				deviceSensorRepository.save(currentSensor);

				if (!inSite) {
					continue;
				}

				siteId = siteDeviceOnlySiteId.getSiteDevicePKSiteId();

				final DeviceSensorPK currentSensorPK = currentSensor.getDeviceSensorPK();
				final SiteDeviceSensorHistory siteDeviceSensorHistory = new SiteDeviceSensorHistory();
				final SiteDeviceSensorHistoryPK siteDeviceSensorHistoryPK = new SiteDeviceSensorHistoryPK(
						currentSensorPK.getDeviceId(), currentSensorPK.getId(), updateDevice.getLastBeatTime());
				siteDeviceSensorHistory.setSiteDeviceSensorHistoryPK(siteDeviceSensorHistoryPK);
				siteDeviceSensorHistory.setSiteId(siteId);
				siteDeviceSensorHistory.setValue(currentSensor.getLatestValue());
				siteDeviceSensorHistoryRepository.save(siteDeviceSensorHistory);
			}
		}

		updateHelper.copyNonNullProperties(updateDevice, currentDevice);
		currentDevice.setSensors(null);

		deviceRepository.save(currentDevice);

		if (inSite) {
			simpMessagingTemplate.convertAndSend("/message/site-device/" + siteId.toString(), updateDevice);
		}

		return ResponseEntity.ok(null);
	}

	@PostMapping
	public void createDevice(@RequestBody Device newDevice) {
		List<DeviceSensor> sensors = newDevice.getSensors();
		newDevice.setSensors(null);
		deviceRepository.save(newDevice);
		sensors.forEach(deviceSensor -> {
			deviceSensor.setDeviceId(newDevice.getId());
			deviceSensorRepository.save(deviceSensor);
		});
	}
}
