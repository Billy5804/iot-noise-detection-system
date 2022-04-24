package com.billy5804.iotnoisedetectionbackend.model;

public enum DeviceType {
	NOISE("Noise level detector");

	private String label;

	DeviceType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
