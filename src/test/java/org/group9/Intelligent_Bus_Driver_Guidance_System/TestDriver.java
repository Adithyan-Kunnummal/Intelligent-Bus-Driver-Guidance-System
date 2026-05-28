package org.group9.Intelligent_Bus_Driver_Guidance_System;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TestDriver {

	private Driver sample(String id) {
		return new Driver(id, "John Doe", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");
	}

	// ---- D1: Driver ID rules ----

	@Test
	void d1_validIdPasses() {
		assertTrue(Driver.isValidID("23#$abcdAB"));
	}

	@Test
	void d1_tooShortFails() {
		assertFalse(Driver.isValidID("23#$abAB"));
	}
	
	@Test
	void d1_tooLongFails() {
		assertFalse(Driver.isValidID("23#$abcdeAB"));
	}

	@Test
	void d1_firstTwoNotDigitsInRangeFails() {
		assertFalse(Driver.isValidID("10#$abcdAB"));
	}

	@Test
	void d1_fewerThanTwoSpecialCharsFails() {
		assertFalse(Driver.isValidID("23abcdefAB"));
	}
	
	@Test
	void d1_allSpecialCharsbetween2and9Passes() {
		assertTrue(Driver.isValidID("23@@@@@@AB"));
	}

	@Test
	void d1_lastTwoNotUppercaseFails() {
		assertFalse(Driver.isValidID("23#$abcdab"));
	}

	@Test
	void d1_duplicateIdRejected() {
		DriverRepository repo = new DriverRepository();
		repo.add(sample("23#$abcdAB"));
		assertThrows(IllegalArgumentException.class, () -> repo.add(sample("23#$abcdAB")));
	}

	// ---- D2: Address format ----

	@Test
	void d2_validAddressPasses() {
		assertTrue(Driver.isValidAddress("12|Main St|Springfield|VIC|AU"));
	}

	@Test
	void d2_tooFewPartsFails() {
		assertFalse(Driver.isValidAddress("12|Main St|Springfield|VIC"));
	}

	@Test
	void d2_emptyPartFails() {
		assertFalse(Driver.isValidAddress("12||Springfield|VIC|AU"));
	}

	@Test
	void d2_tooManyPartsFails() {
		assertFalse(Driver.isValidAddress("12|Main St|Springfield|VIC|AU|Extra"));
	}
	
	@Test
	void d2_wrongDelimiterFails() {
		assertFalse(Driver.isValidAddress("12,Main St,Springfield,VIC,AU,Extra"));
	}

	// ---- D3: Birthdate format ----

	@Test
	void d3_validBirthdatePasses() {
		assertTrue(Driver.isValidBirthdate("29-02-2020"));
	}

	@Test
	void d3_wrongFormatFails() {
		assertFalse(Driver.isValidBirthdate("1990-01-01"));
	}
	
	@Test
	void d3_wrongSeparatorFails() {
		assertFalse(Driver.isValidBirthdate("29,02,2020"));
	}

	@Test
	void d3_impossibleMonthAndDayFails() {
		assertFalse(Driver.isValidBirthdate("32-13-2020"));
	}

	@Test
	void d3_nonLeapFebruaryFails() {
		assertFalse(Driver.isValidBirthdate("30-02-2021"));
	}

	// ---- D4: License update restriction (>10 years) ----

	@Test
	void d4_experienceMoreThanTenYearsCannotChangeLicense() {
		DriverRepository repo = new DriverRepository();
		repo.add(new Driver("23#$abcdAB", "Jane", 15, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1980"));
		Driver changed = new Driver("23#$abcdAB", "Jane", 15, "Medium", "12|Main St|Springfield|VIC|AU", "01-01-1980");
		assertThrows(IllegalArgumentException.class, () -> repo.update(changed));
	}
	
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
	
	@Test
	void d4_experienceLessThanTenYearsCanChangeLicense() {
		DriverRepository repo = new DriverRepository();
		repo.add(new Driver("23#$abcdAB", "Jane", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990"));
		Driver changed = new Driver("23#$abcdAB", "Jane", 5, "Light", "12|Main St|Springfield|VIC|AU", "01-01-1990");
		assertDoesNotThrow(() -> repo.update(changed));
	}

	@Test
	void d4_experiencedDriverCanChangeOtherFields() {
		DriverRepository repo = new DriverRepository();
		repo.add(new Driver("23#$abcdAB", "Jane", 15, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1980"));
		Driver changed = new Driver("23#$abcdAB", "Jane", 15, "Heavy", "99|New Rd|Geelong|VIC|AU", "01-01-1980");
		assertDoesNotThrow(() -> repo.update(changed));
	}
	
	void d4_experiencedDriverUpdateWithInvalidLicenseTypeFails() {
		DriverRepository repo = new DriverRepository();
		repo.add(new Driver("23#$abcdAB", "Jane", 15, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1980"));
		Driver changed = new Driver("23#$abcdAB", "Jane", 15, "Motorcycle", "99|New Rd|Geelong|VIC|AU", "01-01-1980");
		assertThrows(IllegalArgumentException.class, () -> repo.update(changed));
	}


	// ---- D5: Immutable fields (driverID, name) ----

	@Test
	void d5_nameCannotBeModified() {
		DriverRepository repo = new DriverRepository();
		repo.add(sample("23#$abcdAB"));
		Driver renamed = new Driver("23#$abcdAB", "Different Name", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");
		assertThrows(IllegalArgumentException.class, () -> repo.update(renamed));
	}

	@Test
	void d5_updatingUnchangedNameSucceeds() {
		DriverRepository repo = new DriverRepository();
		repo.add(sample("23#$abcdAB"));
		Driver updated = new Driver("23#$abcdAB", "John Doe", 8, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");
		assertDoesNotThrow(() -> repo.update(updated));
		assertEquals(8, repo.retrieve("23#$abcdAB").getExperienceYears());
	}

	@Test
	void d5_updatingUnknownIdFails() {
		DriverRepository repo = new DriverRepository();
		assertThrows(IllegalArgumentException.class, () -> repo.update(sample("23#$abcdAB")));
	}

	// ---- Additional driver tests by kj996 ----

	@Test
	void d1_nullDriverIdFails() {
		assertFalse(Driver.isValidID(null));
	}

	@Test
	void d2_nullAddressFails() {
		assertFalse(Driver.isValidAddress(null));
	}

	@Test
	void d3_nullBirthdateFails() {
		assertFalse(Driver.isValidBirthdate(null));
	}

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

}
