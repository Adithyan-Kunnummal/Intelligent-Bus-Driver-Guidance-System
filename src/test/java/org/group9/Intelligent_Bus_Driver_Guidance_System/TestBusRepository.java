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
 * Integration tests for BusRepository.
 * Tests are ordered to follow Test Case Table (TC1–TC6).
 * Each test is tagged with its TC number and case type (normal / invalid / edge).
 */
class TestBusRepository {

	private final Path targetDir = Paths.get(System.getProperty("user.dir"), "test-output");
    private String currentFileName;

    @BeforeEach
    void setUp(TestInfo testInfo) throws IOException {
        // Ensure the root test-output directory exists
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        
        // Dynamically name the file based on the executing test method
        currentFileName = testInfo.getTestMethod().get().getName() + ".txt";
        
        // Clean up previous runs of this exact test so data remains fresh
        Files.deleteIfExists(targetDir.resolve(currentFileName));
    }

    private String filePath() {
        return targetDir.resolve(currentFileName).toString();
    }

	// ---- Bus Repository Integration Tests ----

	// TC1 | Normal | A bus object with valid bus ID is successfully added and retrieved from a text file
	@Test
	void validBusIsStoredAndPersisted() {
		String path = filePath();
		new BusRepository(path).add(new Bus("12345678", 40, 80.5, "Diesel"));

		Bus loaded = new BusRepository(path).retrieve("12345678");
		assertEquals(40, loaded.getCapacity());
		assertEquals("Diesel", loaded.getFuelType());
		assertEquals(80.5, loaded.getFuelLevel());
	}

	// TC2 | Invalid | A bus object with invalid bus ID is rejected and not written to a text file
	@Test
	void invalidBusIsRejectedAndNotPersisted() {
		String path = filePath();
		BusRepository repo = new BusRepository(path);

		assertThrows(IllegalArgumentException.class, () -> repo.add(new Bus("123", 40, 80.0, "Diesel")));
		assertEquals(0, new BusRepository(path).count());
	}

	// TC3 | Normal | Bus capacity reduction update overwrites and updates the record in the text file
	@Test
	void capacityDecreaseUpdateIsPersisted() {
		String path = filePath();
		BusRepository repo = new BusRepository(path);
		repo.add(new Bus("12345678", 50, 80.0, "Diesel"));
		repo.update(new Bus("12345678", 35, 80.0, "Diesel"));

		Bus loaded = new BusRepository(path).retrieve("12345678");
		assertEquals(35, loaded.getCapacity());
	}
	
	// TC4 | Invalid | Bus capacity increase update is rejected, keeping original records intact in text file
	 @Test
	    void capacityIncreaseRejectedAndNotPersisted() {
	        String path = filePath();
	        BusRepository repo = new BusRepository(path);
	        repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
	 
	        assertThrows(IllegalArgumentException.class, () -> repo.update(new Bus("12345678", 50, 80.0, "Diesel")));
	 
	        Bus reloaded = new BusRepository(path).retrieve("12345678");
	        assertEquals(40, reloaded.getCapacity());
	    }

	// TC5 | Normal | Total repository records count matches accurately across text file reloads
	@Test
	void countReflectsStoredRecordsAcrossReload() {
		String path = filePath();
		BusRepository repo = new BusRepository(path);
		repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
		repo.add(new Bus("87654321", 30, 60.0, "Electricity"));
		assertEquals(2, repo.count());
		assertEquals(2, new BusRepository(path).count());
	}

	// TC6 | Edge | Querying an unassigned or non-existent bus ID returns null
	@Test
	void retrieveNonExistingBusReturnsNull() {
		String path = filePath();
		BusRepository repo = new BusRepository(path);

		assertNull(repo.retrieve("99999999"));
	}
}