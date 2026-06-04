package org.group9.Intelligent_Bus_Driver_Guidance_System;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Bus conditions B1–B5. 
 * Tests are ordered to match the Test Case Table (TC1–TC31).
 * Each test is tagged with its TC number and case type (normal / invalid / edge).
 */
class TestBus {

	// Birthdate (DD-MM-YYYY) for a driver of exactly the given age today.
	private String birthdateForAge(int age) {
		return LocalDate.now().minusYears(age).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
	}

	private Driver driver(int age, int experience, String license) {
		return new Driver("23#$abcdAB", "John", experience, license,
				"12|Main St|Springfield|VIC|AU", birthdateForAge(age));
	}

	// ---- B1: Bus ID rules ----

	// TC1 | Normal | A correctly formatted 8-digit ID passes validation
	@Test
	void b1_validIdPasses() {
		assertTrue(Bus.isValidID("12345678"));
	}

	// TC2 | Invalid | ID shorter than 8 characters is rejected
	@Test
	void b1_tooShortFails() {
		assertFalse(Bus.isValidID("1234567"));
	}

	// TC3 | Invalid | ID longer than 8 characters is rejected
	@Test
	void b1_tooLongBusIdFails() {
		assertFalse(Bus.isValidID("123456789"));
	}

	// TC4 | Invalid | ID containing a non-digit character is rejected
	@Test
	void b1_nonDigitFails() {
		assertFalse(Bus.isValidID("1234567A"));
	}

	// TC5 | Edge | A null bus ID is rejected
	@Test
	void b1_nullBusIdFails() {
		assertFalse(Bus.isValidID(null));
	}

	// TC6 | Invalid | Adding a bus with a duplicate ID is rejected
	@Test
	void b1_duplicateIdRejected() {
		BusRepository repo = new BusRepository();
		repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
		assertThrows(IllegalArgumentException.class, () -> repo.add(new Bus("12345678", 30, 50.0, "Diesel")));
	}

	// TC7 | Edge | An empty bus ID is rejected
	@Test
	void b1_emptyBusIdFails() {
		assertFalse(Bus.isValidID(""));
	}

	// TC8 | Invalid | A bus ID containing spaces is rejected
	@Test
	void b1_spacesInBusIdFail() {
		assertFalse(Bus.isValidID("1234 678"));
	}

	// ---- B2: Capacity update restriction ----

	// TC9 | Normal | Bus capacity can be decreased during an update
	@Test
	void b2_capacityCanDecrease() {
		BusRepository repo = new BusRepository();
		repo.add(new Bus("12345678", 50, 80.0, "Diesel"));
		assertDoesNotThrow(() -> repo.update(new Bus("12345678", 40, 80.0, "Diesel")));
	}

	// TC10 | Invalid | Bus capacity cannot be increased during an update
	@Test
	void b2_capacityCannotIncrease() {
		BusRepository repo = new BusRepository();
		repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
		assertThrows(IllegalArgumentException.class, () -> repo.update(new Bus("12345678", 50, 80.0, "Diesel")));
	}

	// TC11 | Edge | Bus capacity can remain the same during an update
	@Test
	void b2_capacityCanStaySame() {
		BusRepository repo = new BusRepository();
		repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
		assertDoesNotThrow(() -> repo.update(new Bus("12345678", 40, 60.0, "Diesel")));
	}

	// TC12 | Invalid | Creating a bus with negative capacity is rejected
	@Test
	void b2_negativeCapacityFails() {
		assertThrows(IllegalArgumentException.class, () -> new Bus("12345678", -1, 80.0, "Diesel"));
	}

	// ---- B3: Driver age restriction (>50 cannot drive capacity >= 50) ----

	// TC13 | Invalid | Driver older than 50 cannot drive a bus with capacity greater than 50
	@Test
	void b3_oldDriverCannotDriveBusWithCapacityGreaterThan50() {
		Bus bus = new Bus("12345678", 51, 80.0, "Diesel");
		assertFalse(bus.canBeDrivenBy(driver(51, 10, "Heavy")));
	}

	// TC14 | Edge | Driver older than 50 cannot drive a bus with capacity exactly 50
	@Test
	void b3_oldDriverCannotDriveBusWithCapacityExactly50() {
		Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
		assertFalse(bus.canBeDrivenBy(driver(51, 10, "Heavy")));
	}

