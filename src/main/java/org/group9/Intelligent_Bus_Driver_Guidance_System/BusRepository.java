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
 * Storage format (human-readable TXT, one bus per line, comma-separated):
 *   busID,capacity,fuelLevel,fuelType
 * Example:
 *   12345678,40,80.5,Diesel
 * A no-arg instance is in-memory only; the String constructor binds to a file.
 */
public class BusRepository {

	private final Map<String, Bus> buses = new LinkedHashMap<>();
	private final Path file;

	public BusRepository() {
		this.file = null;
	}

	public BusRepository(String filePath) {
		this.file = Path.of(filePath);
		load();
	}

	public void add(Bus bus) {
		validate(bus);
		if (buses.containsKey(bus.getBusID())) {
			throw new IllegalArgumentException("Duplicate bus ID: " + bus.getBusID());
		}
		buses.put(bus.getBusID(), bus);
		save();
	}

	public void update(Bus bus) {
		Bus existing = buses.get(bus.getBusID());
		if (existing == null) {
			throw new IllegalArgumentException("Bus not found: " + bus.getBusID());
		}
		// B2: capacity may decrease but not increase.
		if (bus.getCapacity() > existing.getCapacity()) {
			throw new IllegalArgumentException("Bus capacity cannot increase");
		}
		validate(bus);
		buses.put(bus.getBusID(), bus);
		save();
	}

	public Bus retrieve(String busID) {
		return buses.get(busID);
	}

	public int count() {
		return buses.size();
	}

	private void validate(Bus bus) {
		if (bus == null) {
			throw new IllegalArgumentException("Bus is null");
		}
		if (!Bus.isValidID(bus.getBusID())) {
			throw new IllegalArgumentException("Invalid bus ID: " + bus.getBusID());
		}
		if (!Bus.isValidFuelType(bus.getFuelType())) {
			throw new IllegalArgumentException("Invalid fuel type: " + bus.getFuelType());
		}
		if (bus.getCapacity() <= 0) {
			throw new IllegalArgumentException("Capacity must be positive");
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
				Bus b = new Bus(f[0], Integer.parseInt(f[1]), Double.parseDouble(f[2]), f[3]);
				buses.put(b.getBusID(), b);
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
		for (Bus b : buses.values()) {
			lines.add(String.join(",", b.getBusID(), String.valueOf(b.getCapacity()),
					String.valueOf(b.getFuelLevel()), b.getFuelType()));
		}
		try {
			Files.write(file, lines);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
