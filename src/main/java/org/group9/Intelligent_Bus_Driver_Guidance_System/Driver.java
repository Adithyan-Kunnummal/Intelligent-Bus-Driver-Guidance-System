package org.group9.Intelligent_Bus_Driver_Guidance_System;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class Driver {

	public static final String[] LICENSE_TYPES = { "Light", "Medium", "Heavy", "PublicTransport" };

	private static final DateTimeFormatter DATE_FORMAT =
			DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

	private String driverID;
	private String name;
	private int experienceYears;
	private String licenseType;
	private String address;
	private String birthdate;

	public Driver(String driverID, String name, int experienceYears, String licenseType,
			String address, String birthdate) {
		this.driverID = driverID;
		this.name = name;
		this.experienceYears = experienceYears;
		this.licenseType = licenseType;
		this.address = address;
		this.birthdate = birthdate;
	}

	// D1: 10 chars; first two digits 2-9; >=2 special chars in positions 3-8; last two uppercase letters.
	public static boolean isValidID(String id) {
		if (id == null || id.length() != 10) {
			return false;
		}
		for (int i = 0; i < 2; i++) {
			char c = id.charAt(i);
			if (c < '2' || c > '9') {
				return false;
			}
		}
		int special = 0;
		for (int i = 2; i < 8; i++) {
			if (!Character.isLetterOrDigit(id.charAt(i))) {
				special++;
			}
		}
		if (special < 2) {
			return false;
		}
		for (int i = 8; i < 10; i++) {
			char c = id.charAt(i);
			if (c < 'A' || c > 'Z') {
				return false;
			}
		}
		return true;
	}

	// D2: Street Number|Street Name|City|State|Country
	public static boolean isValidAddress(String address) {
		if (address == null) {
			return false;
		}
		String[] parts = address.split("\\|", -1);
		if (parts.length != 5) {
			return false;
		}
		for (String part : parts) {
			if (part.trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	// D3: DD-MM-YYYY, must be a real calendar date.
	public static boolean isValidBirthdate(String birthdate) {
		if (birthdate == null) {
			return false;
		}
		try {
			LocalDate.parse(birthdate, DATE_FORMAT);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isValidLicenseType(String licenseType) {
		for (String type : LICENSE_TYPES) {
			if (type.equals(licenseType)) {
				return true;
			}
		}
		return false;
	}

	public int getAge() {
		LocalDate born = LocalDate.parse(birthdate, DATE_FORMAT);
		return Period.between(born, LocalDate.now()).getYears();
	}

	public String getDriverID() {
		return driverID;
	}

	public String getName() {
		return name;
	}

	public int getExperienceYears() {
		return experienceYears;
	}

	public void setExperienceYears(int experienceYears) {
		this.experienceYears = experienceYears;
	}

	public String getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
}
