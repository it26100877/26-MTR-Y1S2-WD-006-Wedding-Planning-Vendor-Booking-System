package com.wedding.common.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Utility class for file-based persistence.
 * Handles reading and writing CSV-style data files.
 * All data files are stored in the /data directory.
 *
 * @author Common Module
 */
public class FileUtil {

 private static String getDataDir() {
  String configured = System.getProperty("eclat.data.dir", "data");
  File dir = new File(configured);
  if (!dir.exists() && !dir.mkdirs()) {
   throw new IllegalStateException("Unable to create data directory: " + configured);
  }
  return configured;
 }

 /**
 * Returns the full path for a data file.
 */
 public static String getFilePath(String filename) {
 return getDataDir() + File.separator + filename;
 }

 /**
 * Reads all lines from a data file.
 * Returns an empty list if the file does not exist.
 */
 public static List<String> readLines(String filename) {
 List<String> lines = new ArrayList<>();
 File file = new File(getFilePath(filename));
 if (!file.exists()) return lines;
 try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
 String line;
 while ((line = br.readLine()) != null) {
 if (!line.trim().isEmpty()) {
 lines.add(line.trim());
 }
 }
 } catch (IOException e) {
 throw new UncheckedIOException("Unable to read data file: " + filename, e);
 }
 return lines;
 }

 /**
 * Writes all lines to a data file, overwriting existing content.
 */
 public static void writeLines(String filename, List<String> lines) {
 File file = new File(getFilePath(filename));
 File parent = file.getAbsoluteFile().getParentFile();
 if (parent != null && !parent.exists() && !parent.mkdirs()) {
  throw new UncheckedIOException("Unable to create data directory for: " + filename,
          new IOException("Could not create " + parent));
 }
 Path target = file.toPath();
 Path temporary = null;
 try {
  temporary = Files.createTempFile(parent.toPath(), file.getName(), ".tmp");
  try (BufferedWriter bw = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
   for (String line : lines) {
    bw.write(line == null ? "" : line);
    bw.newLine();
   }
  }
  try {
   Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
  } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
   Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
  }
 } catch (IOException e) {
  throw new UncheckedIOException("Unable to write data file: " + filename, e);
 } finally {
  if (temporary != null) {
   try { Files.deleteIfExists(temporary); } catch (IOException ignored) { }
  }
 }
 }

 /**
 * Appends a single line to a data file.
 */
 public static void appendLine(String filename, String line) {
 File file = new File(getFilePath(filename));
 try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
 bw.write(line);
 bw.newLine();
 } catch (IOException e) {
 throw new UncheckedIOException("Unable to append to data file: " + filename, e);
 }
 }

 /**
 * Escapes a CSV field value (handles commas and quotes).
 */
 public static String escape(String value) {
 if (value == null) return "";
 value = value.replace("\"", "\"\"");
 if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
 value = "\"" + value + "\"";
 }
 return value;
 }

 /**
 * Splits a CSV line, respecting quoted fields.
 */
 public static String[] splitCSV(String line) {
 List<String> result = new ArrayList<>();
 boolean inQuotes = false;
 StringBuilder current = new StringBuilder();
 for (int i = 0; i < line.length(); i++) {
 char c = line.charAt(i);
 if (c == '"') {
 if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
 current.append('"');
 i++;
 } else {
 inQuotes = !inQuotes;
 }
 } else if (c == ',' && !inQuotes) {
 result.add(current.toString());
 current = new StringBuilder();
 } else {
 current.append(c);
 }
 }
 result.add(current.toString());
 return result.toArray(new String[0]);
 }

 /**
 * Generates a simple unique ID with a prefix.
 * Format: PREFIX-timestamp-random4digits
 */
 public static String generateId(String prefix) {
 String safePrefix = (prefix == null || prefix.trim().isEmpty()) ? "ID" : prefix.trim().toUpperCase(Locale.ROOT);
 String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
 return safePrefix + "-" + System.currentTimeMillis() + "-" + suffix;
 }
}

