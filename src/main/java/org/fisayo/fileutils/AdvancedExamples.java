package org.fisayo.fileutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class AdvancedExamples {

    /**
     * Example of bulk file transfer with progress tracking
     */
    public static void bulkTransferExample(String sourceDir, String destDir) {
        try (Stream<Path> paths = Files.walk(Paths.get(sourceDir))) {
            ExecutorService executor = Executors.newFixedThreadPool(3);

            paths.filter(Files::isRegularFile)
                    .map(path -> CompletableFuture.runAsync(() -> {
                        try {
                            Path relativePath = Paths.get(sourceDir).relativize(path);
                            Path destination = Paths.get(destDir, relativePath.toString());

                            // Create parent directories if they don't exist
                            Files.createDirectories(destination.getParent());

                            // Copy with progress tracking
                            FileTransferUtils.copyFileAsync(
                                    path.toString(),
                                    destination.toString(),
                                    (progress, transferred, total) -> {
                                        System.out.printf("File: %s, Progress: %d%%%n",
                                                relativePath, progress);
                                    }
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor))
                    .forEach(CompletableFuture::join);

            executor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Example of recursive directory copy with filtering
     */
    public static void filteredCopyExample(String sourceDir, String destDir, String fileExtension) {
        try (Stream<Path> paths = Files.walk(Paths.get(sourceDir))) {
            paths.filter(path -> Files.isRegularFile(path) &&
                            path.toString().endsWith(fileExtension))
                    .forEach(path -> {
                        try {
                            Path relativePath = Paths.get(sourceDir).relativize(path);
                            Path destination = Paths.get(destDir, relativePath.toString());

                            Files.createDirectories(destination.getParent());
                            FileTransferUtils.copyFile(path.toString(), destination.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Example of file transfer with retry mechanism
     */
    public static void retryTransferExample(String source, String destination, int maxRetries) {
        int retries = 0;
        boolean success = false;

        while (!success && retries < maxRetries) {
            try {
                FileTransferUtils.copyFile(source, destination);
                success = true;
            } catch (IOException e) {
                retries++;
                System.out.printf("Transfer failed, attempt %d of %d%n", retries, maxRetries);

                if (retries < maxRetries) {
                    try {
                        Thread.sleep(1000 * retries); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Example usage of the advanced features
     */
    public static void main(String[] args) {
        // Bulk transfer example
        System.out.println("Running bulk transfer example...");
        bulkTransferExample("/source/directory", "/destination/directory");

        // Filtered copy example (only copy .txt files)
        System.out.println("\nRunning filtered copy example...");
        filteredCopyExample("/source/directory", "/destination/directory", ".txt");

        // Retry transfer example
        System.out.println("\nRunning retry transfer example...");
        retryTransferExample("/source/file.txt", "/destination/file.txt", 3);
    }
}