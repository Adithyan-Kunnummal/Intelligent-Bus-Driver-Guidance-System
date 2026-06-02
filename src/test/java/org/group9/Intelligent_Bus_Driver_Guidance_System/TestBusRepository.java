package org.group9.Intelligent_Bus_Driver_Guidance_System;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

class TestBusRepository {

	private final Path targetDir = Paths.get("test-output");
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
		repo.update(new Bus("12345678", 35, 80.0, "Diesel"));

		Bus loaded = new BusRepository(path).retrieve("12345678");
		assertEquals(35, loaded.getCapacity());
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
