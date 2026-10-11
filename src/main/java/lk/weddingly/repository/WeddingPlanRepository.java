package lk.weddingly.repository;

import lk.weddingly.model.WeddingPlan;
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

/** Text-file persistence. Each field is URL-safe Base64 encoded to preserve delimiters/newlines. */
public class WeddingPlanRepository {
    private final Path dataFile;

    public WeddingPlanRepository(ServletContext context) {
        this.dataFile = resolveProjectDataFile(context);
    }

    private Path resolveProjectDataFile(ServletContext context) {
        // IntelliJ's exploded Tomcat deployment lives under the project build folders.
        // Walk up from the deployed app until its pom.xml is found, then use the source data file.
        String deployedRoot = context.getRealPath("/");
        if (deployedRoot != null) {
            Path folder = Paths.get(deployedRoot).toAbsolutePath().normalize();
            while (folder != null) {
                if (Files.exists(folder.resolve("pom.xml"))) {
                    return folder.resolve(Paths.get("src", "main", "webapp", "WEB-INF", "data", "weddings.txt"));
                }
                folder = folder.getParent();
            }
        }
        // Fallback for a WAR deployed outside the IntelliJ project.
        return Paths.get(System.getProperty("user.home"), "Documents", "WeddinglyData", "weddings.txt");
    }

    public synchronized List<WeddingPlan> findAll() throws IOException {
        ensureFile();
        List<WeddingPlan> plans = new ArrayList<>();
        boolean containsLegacyRows = false;
        for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            try {
                String[] f = line.split("\\t", -1);
                if (f.length != 8) continue;
                // Read both legacy Base64 rows and the current readable tab-separated rows.
                boolean readable = f[0].matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
                if (!readable) containsLegacyRows = true;
                for (int i = 0; i < f.length; i++) f[i] = readable ? unescape(f[i]) : decode(f[i]);
                plans.add(new WeddingPlan(f[0], f[1], LocalDate.parse(f[2]), f[3],
                        Integer.parseInt(f[4]), new BigDecimal(f[5]), f[6], f[7]));
            } catch (RuntimeException ignored) {
                // Ignore malformed rows so one damaged line does not hide every saved plan.
            }
        }
        if (containsLegacyRows) writeAll(plans);
        return plans;
    }

    public synchronized WeddingPlan findById(String id) throws IOException {
        for (WeddingPlan plan : findAll()) if (plan.getId().equals(id)) return plan;
        return null;
    }

    public synchronized void save(WeddingPlan plan) throws IOException {
        List<WeddingPlan> plans = findAll();
        if (plan.getId() == null || plan.getId().trim().isEmpty()) plan.setId(UUID.randomUUID().toString());
        boolean replaced = false;
        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i).getId().equals(plan.getId())) { plans.set(i, plan); replaced = true; break; }
        }
        if (!replaced) plans.add(plan);
        writeAll(plans);
    }

    public synchronized boolean delete(String id) throws IOException {
        List<WeddingPlan> plans = findAll();
        boolean removed = plans.removeIf(plan -> plan.getId().equals(id));
        if (removed) writeAll(plans);
        return removed;
    }

    private void writeAll(List<WeddingPlan> plans) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# id\tcoupleNames\tweddingDate\tvenue\tguestCount\tbudget\ttheme\tnotes");
        for (WeddingPlan p : plans) {
            lines.add(escape(p.getId()) + "\t" + escape(p.getCoupleNames()) + "\t" + escape(p.getWeddingDate().toString())
                    + "\t" + escape(p.getVenue()) + "\t" + p.getGuestCount()
                    + "\t" + p.getBudget().toPlainString() + "\t" + escape(p.getTheme()) + "\t" + escape(p.getNotes()));
        }
        Files.write(dataFile, lines, StandardCharsets.UTF_8);
    }

    private void ensureFile() throws IOException {
        Files.createDirectories(dataFile.getParent());
        if (Files.notExists(dataFile)) Files.createFile(dataFile);
        if (!hasRecords(dataFile)) {
            Path documentsFile = Paths.get(System.getProperty("user.home"), "Documents", "WeddinglyData", "weddings.txt");
            String base = System.getProperty("catalina.base");
            Path oldTomcatFile = base == null ? null : Paths.get(base, "weddingly-data", "weddings.txt");
            if (!dataFile.equals(documentsFile) && hasRecords(documentsFile)) {
                Files.copy(documentsFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            } else if (oldTomcatFile != null && !dataFile.equals(oldTomcatFile) && hasRecords(oldTomcatFile)) {
                Files.copy(oldTomcatFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private boolean hasRecords(Path path) throws IOException {
        if (path == null || Files.notExists(path)) return false;
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (!line.trim().isEmpty() && !line.startsWith("#")) return true;
        }
        return false;
    }

    private String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n");
    }
    private String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!escaped && c == '\\') { escaped = true; continue; }
            if (escaped) {
                if (c == 't') result.append('\t');
                else if (c == 'r') result.append('\r');
                else if (c == 'n') result.append('\n');
                else result.append(c);
                escaped = false;
            } else result.append(c);
        }
        if (escaped) result.append('\\');
        return result.toString();
    }
}
