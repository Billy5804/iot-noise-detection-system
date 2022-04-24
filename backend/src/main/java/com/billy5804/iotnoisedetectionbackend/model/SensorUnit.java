package com.billy5804.iotnoisedetectionbackend.model;

public enum SensorUnit {
	DECIBEL("dB", Sensor.SOUND), BEL("B", Sensor.SOUND);

	private final String symbol;
	private final Sensor sensorType;

	private SensorUnit(String symbol, Sensor sensorType) {
		this.symbol = symbol;
		this.sensorType = sensorType;
	}

	public String getSymbol() {
		return symbol;
	}

	public Sensor getSensorType() {
		return sensorType;
	}
}
