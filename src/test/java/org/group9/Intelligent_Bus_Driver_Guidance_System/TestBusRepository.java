package org.group9.Intelligent_Bus_Driver_Guidance_System;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

class TestBusRepository {

	@TempDir
	Path tempDir;

	private String filePath() {
		return tempDir.resolve("buses.txt").toString();
	}

	@Test
	void validBusIsStoredAndPersisted() {
		String path = filePath();
		new BusRepository(path).add(new Bus("12345678", 40, 80.5, "Diesel"));

		Bus loaded = new BusRepository(path).retrieve("12345678");
		assertEquals(40, loaded.getCapacity());
		assertEquals("Diesel", loaded.getFuelType());
		assertEquals(80.5, loaded.getFuelLevel());
	}

	@Test
	void invalidBusIsRejectedAndNotPersisted() {
		String path = filePath();
		BusRepository repo = new BusRepository(path);

		assertThrows(IllegalArgumentException.class, () -> repo.add(new Bus("123", 40, 80.0, "Diesel")));
		assertEquals(0, new BusRepository(path).count());
	}

	@Test
	void capacityDecreaseUpdateIsPersisted() {
		String path = filePath();
		BusRepository repo = new BusRepository(path);
		repo.add(new Bus("12345678", 50, 80.0, "Diesel"));
		repo.update(new Bus("12345678", 35, 70.0, "Hybrid"));

		Bus loaded = new BusRepository(path).retrieve("12345678");
		assertEquals(35, loaded.getCapacity());
		assertEquals("Hybrid", loaded.getFuelType());
	}
	
	 @Test
	    void capacityIncreaseRejectedAndNotPersisted() {
	        String path = filePath();
	        BusRepository repo = new BusRepository(path);
	        repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
	 
	        assertThrows(IllegalArgumentException.class, () -> repo.update(new Bus("12345678", 50, 80.0, "Diesel")));
	 
	        Bus reloaded = new BusRepository(path).retrieve("12345678");
	        assertEquals(40, reloaded.getCapacity());
	    }

	@Test
	void countReflectsStoredRecordsAcrossReload() {
		String path = filePath();
		BusRepository repo = new BusRepository(path);
		repo.add(new Bus("12345678", 40, 80.0, "Diesel"));
		repo.add(new Bus("87654321", 30, 60.0, "Electricity"));
		assertEquals(2, repo.count());
		assertEquals(2, new BusRepository(path).count());
	}
}
