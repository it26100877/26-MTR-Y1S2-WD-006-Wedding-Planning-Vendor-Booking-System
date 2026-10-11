package lk.weddingly.repository;

import lk.weddingly.model.Booking;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/** Persists bookings as readable, escaped tab-separated text rows. */
public class BookingRepository {
    private final Path dataFile;

    public BookingRepository(ServletContext context) {
        this.dataFile = resolveProjectDataFile(context);
    }

    private Path resolveProjectDataFile(ServletContext context) {
        String deployedRoot = context.getRealPath("/");
        if (deployedRoot != null) {
            Path folder = Paths.get(deployedRoot).toAbsolutePath().normalize();
            while (folder != null) {
                if (Files.exists(folder.resolve("pom.xml"))) {
                    return folder.resolve(Paths.get("src", "main", "webapp", "WEB-INF", "data", "bookings.txt"));
                }
                folder = folder.getParent();
            }
        }
        return Paths.get(System.getProperty("user.home"), "Documents", "WeddinglyData", "bookings.txt");
    }

    public synchronized List<Booking> findAll() throws IOException {
        ensureFile();
        List<Booking> bookings = new ArrayList<>();
        boolean containsLegacyRows = false;
        for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            try {
                String[] f = line.split("\\t", -1);
                if (f.length != 10) continue;
                boolean readable = f[0].matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
                if (!readable) containsLegacyRows = true;
                for (int i = 0; i < f.length; i++) f[i] = readable ? unescape(f[i]) : decode(f[i]);
                bookings.add(new Booking(f[0], f[1], f[2], f[3], f[4], f[5],
                        LocalDate.parse(f[6]), f[7], new BigDecimal(f[8]), f[9]));
            } catch (RuntimeException ignored) { /* Skip a malformed row, keep other records readable. */ }
        }
        if (containsLegacyRows) writeAll(bookings);
        return bookings;
    }

    public synchronized Booking findById(String id) throws IOException {
        for (Booking booking : findAll()) if (booking.getId().equals(id)) return booking;
        return null;
    }

    public synchronized void save(Booking booking) throws IOException {
        List<Booking> all = findAll();
        if (booking.getId() == null || booking.getId().trim().isEmpty()) booking.setId(UUID.randomUUID().toString());
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(booking.getId())) { all.set(i, booking); updated = true; break; }
        }
        if (!updated) all.add(booking);
        writeAll(all);
    }

    public synchronized boolean delete(String id) throws IOException {
        List<Booking> all = findAll();
        boolean removed = all.removeIf(booking -> booking.getId().equals(id));
        if (removed) writeAll(all);
        return removed;
    }

    private void writeAll(List<Booking> all) throws IOException {
        List<String> rows = new ArrayList<>();
        rows.add("# id\tcustomerId\tcustomerName\tvendorId\tvendorName\tserviceName\teventDate\tstatus\tamount\tnotes");
        for (Booking b : all) rows.add(e(b.getId())+"\t"+e(b.getCustomerId())+"\t"+e(b.getCustomerName())+"\t"+
                e(b.getVendorId())+"\t"+e(b.getVendorName())+"\t"+e(b.getServiceName())+"\t"+e(b.getEventDate().toString())+"\t"+
                e(b.getStatus())+"\t"+b.getAmount().toPlainString()+"\t"+e(b.getNotes()));
        Files.write(dataFile, rows, StandardCharsets.UTF_8);
    }

    private void ensureFile() throws IOException {
        Files.createDirectories(dataFile.getParent());
        if (Files.notExists(dataFile)) Files.createFile(dataFile);
        if (!hasRecords(dataFile)) {
            Path oldDocumentsFile = Paths.get(System.getProperty("user.home"), "Documents", "WeddinglyData", "bookings.txt");
            String base = System.getProperty("catalina.base");
            Path oldTomcatFile = base == null ? null : Paths.get(base, "weddingly-data", "bookings.txt");
            if (!dataFile.equals(oldDocumentsFile) && hasRecords(oldDocumentsFile)) {
                Files.copy(oldDocumentsFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            } else if (oldTomcatFile != null && !dataFile.equals(oldTomcatFile) && hasRecords(oldTomcatFile)) {
                Files.copy(oldTomcatFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
    private boolean hasRecords(Path path) throws IOException {
        if (path == null || Files.notExists(path)) return false;
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) if (!line.trim().isEmpty() && !line.startsWith("#")) return true;
        return false;
    }
    private String e(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n");
    }
    private String decode(String value) { return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8); }
    private String unescape(String value) {
        StringBuilder result = new StringBuilder(); boolean escaped = false;
        for (int i=0; i<value.length(); i++) {
            char c=value.charAt(i);
            if (!escaped && c=='\\') { escaped=true; continue; }
            if (escaped) { if(c=='t') result.append('\t'); else if(c=='r') result.append('\r'); else if(c=='n') result.append('\n'); else result.append(c); escaped=false; }
            else result.append(c);
        }
        if(escaped) result.append('\\');
        return result.toString();
    }
}
