package com.wedding.member03_vendor.model;

/**
 * Enum representing the category of a vendor service.
 * Demonstrates encapsulation by holding a displayName per constant.
 *
 * @author Member 03 – Vendor Management
 */
public enum VendorCategory {

 PHOTOGRAPHY("Photography"),
 VIDEOGRAPHY("Videography"),
 CATERING("Catering"),
 DECORATION("Decoration"),
 MUSIC_DJ("Music / DJ"),
 WEDDING_DRESSES("Wedding Dresses"),
 WEDDING_CAKES("Wedding Cakes"),
 BEAUTY_SALON("Beauty Salon"),
 WEDDING_PLANNERS("Wedding Planners"),
 VENUES("Venues"),
 MAKEUP_ARTISTS("Makeup Artists"),
 BRIDAL_SALONS("Bridal Salons"),
 HAIR_STYLISTS("Hair Stylists"),
 PHOTO_BOOTH("Photo Booth"),
 CONTENT_CREATOR("Content Creator"),
 WEDDING_OFFICIANTS("Wedding Officiants"),
 FLORISTS("Florists"),
 OTHER("Other");

 // ── Fields ────────────────────────────────────────────────────────────────
 private final String displayName;

 // ── Constructor ───────────────────────────────────────────────────────────
 VendorCategory(String displayName) {
 this.displayName = displayName;
 }

 // ── Getter ────────────────────────────────────────────────────────────────
 public String getDisplayName() {
 return displayName;
 }

 /**
 * Looks up a VendorCategory by its enum name (case-insensitive).
 * Falls back to OTHER if the string is not recognised.
 *
 * @param s the raw string stored in the CSV
 * @return matching VendorCategory, or OTHER
 */
 public static VendorCategory fromString(String s) {
 if (s == null || s.trim().isEmpty()) return OTHER;
 for (VendorCategory vc : values()) {
 if (vc.name().equalsIgnoreCase(s.trim())) {
 return vc;
 }
 }
 return OTHER;
 }

 @Override
 public String toString() {
 return displayName;
 }
}
