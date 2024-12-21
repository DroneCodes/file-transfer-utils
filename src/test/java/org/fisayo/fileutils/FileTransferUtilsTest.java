package org.fisayo.fileutils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class FileTransferUtilsTest {

    @TempDir
    Path tempDir;

    private File sourceFile;
    private File destFile;
    private static final String TEST_CONTENT = "Test content for file transfer";

    @BeforeEach
    void setUp() throws IOException {
        sourceFile = tempDir.resolve("source.txt").toFile();
        destFile = tempDir.resolve("destination.txt").toFile();
        Files.writeString(sourceFile.toPath(), TEST_CONTENT);
    }

    @Test
    void testSyncFileCopy() throws IOException {
        // Test synchronous copy
        FileTransferUtils.copyFile(sourceFile.getPath(), destFile.getPath());

        assertTrue(destFile.exists());
        assertEquals(TEST_CONTENT, Files.readString(destFile.toPath()));
    }

    @Test
    void testAsyncFileCopy() throws Exception {
        // Test async copy with progress tracking
        AtomicInteger progressValue = new AtomicInteger(0);

        CompletableFuture<Void> future = FileTransferUtils.copyFileAsync(
                sourceFile.getPath(),
                destFile.getPath(),
                (progress, transferred, total) -> progressValue.set(progress)
        );

        future.get(); // Wait for completion

        assertTrue(destFile.exists());
        assertEquals(TEST_CONTENT, Files.readString(destFile.toPath()));
        assertEquals(100, progressValue.get());
    }

    @Test
    void testMoveFile() throws IOException {
        // Test file move
        FileTransferUtils.moveFile(sourceFile.getPath(), destFile.getPath());

        assertFalse(sourceFile.exists());
        assertTrue(destFile.exists());
        assertEquals(TEST_CONTENT, Files.readString(destFile.toPath()));
    }

    @Test
    void testValidateTransfer() {
        // Test validation with non-existent source
        File nonExistentFile = tempDir.resolve("nonexistent.txt").toFile();

        assertThrows(IllegalArgumentException.class, () ->
                FileTransferUtils.validateTransfer(
                        nonExistentFile.getPath(),
                        destFile.getPath()
                )
        );

        // Test validation with valid files
        assertDoesNotThrow(() ->
                FileTransferUtils.validateTransfer(
                        sourceFile.getPath(),
                        destFile.getPath()
                )
        );
    }

    @Test
    void testLargeFileTransfer() throws IOException {
        // Create a larger test file (1MB)
        byte[] data = new byte[1024 * 1024];
        Files.write(sourceFile.toPath(), data);

        FileTransferUtils.copyFile(sourceFile.getPath(), destFile.getPath());

        assertTrue(destFile.exists());
        assertEquals(Files.size(sourceFile.toPath()), Files.size(destFile.toPath()));
    }
}