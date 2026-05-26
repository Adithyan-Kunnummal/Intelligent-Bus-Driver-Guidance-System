package org.group9.Intelligent_Bus_Driver_Guidance_System;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Storage format (human-readable TXT, one driver per line, comma-separated):
 *   driverID,name,experienceYears,licenseType,address,birthdate
 * Example:
 *   23#$abcdAB,John Doe,5,Heavy,12|Main St|Springfield|VIC|AU,01-01-1990
 * The address keeps its own pipe ('|') delimiters; commas are not used inside fields.
 * A no-arg instance is in-memory only; the String constructor binds to a file.
 */
public class DriverRepository {

	private final Map<String, Driver> drivers = new LinkedHashMap<>();
	private final Path file;

	public DriverRepository() {
		this.file = null;
	}

	public DriverRepository(String filePath) {
		this.file = Path.of(filePath);
		load();
	}

	public void add(Driver driver) {
		validate(driver);
		if (drivers.containsKey(driver.getDriverID())) {
			throw new IllegalArgumentException("Duplicate driver ID: " + driver.getDriverID());
		}
		drivers.put(driver.getDriverID(), driver);
		save();
	}

	public void update(Driver driver) {
		Driver existing = drivers.get(driver.getDriverID());
		if (existing == null) {
			throw new IllegalArgumentException("Driver not found: " + driver.getDriverID());
		}
		// D5: name is immutable (driverID is the key, so it is immutable by construction).
		if (!existing.getName().equals(driver.getName())) {
			throw new IllegalArgumentException("Driver name cannot be modified");
		}
		// D4: experienced drivers (>10 years) cannot change license type.
		if (existing.getExperienceYears() > 10
				&& !existing.getLicenseType().equals(driver.getLicenseType())) {
			throw new IllegalArgumentException("License type cannot be changed for experienced drivers");
		}
		validate(driver);
		drivers.put(driver.getDriverID(), driver);
		save();
	}

	public Driver retrieve(String driverID) {
		return drivers.get(driverID);
	}

	public int count() {
		return drivers.size();
	}

	private void validate(Driver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("Driver is null");
		}
		if (!Driver.isValidID(driver.getDriverID())) {
			throw new IllegalArgumentException("Invalid driver ID: " + driver.getDriverID());
		}
		if (!Driver.isValidLicenseType(driver.getLicenseType())) {
			throw new IllegalArgumentException("Invalid license type: " + driver.getLicenseType());
		}
		if (!Driver.isValidAddress(driver.getAddress())) {
			throw new IllegalArgumentException("Invalid address: " + driver.getAddress());
		}
		if (!Driver.isValidBirthdate(driver.getBirthdate())) {
			throw new IllegalArgumentException("Invalid birthdate: " + driver.getBirthdate());
		}
	}

	private void load() {
		if (file == null || !Files.exists(file)) {
			return;
		}
		try {
			for (String line : Files.readAllLines(file)) {
				if (line.isBlank()) {
					continue;
				}
				String[] f = line.split(",", -1);
				Driver d = new Driver(f[0], f[1], Integer.parseInt(f[2]), f[3], f[4], f[5]);
				drivers.put(d.getDriverID(), d);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void save() {
		if (file == null) {
			return;
		}
		List<String> lines = new ArrayList<>();
		for (Driver d : drivers.values()) {
			lines.add(String.join(",", d.getDriverID(), d.getName(),
					String.valueOf(d.getExperienceYears()), d.getLicenseType(),
					d.getAddress(), d.getBirthdate()));
		}
		try {
			Files.write(file, lines);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
