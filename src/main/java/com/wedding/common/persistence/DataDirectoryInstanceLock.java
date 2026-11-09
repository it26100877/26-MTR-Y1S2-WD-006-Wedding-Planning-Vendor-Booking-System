package com.wedding.common.persistence;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Enforces the supported single-instance deployment model for CSV persistence.
 * This prevents two JVMs from loading stale in-memory CSV caches and overwriting
 * each other's updates in the same data directory.
 *
 * For horizontal scaling, migrate repositories to a shared transactional database
 * and replace this guard with distributed coordination.
 */
@Component
public class DataDirectoryInstanceLock {
    private static final Logger LOG = LoggerFactory.getLogger(DataDirectoryInstanceLock.class);
    private FileChannel channel;
    private FileLock lock;

    @PostConstruct
    public void acquire() {
        try {
            Path directory = Path.of(System.getProperty("eclat.data.dir", "data")).toAbsolutePath();
            Files.createDirectories(directory);
            Path lockFile = directory.resolve(".eclat-instance.lock");
            channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            try {
                lock = channel.tryLock();
            } catch (OverlappingFileLockException ex) {
                lock = null;
            }
            if (lock == null) {
                channel.close();
                throw new IllegalStateException("Another Éclat application instance is already using data directory "
                        + directory + ". CSV persistence supports one instance only.");
            }
            LOG.info("Acquired exclusive CSV data-directory lock at {}", directory);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to lock the Éclat CSV data directory.", ex);
        }
    }

    @PreDestroy
    public void release() {
        try {
            if (lock != null && lock.isValid()) lock.release();
        } catch (IOException ex) {
            LOG.warn("Could not release CSV data-directory lock cleanly.", ex);
        }
        try {
            if (channel != null && channel.isOpen()) channel.close();
        } catch (IOException ex) {
            LOG.warn("Could not close CSV data-directory lock channel cleanly.", ex);
        }
    }
}
