package in.zeta.zea_opc_b03_digital_kyc.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void createDirectory_Success() throws IOException {
        Path newDir = tempDir.resolve("testdir");
        
        Files.createDirectories(newDir);
        
        assertTrue(Files.exists(newDir));
        assertTrue(Files.isDirectory(newDir));
    }

    @Test
    void deleteFile_Success() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.createFile(testFile);
        
        Files.delete(testFile);
        
        assertFalse(Files.exists(testFile));
    }

    @Test
    void fileExists_ReturnsTrue() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.createFile(testFile);
        
        assertTrue(Files.exists(testFile));
    }

    @Test
    void fileExists_ReturnsFalse() {
        Path testFile = tempDir.resolve("nonexistent.txt");
        
        assertFalse(Files.exists(testFile));
    }
}
