# FileTransferUtils

A high-performance Java library for file transfer operations with support for both synchronous and asynchronous transfers, progress tracking, and atomic operations.

## Features

- **High-Performance File Copying**: Utilizes Java NIO channels for optimal performance
- **Asynchronous Operations**: Support for non-blocking file transfers with progress tracking
- **Atomic Move Operations**: Ensures file integrity during move operations
- **Progress Tracking**: Real-time progress monitoring for long-running transfers
- **Transfer Validation**: Built-in validation of source and destination paths
- **Thread-Safe**: Safe to use in concurrent environments

## Installation

### Maven

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.fileutils</groupId>
    <artifactId>file-transfer-utils</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

Add this to your `build.gradle`:

```groovy
implementation 'com.fileutils:file-transfer-utils:1.0.0'
```

## Usage

### Simple File Copy

```java
import com.fileutils.FileTransferUtils;

// Synchronous copy
try {
    FileTransferUtils.copyFile("source.txt", "destination.txt");
} catch (IOException e) {
    e.printStackTrace();
}
```

### Asynchronous Copy with Progress Tracking

```java
FileTransferUtils.copyFileAsync("source.txt", "destination.txt",
    (progress, transferred, total) -> {
        System.out.printf("Progress: %d%% (%d/%d bytes)%n", 
            progress, transferred, total);
    })
    .thenRun(() -> System.out.println("Transfer complete!"))
    .exceptionally(throwable -> {
        System.err.println("Transfer failed: " + throwable.getMessage());
        return null;
    });
```

### Moving Files

```java
try {
    FileTransferUtils.moveFile("source.txt", "destination.txt");
} catch (IOException e) {
    e.printStackTrace();
}
```

### Validation

```java
try {
    FileTransferUtils.validateTransfer("source.txt", "destination.txt");
    // Proceed with transfer
} catch (IllegalArgumentException e) {
    System.err.println("Invalid transfer: " + e.getMessage());
}
```

## Requirements

- Java 11 or higher
- No additional dependencies required

## Building from Source

1. Clone the repository:
```bash
git clone https://github.com/DroneCodes/file-transfer-utils.git
```

2. Build with Maven:
```bash
cd file-transfer-utils
mvn clean install
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have questions, please file an issue on the GitHub repository.

## Acknowledgments

- Inspired by the need for a simple, high-performance file transfer solution
- Built using Java NIO for optimal performance
- Designed with concurrent operations in mind