package org.group9.Intelligent_Bus_Driver_Guidance_System;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Driver conditions D1–D5.
 * Tests are ordered to match the Test Case Table (TC1–TC30).
 * Each test is tagged with its TC number and case type (normal / invalid / edge).
 */
class TestDriver {

	// Helper that builds a baseline valid driver for reuse across tests.
	private Driver sample(String id) {
		return new Driver(id, "John Doe", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");
	}

	// ---- D1: Driver ID rules ----

	// TC1 | Normal | A correctly formatted 10-character ID passes validation
	@Test
	void d1_validIdPasses() {
		assertTrue(Driver.isValidID("23#$abcdAB"));
	}

	// TC2 | Invalid | ID shorter than 10 characters is rejected
	@Test
	void d1_tooShortFails() {
		assertFalse(Driver.isValidID("23#$abAB"));
	}

	// TC3 | Invalid | ID longer than 10 characters is rejected
	@Test
	void d1_tooLongFails() {
		assertFalse(Driver.isValidID("23#$abcdeAB"));
	}

	// TC4 | Invalid | First two characters not digits in range 2–9
	@Test
	void d1_firstTwoNotDigitsInRangeFails() {
		assertFalse(Driver.isValidID("10#$abcdAB"));
	}

	// TC5 | Invalid | No special characters between positions 3 and 8
	@Test
	void d1_fewerThanTwoSpecialCharsFails() {
		assertFalse(Driver.isValidID("23abcdefAB"));
	}

	// TC6 | Edge | Positions 3–8 are entirely special characters (boundary of the "at least two" rule)
	@Test
	void d1_allSpecialCharsbetween2and9Passes() {
		assertTrue(Driver.isValidID("23@@@@@@AB"));
	}

	// TC7 | Invalid | Last two characters are not uppercase letters
	@Test
	void d1_lastTwoNotUppercaseFails() {
		assertFalse(Driver.isValidID("23#$abcdab"));
	}

	// TC8 | Invalid | Adding a driver with a duplicate ID is rejected
	@Test
	void d1_duplicateIdRejected() {
		DriverRepository repo = new DriverRepository();
		repo.add(sample("23#$abcdAB"));
		assertThrows(IllegalArgumentException.class, () -> repo.add(sample("23#$abcdAB")));
	}

	// TC9 | Edge | A null driver ID is rejected
	@Test
	void d1_nullDriverIdFails() {
		assertFalse(Driver.isValidID(null));
	}

	// ---- D2: Address format ----

	// TC10 | Invalid | Wrong delimiter (commas instead of pipes)
	@Test
	void d2_wrongDelimiterFails() {
		assertFalse(Driver.isValidAddress("12,Main St,Springfield,VIC,AU,Extra"));
	}

	// TC11 | Invalid | Too few fields (4 instead of 5)
	@Test
	void d2_tooFewPartsFails() {
		assertFalse(Driver.isValidAddress("12|Main St|Springfield|VIC"));
	}

	// TC12 | Normal | A correctly formatted 5-part address passes validation
	@Test
	void d2_validAddressPasses() {
		assertTrue(Driver.isValidAddress("12|Main St|Springfield|VIC|AU"));
	}

	// TC13 | Invalid | An empty field between delimiters
	@Test
	void d2_emptyPartFails() {
		assertFalse(Driver.isValidAddress("12||Springfield|VIC|AU"));
	}

	// TC14 | Invalid | Too many fields (6 instead of 5)
	@Test
	void d2_tooManyPartsFails() {
		assertFalse(Driver.isValidAddress("12|Main St|Springfield|VIC|AU|Extra"));
	}

	// TC15 | Edge | A null address is rejected
	@Test
	void d2_nullAddressFails() {
		assertFalse(Driver.isValidAddress(null));
	}

	// ---- D3: Birthdate format ----

	// TC16 | Invalid | Wrong separator (commas instead of hyphens)
	@Test
	void d3_wrongSeparatorFails() {
		assertFalse(Driver.isValidBirthdate("29,02,2020"));
	}

	// TC17 | Invalid | Wrong order (YYYY-MM-DD instead of DD-MM-YYYY)
	@Test
	void d3_wrongFormatFails() {
		assertFalse(Driver.isValidBirthdate("1990-01-01"));
	}

	// TC18 | Invalid | Impossible month and day values
	@Test
	void d3_impossibleMonthAndDayFails() {
		assertFalse(Driver.isValidBirthdate("32-13-2020"));
	}

	// TC19 | Normal | A valid DD-MM-YYYY date passes validation
	@Test
	void d3_validBirthdatePasses() {
		assertTrue(Driver.isValidBirthdate("29-02-2020"));
	}

	// TC20 | Edge | 29/30 February in a non-leap year (calendar boundary)
	@Test
	void d3_nonLeapFebruaryFails() {
		assertFalse(Driver.isValidBirthdate("30-02-2021"));
	}

	// TC21 | Edge | A null birthdate is rejected
	@Test
	void d3_nullBirthdateFails() {
		assertFalse(Driver.isValidBirthdate(null));
	}

	// ---- D4: License update restriction (>10 years) ----

	// TC22 | Invalid | Driver with MORE THAN 10 years cannot change licence type
	@Test
	void d4_experienceMoreThanTenYearsCannotChangeLicense() {
		DriverRepository repo = new DriverRepository();
		repo.add(new Driver("23#$abcdAB", "Jane", 15, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1980"));
		Driver changed = new Driver("23#$abcdAB", "Jane", 15, "Medium", "12|Main St|Springfield|VIC|AU", "01-01-1980");
		assertThrows(IllegalArgumentException.class, () -> repo.update(changed));
	}

	// TC23 | Edge | Driver with EXACTLY 10 years can change licence (10 is not more than 10)
	@Test
	void d4_experienceExactlyTenYearsCanChangeLicense() {
		DriverRepository repo = new DriverRepository();
		repo.add(new Driver("23#$abcdAB", "Jane", 10, "Heavy",
				"12|Main St|Springfield|VIC|AU", "01-01-1980"));
		Driver changed = new Driver("23#$abcdAB", "Jane", 10, "Medium",
				"12|Main St|Springfield|VIC|AU", "01-01-1980");
		assertDoesNotThrow(() -> repo.update(changed),
				"Exactly 10 years is not MORE THAN 10; license change should be allowed");
	}

	// TC24 | Normal | Driver with FEWER THAN 10 years can change licence type
	@Test
	void d4_experienceLessThanTenYearsCanChangeLicense() {
		DriverRepository repo = new DriverRepository();
		repo.add(new Driver("23#$abcdAB", "Jane", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990"));
		Driver changed = new Driver("23#$abcdAB", "Jane", 5, "Light", "12|Main St|Springfield|VIC|AU", "01-01-1990");
		assertDoesNotThrow(() -> repo.update(changed));
	}

	// TC25 | Normal | Experienced driver (>10 yrs) may still change non-licence fields (address)
	@Test
	void d4_experiencedDriverCanChangeOtherFields() {
		DriverRepository repo = new DriverRepository();
		repo.add(new Driver("23#$abcdAB", "Jane", 15, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1980"));
		Driver changed = new Driver("23#$abcdAB", "Jane", 15, "Heavy", "99|New Rd|Geelong|VIC|AU", "01-01-1980");
		assertDoesNotThrow(() -> repo.update(changed));
	}

	// TC26 | Invalid | Experienced driver update to an invalid licence type is rejected
	@Test
	void d4_experiencedDriverUpdateWithInvalidLicenseTypeFails() {
		DriverRepository repo = new DriverRepository();
		repo.add(new Driver("23#$abcdAB", "Jane", 15, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1980"));
		Driver changed = new Driver("23#$abcdAB", "Jane", 15, "Motorcycle", "99|New Rd|Geelong|VIC|AU", "01-01-1980");
		assertThrows(IllegalArgumentException.class, () -> repo.update(changed));
	}

	// TC27 | Invalid | Adding a driver with a licence type outside the allowed set is rejected
	@Test
	void d4_invalidLicenseTypeRejectedByRepository() {
		DriverRepository repo = new DriverRepository();

		Driver invalidDriver = new Driver(
				"24#$abcdCD",
				"Alex Tan",
				5,
				"Motorcycle",
				"88|King St|Melbourne|VIC|AU",
				"15-05-1998"
		);

		assertThrows(IllegalArgumentException.class, () -> repo.add(invalidDriver));
	}

	// ---- D5: Immutable fields (driverID, name) ----

	// TC28 | Invalid | The name cannot be modified during an update
	@Test
	void d5_nameCannotBeModified() {
		DriverRepository repo = new DriverRepository();
		repo.add(sample("23#$abcdAB"));
		Driver renamed = new Driver("23#$abcdAB", "Different Name", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");
		assertThrows(IllegalArgumentException.class, () -> repo.update(renamed));
	}

	// TC29 | Normal | Updating with the same name (changing experience) succeeds and persists
	@Test
	void d5_updatingUnchangedNameSucceeds() {
		DriverRepository repo = new DriverRepository();
		repo.add(sample("23#$abcdAB"));
		Driver updated = new Driver("23#$abcdAB", "John Doe", 8, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");
		assertDoesNotThrow(() -> repo.update(updated));
		assertEquals(8, repo.retrieve("23#$abcdAB").getExperienceYears());
	}

	// TC30 | Edge | Updating a driver whose ID is not in the repository is rejected
	@Test
	void d5_updatingUnknownIdFails() {
		DriverRepository repo = new DriverRepository();
		assertThrows(IllegalArgumentException.class, () -> repo.update(sample("23#$abcdAB")));
	}

}