package org.group9.Intelligent_Bus_Driver_Guidance_System;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

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

	@Test
	void b1_validIdPasses() {
		assertTrue(Bus.isValidID("12345678"));
	}

	@Test
	void b1_tooShortFails() {
		assertFalse(Bus.isValidID("1234567"));
	}
	
	@Test
	void b1_tooLongBusIdFails() {
		assertFalse(Bus.isValidID("123456789"));
	}

	@Test
	void b1_nonDigitFails() {
		assertFalse(Bus.isValidID("1234567A"));
	}

	@Test
	void b1_nullBusIdFails() {
		assertFalse(Bus.isValidID(null));
	}

	@Test
	void b1_duplicateIdRejected() {
		BusRepository repo = new BusRepository();
		repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
		assertThrows(IllegalArgumentException.class, () -> repo.add(new Bus("12345678", 30, 50.0, "Diesel")));
	}

	// ---- B2: Capacity update restriction ----

	@Test
	void b2_capacityCanDecrease() {
		BusRepository repo = new BusRepository();
		repo.add(new Bus("12345678", 50, 80.0, "Diesel"));
		assertDoesNotThrow(() -> repo.update(new Bus("12345678", 40, 80.0, "Diesel")));
	}

	@Test
	void b2_capacityCannotIncrease() {
		BusRepository repo = new BusRepository();
		repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
		assertThrows(IllegalArgumentException.class, () -> repo.update(new Bus("12345678", 50, 80.0, "Diesel")));
	}

	@Test
	void b2_capacityCanStaySame() {
		BusRepository repo = new BusRepository();
		repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
		assertDoesNotThrow(() -> repo.update(new Bus("12345678", 40, 60.0, "Diesel")));
	}

	// ---- B3: Driver age restriction (>50 cannot drive capacity >= 50) ----

	@Test
	void b3_oldDriverCannotDriveBusWithCapacityGreaterThan50() {
		Bus bus = new Bus("12345678", 51, 80.0, "Diesel");
		assertFalse(bus.canBeDrivenBy(driver(51, 10, "Heavy")));
	}
	
	@Test
	void b3_oldDriverCannotDriveBusWithCapacityExactly50() {
		Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
		assertFalse(bus.canBeDrivenBy(driver(51, 10, "Heavy")));
	}

	@Test
	void b3_oldDriverCanDrivewithCapacityUnder50() {
		Bus bus = new Bus("12345678", 49, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(51, 10, "Heavy")));
	}
	
	@Test
	void b3_DriverAgedExactly50CanDriveLargeBus() {
		Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(50, 10, "Heavy")));
	}

	@Test
	void b3_youngDriverCanDriveLargeBus() {
		Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(50, 10, "Heavy")));
	}
	
	@Test
	void b3_youngDriverCanDriveSmallBus() {
		Bus bus = new Bus("12345678", 40, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(50, 10, "Heavy")));
	}

	// ---- B4: Electric bus experience restriction (>= 5 years) ----

	@Test
	void b4_driverWithLessThanFiveYearsCannotDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertFalse(bus.canBeDrivenBy(driver(30, 4, "Heavy")));
	}

	@Test
	void b4_driverWithExactlyFiveYearsCanDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertTrue(bus.canBeDrivenBy(driver(30, 5, "Heavy")));
	}

	@Test
	void b4_driverWithMoreThanFiveYearsCanDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertTrue(bus.canBeDrivenBy(driver(30, 6, "Heavy")));
	}

	@Test
	void b4_experienceIrrelevantForDiesel() {
		Bus bus = new Bus("12345678", 20, 80.0, "Diesel");
		assertTrue(bus.canBeDrivenBy(driver(30, 1, "Light")));
	}

	// ---- B5: Licence restriction for electric and hybrid buses ----

	@Test
	void b5_lightLicenceCannotDriveHybrid() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertFalse(bus.canBeDrivenBy(driver(30, 10, "Light")));
	}
	
	@Test
	void b5_lightLicenceCannotDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertFalse(bus.canBeDrivenBy(driver(30, 10, "Light")));
	}
	
	@Test
	void b5_mediumLicenceCannotDriveHybrid() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertFalse(bus.canBeDrivenBy(driver(30, 10, "Medium")));
	}
	
	@Test
	void b5_mediumLicenceCannotDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electricity");
		assertFalse(bus.canBeDrivenBy(driver(30, 10, "Medium")));
	}

	@Test
	void b5_heavyLicenceCanDriveHybrid() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertTrue(bus.canBeDrivenBy(driver(30, 10, "Heavy")));
	}
	
	@Test
	void b5_heavyLicenceCanDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertTrue(bus.canBeDrivenBy(driver(30, 10, "Heavy")));
	}

	@Test
	void b5_publicTransportLicenceCanDriveHybrid() {
		Bus bus = new Bus("12345678", 20, 80.0, "Hybrid");
		assertTrue(bus.canBeDrivenBy(driver(30, 10, "PublicTransport")));
	}
	
	@Test
	void b5_publicTransportLicenceCanDriveElectric() {
		Bus bus = new Bus("12345678", 20, 80.0, "Electric");
		assertTrue(bus.canBeDrivenBy(driver(30, 10, "PublicTransport")));
	}

	// ---- Additional driver tests by tlee ----
	@Test
	void b1_emptyBusIdFails() {
		assertFalse(Bus.isValidID(""));
	}

	@Test
	void b1_spacesInBusIdFail() {
		assertFalse(Bus.isValidID("1234 678"));
	}

	@Test
	void b2_negativeCapacityFails() {
		assertThrows(IllegalArgumentException.class, () -> new Bus("12345678", -1, 80.0, "Diesel"));
	}

	@Test
	void b3_nullDriverFails() {
		Bus bus = new Bus("12345678", 50, 80.0, "Diesel");
		assertThrows(NullPointerException.class, () -> bus.canBeDrivenBy(null));
	}

}
