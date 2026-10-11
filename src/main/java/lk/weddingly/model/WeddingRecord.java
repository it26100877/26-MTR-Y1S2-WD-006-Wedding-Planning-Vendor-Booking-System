package lk.weddingly.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Shared contract for wedding records. Subclasses provide their own label. */
public abstract class WeddingRecord {
    private String id;
    private String coupleNames;
    private LocalDate weddingDate;
    private String venue;
    private int guestCount;
    private BigDecimal budget;
    private String theme;
    private String notes;

    protected WeddingRecord(String id, String coupleNames, LocalDate weddingDate, String venue,
                            int guestCount, BigDecimal budget, String theme, String notes) {
        this.id = id;
        this.coupleNames = coupleNames;
        this.weddingDate = weddingDate;
        this.venue = venue;
        this.guestCount = guestCount;
        this.budget = budget;
        this.theme = theme;
        this.notes = notes;
    }

    public abstract String getPlanLabel();
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCoupleNames() { return coupleNames; }
    public void setCoupleNames(String coupleNames) { this.coupleNames = coupleNames; }
    public LocalDate getWeddingDate() { return weddingDate; }
    public void setWeddingDate(LocalDate weddingDate) { this.weddingDate = weddingDate; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
