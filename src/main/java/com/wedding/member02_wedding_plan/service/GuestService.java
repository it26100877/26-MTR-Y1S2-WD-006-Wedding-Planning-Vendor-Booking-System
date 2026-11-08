package com.wedding.member02_wedding_plan.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member02_wedding_plan.model.Guest;
import com.wedding.member02_wedding_plan.model.GuestGroup;
import com.wedding.member02_wedding_plan.model.GuestStats;
import com.wedding.member02_wedding_plan.repository.GuestRepository;

import java.util.Comparator;
import java.util.List;

/**
 * Business-logic service for the guest list and RSVP tracking.
 * Extends BaseService (Inheritance). A guest entry has a party size from 1 to 10.
 *
 * @author Member 02
 */
public class GuestService extends BaseService {

    public static final int MAX_PARTY_SIZE = 10;

    private final GuestRepository repository;

    public GuestService(GuestRepository repository) {
        this.repository = repository;
    }

    // ─── Create ──────────────────────────────────────────────────────────────────

    public Guest addGuest(String planId, String guestName, String groupCode, String contact,
                          String partySize, String rsvpStatus, String note) {
        requireNonEmpty(planId, "Plan ID");
        validateFields(guestName, groupCode, contact, note);
        Guest guest = new Guest(FileUtil.generateId("GST"), planId, guestName.trim(),
                GuestGroup.fromCode(groupCode), clean(contact), parsePartySize(partySize),
                parseRsvp(rsvpStatus), clean(note), ValidationUtil.nowDateTime());
        repository.save(guest);
        log("Added guest " + guest.getGuestId() + " to plan " + planId);
        return guest;
    }

    // ─── Update ──────────────────────────────────────────────────────────────────

    public Guest updateGuest(String planId, String guestId, String guestName, String groupCode,
                             String contact, String partySize, String rsvpStatus, String note) {
        Guest guest = findInPlan(planId, guestId);
        validateFields(guestName, groupCode, contact, note);
        int party = parsePartySize(partySize);
        String rsvp = parseRsvp(rsvpStatus);

        guest.setGuestName(guestName.trim());
        guest.setGroup(GuestGroup.fromCode(groupCode));
        guest.setContact(clean(contact));
        guest.setPartySize(party);
        guest.setRsvpStatus(rsvp);
        guest.setNote(clean(note));
        repository.save(guest);
        log("Updated guest " + guestId);
        return guest;
    }

    /** Changes only the RSVP answer of a guest. */
    public Guest setRsvp(String planId, String guestId, String rsvpStatus) {
        Guest guest = findInPlan(planId, guestId);
        guest.setRsvpStatus(parseRsvp(rsvpStatus));
        repository.save(guest);
        log("RSVP of " + guestId + " is now " + guest.getRsvpStatus());
        return guest;
    }

    // ─── Delete ──────────────────────────────────────────────────────────────────

    public boolean deleteGuest(String planId, String guestId) {
        findInPlan(planId, guestId);
        boolean removed = repository.deleteById(guestId);
        if (removed) log("Deleted guest " + guestId);
        return removed;
    }

    public int deleteByPlan(String planId) {
        int removed = repository.deleteByPlanId(planId);
        if (removed > 0) log("Deleted " + removed + " guest(s) of plan " + planId);
        return removed;
    }

    // ─── Queries ─────────────────────────────────────────────────────────────────

    /** Guests of a plan, sorted by name. */
    public List<Guest> getGuestsByPlan(String planId) {
        List<Guest> guests = repository.findByPlanId(planId);
        guests.sort(Comparator.comparing(g -> g.getGuestName().toLowerCase()));
        return guests;
    }

    /**
     * Headcounts for the guest list. Declined guests are counted separately; confirmed
     * and still-waiting guests together make up the attending estimate.
     *
     * @param expectedGuests the plan's expected guest count (0 if not set)
     */
    public GuestStats buildStats(List<Guest> guests, int expectedGuests) {
        int confirmed = 0, pending = 0, declined = 0;
        for (Guest g : guests) {
            if (Guest.RSVP_CONFIRMED.equals(g.getRsvpStatus())) confirmed += g.getPartySize();
            else if (Guest.RSVP_DECLINED.equals(g.getRsvpStatus())) declined += g.getPartySize();
            else pending += g.getPartySize();
        }
        return new GuestStats(guests.size(), confirmed, pending, declined, expectedGuests);
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────────

    private Guest findInPlan(String planId, String guestId) {
        Guest guest = repository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found: " + guestId));
        if (planId == null || !planId.equals(guest.getPlanId())) {
            throw new BusinessRuleException("This guest does not belong to the selected plan.");
        }
        return guest;
    }

    private void validateFields(String guestName, String groupCode, String contact, String note) {
        requireNonEmpty(guestName, "Guest name");
        int length = guestName.trim().length();
        if (length < 2 || length > 80) {
            throw new ValidationException("Guest name must be between 2 and 80 characters.");
        }
        if (contact != null && contact.trim().length() > 80) {
            throw new ValidationException("Contact must be 80 characters or fewer.");
        }
        if (note != null && note.trim().length() > 200) {
            throw new ValidationException("Note must be 200 characters or fewer.");
        }
        if (groupCode != null && !groupCode.trim().isEmpty() && !GuestGroup.isValidCode(groupCode)) {
            throw new ValidationException("Unknown guest group.");
        }
    }

    private int parsePartySize(String value) {
        if (value == null || value.trim().isEmpty()) return 1;
        int size;
        try {
            size = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Party size must be a whole number.");
        }
        if (size < 1 || size > MAX_PARTY_SIZE) {
            throw new ValidationException("Party size must be between 1 and " + MAX_PARTY_SIZE + ".");
        }
        return size;
    }

    private String parseRsvp(String value) {
        if (value == null || value.trim().isEmpty()) return Guest.RSVP_INVITED;
        switch (value.trim().toUpperCase()) {
            case Guest.RSVP_INVITED: return Guest.RSVP_INVITED;
            case Guest.RSVP_CONFIRMED: return Guest.RSVP_CONFIRMED;
            case Guest.RSVP_DECLINED: return Guest.RSVP_DECLINED;
            default: throw new ValidationException("RSVP status must be Invited, Confirmed or Declined.");
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
