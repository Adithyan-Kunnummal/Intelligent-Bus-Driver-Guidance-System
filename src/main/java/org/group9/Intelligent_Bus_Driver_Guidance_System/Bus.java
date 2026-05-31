package org.group9.Intelligent_Bus_Driver_Guidance_System;

public class Bus {

	public static final String[] FUEL_TYPES = { "Diesel", "Hybrid", "Electricity" };

	private String busID;
	private int capacity;
	private double fuelLevel;
	private String fuelType;

	public Bus(String busID, int capacity, double fuelLevel, String fuelType) {

		if (capacity < 1) throw new IllegalArgumentException();

		this.busID = busID;
		this.capacity = capacity;
		this.fuelLevel = fuelLevel;
		this.fuelType = fuelType;
	}

	// B1: 8 characters, all digits.
	public static boolean isValidID(String id) {
		if (id == null || id.length() != 8) {
			return false;
		}
		for (int i = 0; i < id.length(); i++) {
			if (!Character.isDigit(id.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidFuelType(String fuelType) {
		for (String type : FUEL_TYPES) {
			if (type.equals(fuelType)) {
				return true;
			}
		}
		return false;
	}

	// B3, B4, B5: whether the given driver may operate this bus.
	public boolean canBeDrivenBy(Driver driver) {
		if (driver.getAge() > 50 && capacity >= 50) {
			return false;
		}
		boolean electric = "Electricity".equals(fuelType);
		if (electric && driver.getExperienceYears() < 5) {
			return false;
		}
		if ((electric || "Hybrid".equals(fuelType))
				&& !("Heavy".equals(driver.getLicenseType())
						|| "PublicTransport".equals(driver.getLicenseType()))) {
			return false;
		}
		return true;
	}

	public String getBusID() {
		return busID;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public double getFuelLevel() {
		return fuelLevel;
	}

	public void setFuelLevel(double fuelLevel) {
		this.fuelLevel = fuelLevel;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
}
