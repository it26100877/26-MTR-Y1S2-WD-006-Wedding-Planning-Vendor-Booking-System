package com.wedding.member02_wedding_plan.model;

/**
 * A guest (or a party of guests) invited to the wedding.
 * Encapsulation + Inheritance (extends {@link PlanItem}).
 *
 * @author Member 02
 */
public class Guest extends PlanItem {

    public static final String RSVP_INVITED = "INVITED";
    public static final String RSVP_CONFIRMED = "CONFIRMED";
    public static final String RSVP_DECLINED = "DECLINED";

    private String guestName;
    private GuestGroup group;
    private String contact;
    private int partySize;     // the guest plus the people coming with them (1 or more)
    private String rsvpStatus; // INVITED | CONFIRMED | DECLINED
    private String note;

    public Guest(String guestId, String planId, String guestName, GuestGroup group, String contact,
                 int partySize, String rsvpStatus, String note, String createdAt) {
        super(guestId, planId, createdAt);
        this.guestName = guestName;
        this.group = group == null ? GuestGroup.OTHER : group;
        this.contact = contact;
        this.partySize = Math.max(1, partySize);
        this.rsvpStatus = rsvpStatus;
        this.note = note;
    }

    public String getGuestId() { return getItemId(); }
    public String getGuestName() { return guestName; }
    public GuestGroup getGroup() { return group; }
    public String getContact() { return contact; }
    public int getPartySize() { return partySize; }
    public String getRsvpStatus() { return rsvpStatus; }
    public String getNote() { return note; }

    public void setGuestName(String guestName) { this.guestName = guestName; }
    public void setGroup(GuestGroup group) { this.group = group == null ? GuestGroup.OTHER : group; }
    public void setContact(String contact) { this.contact = contact; }
    public void setPartySize(int partySize) { this.partySize = Math.max(1, partySize); }
    public void setRsvpStatus(String rsvpStatus) { this.rsvpStatus = rsvpStatus; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String getItemType() { return "GUEST"; }

    @Override
    public String getDisplayTitle() { return guestName; }

    @Override
    public String getDetailLine() {
        return group.getLabel() + ", party of " + partySize + ", " + rsvpStatus;
    }
}
