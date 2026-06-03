package org.group9.Intelligent_Bus_Driver_Guidance_System;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Integration and persistence tests for DriverRepository.
 * Tests are ordered to follow Test Case Table (TC1–TC6).
 * Each test is tagged with its TC number and case type (normal / invalid / edge).
 */
class TestDriverRepository {

	private final Path targetDir = Paths.get(System.getProperty("user.dir"), "test-output");
    private String currentFileName;

    @BeforeEach
    void setUp(TestInfo testInfo) throws IOException {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        
        // Dynamically set the file name using the current test method's name
        currentFileName = testInfo.getTestMethod().get().getName() + ".txt";
        
        //Clean up the specific file from a PREVIOUS run so the test starts fresh
        Files.deleteIfExists(targetDir.resolve(currentFileName));
    }

    private String filePath() {
        return targetDir.resolve(currentFileName).toString();
    }

	private Driver valid(String id) {
		return new Driver(id, "John Doe", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");
	}

	// ---- Driver Repository Integration Tests ----

    // TC1 | Normal | A driver object with valid driver ID is successfully added and retrieved from a text file
	@Test
	void validDriverIsStoredAndPersisted() {
		String path = filePath();
		new DriverRepository(path).add(valid("23#$abcdAB"));

		DriverRepository reloaded = new DriverRepository(path);
		Driver loaded = reloaded.retrieve("23#$abcdAB");
		assertEquals("John Doe", loaded.getName());
		assertEquals("12|Main St|Springfield|VIC|AU", loaded.getAddress());
	}

	// TC2 | Invalid | A driver object with invalid driver ID is rejected and not written to a text file
	@Test
	void invalidDriverIsRejectedAndNotPersisted() {
		String path = filePath();
		DriverRepository repo = new DriverRepository(path);
		Driver invalid = new Driver("bad-id", "John", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");

		assertThrows(IllegalArgumentException.class, () -> repo.add(invalid));
		assertEquals(0, new DriverRepository(path).count());
	}
	
	// TC3 | Invalid | Attempting to append an existing driver ID to the repository is rejected to guarantee uniqueness
	@Test
    void duplicateIdIsRejectedAndNotPersisted() {
        String path = filePath();
        DriverRepository repo = new DriverRepository(path);
        repo.add(valid("23#$abcdAB"));
 
        assertThrows(IllegalArgumentException.class, () -> repo.add(valid("23#$abcdAB")));
 
        // Reload from file: must still have exactly 1 record
        assertEquals(1, new DriverRepository(path).count(),
                "File must contain only the first successfully added driver");
    }

	// TC4 | Normal | Valid driver field updates (license/address) correctly overwrite the driver records in the text file
	@Test
	void updatesArePersisted() {
		String path = filePath();
		DriverRepository repo = new DriverRepository(path);
		repo.add(valid("23#$abcdAB"));
		repo.update(new Driver("23#$abcdAB", "John Doe", 5, "Light", "99|New Rd|Geelong|VIC|AU", "01-01-1990"));

		Driver loaded = new DriverRepository(path).retrieve("23#$abcdAB");
		assertEquals("Light", loaded.getLicenseType());
		assertEquals("99|New Rd|Geelong|VIC|AU", loaded.getAddress());
	}

	// TC5 | Normal | Total repository records count matches accurately across text file reloads
	@Test
	void countReflectsStoredRecordsAcrossReload() {
		String path = filePath();
		DriverRepository repo = new DriverRepository(path);
		repo.add(valid("23#$abcdAB"));
		repo.add(valid("45@#wxyzCD"));
		assertEquals(2, repo.count());
		assertEquals(2, new DriverRepository(path).count());
	}

	// TC6 | Edge | Querying an unassigned or non-existent driver ID returns null
	@Test
	void retrieveNonExistingDriverReturnsNull() {
		String path = filePath();
		DriverRepository repo = new DriverRepository(path);

		assertNull(repo.retrieve("99@@abcdZZ"));
	}
}