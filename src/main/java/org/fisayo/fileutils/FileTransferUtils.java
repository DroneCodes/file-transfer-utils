package org.fisayo.fileutils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;

public class FileTransferUtils {

    /**
     * Copies a file from source to destination using NIO channels for better performance
     * @param source Source file path
     * @param destination Destination file path
     * @throws IOException If an I/O error occurs
     */
    public static void copyFile(String source, String destination) throws IOException {
        try (FileChannel sourceChannel = FileChannel.open(Path.of(source), StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(Path.of(destination),
                     StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        }
    }

    /**
     * Asynchronously copies a file with progress tracking
     * @param source Source file path
     * @param destination Destination file path
     * @param progressCallback Callback to track progress
     * @return CompletableFuture that completes when the transfer is done
     */
    public static CompletableFuture<Void> copyFileAsync(String  source, String destination, ProgressCallback progressCallback) {
        return CompletableFuture.runAsync(() -> {
            try {
                File sourceFile = new File(source);
                long totalBytes = sourceFile.length();
                long transferredBytes = 0;

                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
                     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destination))) {

                    byte[] buffer = new byte[8192];
                    int bytesRead;

                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                        transferredBytes += bytesRead;

                        if (progressCallback != null) {
                            int progress = (int) ((transferredBytes * 100) / totalBytes);
                            progressCallback.onProgress(progress, transferredBytes, totalBytes);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("File transfer failed", e);
            }
        });
    }

    /**
     * Interface for tracking file transfer progress
     */
    public interface ProgressCallback {
        void onProgress(int percentComplete, long bytesTransferred, long totalBytes);
    }

    /**
     * Moves a file from source to destination
     * @param source Source file path
     * @param destination Destination file path
     * @throws IOException If an I/O error occurs
     */
    public static void moveFile(String source, String destination) throws IOException {
        Path sourcePath = Path.of(source);
        Path destPath = Path.of(destination);

        try {
            // Try atomic move first
            Files.move(sourcePath, destPath, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            // Fallback to copy and delete
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(sourcePath);
        }
    }

    /**
     * Validates if a file transfer operation is possible
     * @param source Source file path
     * @param destination Destination file path
     * @throws IllegalArgumentException If the transfer is not possible
     */
    public static void validateTransfer(String source, String destination) {
        File sourceFile = new File(source);
        File destFile = new File(destination);

        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("Source file does not exist: " + source);
        }

        if (!sourceFile.canRead()) {
            throw new IllegalArgumentException("Cannot read source file: " + source);
        }

        File destDir = destFile.getParentFile();
        if (destDir != null &&  !destDir.exists() && !destDir.mkdirs()) {
            throw  new IllegalArgumentException("Cannot create destination directory: " + destDir);
        }
    }
}
