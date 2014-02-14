package com.rrdonnelly.up2me.json;

import java.util.Date;

public class Phone{

	private Long id;
	
	private String manufacturer;
    private String name;
    private String color;
    private String dimensionsInInches;
    private String operatingsystem;
    private String processor;
    private String connectionspeed;
    private Double DisplaySizeInInches;
    private String displayResolution;
    private Double storageMemoryInGB;
    private Double cameraMegapixels;
    private Double batteryLifeInHours;
    private Double priceWithContract;
    private Double weightInOunces;
    private long availableDate;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getDimensionsInInches() {
		return dimensionsInInches;
	}
	public void setDimensionsInInches(String dimensionsInInches) {
		this.dimensionsInInches = dimensionsInInches;
	}
	public String getOperatingsystem() {
		return operatingsystem;
	}
	public void setOperatingsystem(String operatingsystem) {
		this.operatingsystem = operatingsystem;
	}
	public String getProcessor() {
		return processor;
	}
	public void setProcessor(String processor) {
		this.processor = processor;
	}
	public String getConnectionspeed() {
		return connectionspeed;
	}
	public void setConnectionspeed(String connectionspeed) {
		this.connectionspeed = connectionspeed;
	}
	public Double getDisplaySizeInInches() {
		return DisplaySizeInInches;
	}
	public void setDisplaySizeInInches(Double displaySizeInInches) {
		DisplaySizeInInches = displaySizeInInches;
	}
	public String getDisplayResolution() {
		return displayResolution;
	}
	public void setDisplayResolution(String displayResolution) {
		this.displayResolution = displayResolution;
	}
	public Double getStorageMemoryInGB() {
		return storageMemoryInGB;
	}
	public void setStorageMemoryInGB(Double storageMemoryInGB) {
		this.storageMemoryInGB = storageMemoryInGB;
	}
	public Double getCameraMegapixels() {
		return cameraMegapixels;
	}
	public void setCameraMegapixels(Double cameraMegapixels) {
		this.cameraMegapixels = cameraMegapixels;
	}
	public Double getBatteryLifeInHours() {
		return batteryLifeInHours;
	}
	public void setBatteryLifeInHours(Double batteryLifeInHours) {
		this.batteryLifeInHours = batteryLifeInHours;
	}
	public Double getPriceWithContract() {
		return priceWithContract;
	}
	public void setPriceWithContract(Double priceWithContract) {
		this.priceWithContract = priceWithContract;
	}
	public Double getWeightInOunces() {
		return weightInOunces;
	}
	public void setWeightInOunces(Double weightInOunces) {
		this.weightInOunces = weightInOunces;
	}
	public long getAvailableDate() {
		return availableDate;
	}
	public void setAvailableDate(long availableDate) {
		this.availableDate = availableDate;
	}

}
