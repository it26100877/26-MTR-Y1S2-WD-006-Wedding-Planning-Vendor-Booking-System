package com.wedding.common.repository;

import com.wedding.common.utils.FileUtil;
import java.util.*;

/**
 * Abstract base repository with file-based persistence.
 * Concrete repositories extend this class and implement serialization.
 * Demonstrates Inheritance and Abstraction.
 *
 * @param <T> Entity type
 *
 * @author Common Module
 */
public abstract class AbstractFileRepository<T> implements Repository<T, String> {

 /** The filename to store this entity's data. */
 protected final String filename;

 /** In-memory cache of all entities. */
 protected final Map<String, T> cache = new LinkedHashMap<>();

 protected AbstractFileRepository(String filename) {
 this.filename = filename;
 loadAll();
 }

 /** Serializes an entity to a CSV line. */
 protected abstract String serialize(T entity);

 /** Deserializes a CSV line back to an entity. */
 protected abstract T deserialize(String line);

 /** Returns the unique string ID of an entity. */
 protected abstract String getId(T entity);

 @Override
 public synchronized void save(T entity) {
 cache.put(getId(entity), entity);
 persist();
 }

 @Override
 public synchronized Optional<T> findById(String id) {
 return Optional.ofNullable(cache.get(id));
 }

 @Override
 public synchronized List<T> findAll() {
 return new ArrayList<>(cache.values());
 }

 @Override
 public synchronized boolean deleteById(String id) {
 boolean removed = cache.remove(id) != null;
 if (removed) persist();
 return removed;
 }

 @Override
 public synchronized int count() {
 return cache.size();
 }

 /** Writes all cached entities to disk. */
 protected synchronized void persist() {
 List<String> lines = new ArrayList<>();
 for (T entity : cache.values()) {
 lines.add(serialize(entity));
 }
 FileUtil.writeLines(filename, lines);
 }

 /** Loads all entities from disk into the cache. */
 private void loadAll() {
 cache.clear();
 List<String> lines = FileUtil.readLines(filename);
 for (String line : lines) {
 try {
 T entity = deserialize(line);
 if (entity != null) {
 cache.put(getId(entity), entity);
 }
 } catch (Exception e) {
 System.err.println("[Repo] Skipping corrupt line in " + filename + ": " + e.getMessage());
 }
 }
 }
}

