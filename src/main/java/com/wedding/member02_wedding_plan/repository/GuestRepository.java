package com.wedding.member02_wedding_plan.repository;

import com.wedding.member02_wedding_plan.model.Guest;
import com.wedding.member02_wedding_plan.model.GuestGroup;

/**
 * File-based repository for Guest entities (guests.csv).
 * Order: guestId|planId|guestName|group|contact|partySize|rsvpStatus|note|createdAt
 *
 * @author Member 02
 */
public class GuestRepository extends PlanItemRepository<Guest> {

    private static final String FILE_NAME = "guests.csv";

    public GuestRepository() {
        super(FILE_NAME);
    }

    /** Uses a different file name (for unit tests). */
    public GuestRepository(String filename) {
        super(filename);
    }

    @Override
    protected String serialize(Guest guest) {
        return String.join(SEPARATOR,
                safe(guest.getGuestId()),
                safe(guest.getPlanId()),
                safe(guest.getGuestName()),
                guest.getGroup().getCode(),
                safe(guest.getContact()),
                Integer.toString(guest.getPartySize()),
                safe(guest.getRsvpStatus()),
                safe(guest.getNote()),
                safe(guest.getCreatedAt())
        );
    }

    @Override
    protected Guest deserialize(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split(SAFE_SEP, -1);
        if (p.length < 9) return null;
        int party;
        try {
            party = Integer.parseInt(p[5].trim());
        } catch (NumberFormatException e) {
            party = 1;
        }
        String rsvp = p[6].trim();
        if (!Guest.RSVP_CONFIRMED.equals(rsvp) && !Guest.RSVP_DECLINED.equals(rsvp)) {
            rsvp = Guest.RSVP_INVITED;
        }
        return new Guest(
                p[0].trim(),
                p[1].trim(),
                p[2].trim(),
                GuestGroup.fromCode(p[3]),
                p[4].trim(),
                party,
                rsvp,
                p[7].trim(),
                p[8].trim()
        );
    }
}
