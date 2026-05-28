package org.group9.Intelligent_Bus_Driver_Guidance_System;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

class TestDriverRepository {

	@TempDir
	Path tempDir;

	private String filePath() {
		return tempDir.resolve("drivers.txt").toString();
	}

	private Driver valid(String id) {
		return new Driver(id, "John Doe", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");
	}

	@Test
	void validDriverIsStoredAndPersisted() {
		String path = filePath();
		new DriverRepository(path).add(valid("23#$abcdAB"));

		DriverRepository reloaded = new DriverRepository(path);
		Driver loaded = reloaded.retrieve("23#$abcdAB");
		assertEquals("John Doe", loaded.getName());
		assertEquals("12|Main St|Springfield|VIC|AU", loaded.getAddress());
	}

	@Test
	void invalidDriverIsRejectedAndNotPersisted() {
		String path = filePath();
		DriverRepository repo = new DriverRepository(path);
		Driver invalid = new Driver("bad-id", "John", 5, "Heavy", "12|Main St|Springfield|VIC|AU", "01-01-1990");

		assertThrows(IllegalArgumentException.class, () -> repo.add(invalid));
		assertEquals(0, new DriverRepository(path).count());
	}
	
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

	@Test
	void countReflectsStoredRecordsAcrossReload() {
		String path = filePath();
		DriverRepository repo = new DriverRepository(path);
		repo.add(valid("23#$abcdAB"));
		repo.add(valid("45@#wxyzCD"));
		assertEquals(2, repo.count());
		assertEquals(2, new DriverRepository(path).count());
	}
}
