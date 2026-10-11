package lk.weddingly.model;

import java.time.LocalDate;

/** Base type shared by bookable wedding services. */
public abstract class BookingRecord {
    private String id;
    private LocalDate eventDate;
    private String notes;

    protected BookingRecord(String id, LocalDate eventDate, String notes) {
        this.id = id;
        this.eventDate = eventDate;
        this.notes = notes;
    }

    public abstract String getRecordType();
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
