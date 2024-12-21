package org.fisayo.fileutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class FileTransferDemo {
    public static void main(String[] args) {
        // Create demo directories and files
        try {
            // Setup test directories
            Path sourceDir = Path.of("demo-source");
            Path destDir = Path.of("demo-destination");
            Files.createDirectories(sourceDir);
            Files.createDirectories(destDir);

            // Create a test file
            Path testFile = sourceDir.resolve("test.txt");
            Files.writeString(testFile, "This is a test file for demonstrating file transfer utilities!");

            System.out.println("🚀 FileTransferUtils Demo\n");

            // 1. Simple synchronous file copy
            System.out.println("1. Demonstrating synchronous file copy...");
            Path syncDestFile = destDir.resolve("sync-copy.txt");
            FileTransferUtils.copyFile(testFile.toString(), syncDestFile.toString());
            System.out.println("✅ Synchronous copy completed!\n");

            // 2. Asynchronous copy with progress tracking
            System.out.println("2. Demonstrating asynchronous copy with progress...");
            Path asyncDestFile = destDir.resolve("async-copy.txt");
            CompletableFuture<Void> future = FileTransferUtils.copyFileAsync(
                    testFile.toString(),
                    asyncDestFile.toString(),
                    (progress, transferred, total) -> {
                        System.out.printf("📊 Progress: %d%% (%d/%d bytes)%n",
                                progress, transferred, total);
                    }
            );
            future.join(); // Wait for completion
            System.out.println("✅ Asynchronous copy completed!\n");

            // 3. File move operation
            System.out.println("3. Demonstrating file move...");
            Path moveSource = sourceDir.resolve("move-test.txt");
            Path moveDest = destDir.resolve("moved-file.txt");
            Files.writeString(moveSource, "This file will be moved!");
            FileTransferUtils.moveFile(moveSource.toString(), moveDest.toString());
            System.out.println("✅ File move completed!");
            System.out.println("   Original file exists: " + Files.exists(moveSource));
            System.out.println("   Destination file exists: " + Files.exists(moveDest) + "\n");

            // 4. Validation demonstration
            System.out.println("4. Demonstrating transfer validation...");
            try {
                FileTransferUtils.validateTransfer(
                        "nonexistent-file.txt",
                        destDir.resolve("validation-test.txt").toString()
                );
            } catch (IllegalArgumentException e) {
                System.out.println("🔍 Validation caught invalid transfer: " + e.getMessage());
            }

            // Cleanup
            System.out.println("\nCleaning up demo files...");
            Files.walk(sourceDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path);
                        }
                    });
            Files.walk(destDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path);
                        }
                    });
            System.out.println("✨ Demo completed successfully!");

        } catch (IOException e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}