	// TC15 | Normal | Driver older than 50 can drive a bus with capacity under 50
	@Test
	void b3_oldDriverCanDrivewithCapacityUnder50() {
		Bus bus = new Bus("12345678", 49, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(51, 10, "Heavy")));
	}

	// TC16 | Edge | Driver aged exactly 50 can drive a bus with capacity of 50
	@Test
	void b3_DriverAgedExactly50CanDriveLargeBus() {
		Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(50, 10, "Heavy")));
	}

	// TC17 | Normal | Driver younger than 50 can drive a bus with capacity of 50
	@Test
	void b3_youngDriverCanDriveLargeBus() {
		Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(49, 10, "Heavy")));
	}

	// TC18 | Normal | Driver aged 50 or younger can drive a bus with capacity under 50
	@Test
	void b3_youngDriverCanDriveSmallBus() {
		Bus bus = new Bus("12345678", 40, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(50, 10, "Heavy")));
	}

	// TC19 | Edge | A null driver cannot drive any bus
	@Test
	void b3_nullDriverFails() {
		Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
		assertThrows(NullPointerException.class, () -> bus.canBeDrivenBy(null));
	}

	// ---- B4: Electric bus experience restriction (>= 5 years) ----

	// TC20 | Invalid | Driver with fewer than 5 years of experience cannot drive an electric bus
	@Test
	void b4_driverWithLessThanFiveYearsCannotDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertFalse(bus.canBeDrivenBy(driver(30, 4, "Heavy")));
	}

	// TC21 | Edge | Driver with exactly 5 years of experience can drive an electric bus
	@Test
	void b4_driverWithExactlyFiveYearsCanDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertTrue(bus.canBeDrivenBy(driver(30, 5, "Heavy")));
	}

	// TC22 | Normal | Driver with more than 5 years of experience can drive an electric bus
	@Test
	void b4_driverWithMoreThanFiveYearsCanDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertTrue(bus.canBeDrivenBy(driver(30, 6, "Heavy")));
	}

	// TC23 | Normal | Experience restriction does not apply to Diesel buses
	@Test
	void b4_experienceIrrelevantForDiesel() {
		Bus bus = new Bus("12345678", 20, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(30, 1, "Light")));
	}

	// ---- B5: Licence restriction for electric and hybrid buses ----

	// TC24 | Invalid | Light licence cannot drive a Hybrid bus
	@Test
	void b5_lightLicenceCannotDriveHybrid() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertFalse(bus.canBeDrivenBy(driver(30, 10, "Light")));
	}

	// TC25 | Invalid | Light licence cannot drive an Electric bus
	@Test
	void b5_lightLicenceCannotDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertFalse(bus.canBeDrivenBy(driver(30, 10, "Light")));
	}

	// TC26 | Invalid | Medium licence cannot drive a Hybrid bus
	@Test
	void b5_mediumLicenceCannotDriveHybrid() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertFalse(bus.canBeDrivenBy(driver(30, 10, "Medium")));
	}

	// TC27 | Invalid | Medium licence cannot drive an Electric bus
	@Test
	void b5_mediumLicenceCannotDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertFalse(bus.canBeDrivenBy(driver(30, 10, "Medium")));
	}

	// TC28 | Normal | Heavy licence can drive a Hybrid bus
	@Test
	void b5_heavyLicenceCanDriveHybrid() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertTrue(bus.canBeDrivenBy(driver(30, 10, "Heavy")));
	}

	// TC29 | Normal | Heavy licence can drive an Electric bus
	@Test
	void b5_heavyLicenceCanDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertTrue(bus.canBeDrivenBy(driver(30, 10, "Heavy")));
	}

	// TC30 | Normal | PublicTransport licence can drive a Hybrid bus
	@Test
	void b5_publicTransportLicenceCanDriveHybrid() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertTrue(bus.canBeDrivenBy(driver(30, 10, "PublicTransport")));
	}

	// TC31 | Normal | PublicTransport licence can drive an Electric bus
	@Test
	void b5_publicTransportLicenceCanDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electric");
		assertTrue(bus.canBeDrivenBy(driver(30, 10, "PublicTransport")));
	}

}