package com.wedding.member04_booking.service;

import com.wedding.member04_booking.model.Booking;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Single-page Éclat-branded booking receipt.
 *
 * The PDF uses the same ivory, champagne-gold and ink palette as the web
 * document templates. Standard PDF fonts keep the generator dependency-free.
 */
public final class BookingPdfReceipt {
    private static final String INK = "0.16 0.15 0.13";
    private static final String MUTED = "0.45 0.42 0.37";
    private static final String GOLD = "0.75 0.64 0.47";
    private static final String IVORY = "0.98 0.97 0.95";
    private static final String WHITE = "1 1 1";

    private BookingPdfReceipt() {}

    private static String pdfText(String value) {
        if (value == null) return "";
        StringBuilder s = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (c == '\\' || c == '(' || c == ')') s.append('\\');
            s.append(c >= 32 && c <= 126 ? c : (c == '\n' || c == '\r' ? ' ' : '?'));
        }
        return s.toString();
    }

    private static String line(String text, int x, int y, int size, String font) {
        return "BT /" + font + " " + size + " Tf 1 0 0 1 " + x + " " + y
                + " Tm (" + pdfText(text) + ") Tj ET\n";
    }

    private static List<String> wrap(String value, int max) {
        String clean = (value == null ? "" : value).replaceAll("[\\r\\n|]+", " ").trim();
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : clean.split("\\s+")) {
            while (word.length() > max) {
                if (current.length() > 0) { lines.add(current.toString()); current.setLength(0); }
                lines.add(word.substring(0, max));
                word = word.substring(max);
            }
            if (current.length() > 0 && current.length() + word.length() + 1 > max) {
                lines.add(current.toString());
                current.setLength(0);
            }
            if (!word.isEmpty()) {
                if (current.length() > 0) current.append(' ');
                current.append(word);
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        return lines;
    }

    private static void section(StringBuilder content, String title, int y) {
        content.append(GOLD).append(" rg\n");
        content.append("45 ").append(y - 5).append(" 4 15 re f\n");
        content.append(INK).append(" rg\n");
        content.append(line(title, 58, y, 10, "F2"));
    }

    public static byte[] generate(Booking b, String customerName, String vendorName, String planName) {
        StringBuilder content = new StringBuilder();

        // Éclat document masthead: ivory field, champagne divider and clear hierarchy.
        content.append(IVORY).append(" rg 0 720 595 122 re f\n");
        content.append(GOLD).append(" rg 0 716 595 4 re f\n");
        content.append(INK).append(" rg\n");
        content.append(line("ECLAT", 46, 793, 25, "F3"));
        content.append(MUTED).append(" rg\n");
        content.append(line("WEDDING PLANNING & VENDOR BOOKING", 47, 773, 8, "F2"));
        content.append(INK).append(" rg\n");
        content.append(line("BOOKING RECEIPT", 375, 797, 12, "F2"));
        content.append(MUTED).append(" rg\n");
        content.append(line("OFFICIAL CUSTOMER COPY", 375, 780, 7, "F1"));
        content.append(WHITE).append(" rg 45 735 505 25 re f\n");
        content.append(INK).append(" rg\n");
        content.append(line("REFERENCE  " + b.getBookingId(), 58, 744, 9, "F2"));

        section(content, "BOOKING DETAILS", 690);
        int y = 663;
        String[][] details = {
            {"STATUS", b.getStatus().getDisplayName()},
            {"CUSTOMER", customerName},
            {"VENDOR", vendorName},
            {"WEDDING PLAN", planName},
            {"EVENT DATE", b.getBookingDate()},
            {"REQUESTED AT", b.getRequestedAt()},
            {"CONFIRMED AT", b.getConfirmedAt() == null ? "-" : b.getConfirmedAt()},
            {"AGREED SERVICE FEE", "LKR " + b.getAgreedAmount().toPlainString()}
        };
        for (int i = 0; i < details.length; i++) {
            if (i % 2 == 0) content.append(IVORY).append(" rg 45 ").append(y - 9).append(" 505 29 re f\n");
            content.append(MUTED).append(" rg\n");
            content.append(line(details[i][0], 58, y, 8, "F2"));
            content.append(INK).append(" rg\n");
            content.append(line(details[i][1], 220, y, 9, "F1"));
            y -= 31;
        }

        y -= 3;
        section(content, "SERVICE DETAILS", y);
        y -= 25;
        content.append(INK).append(" rg\n");
        for (String s : wrap(b.getServiceDetails(), 92)) {
            if (y < 105) break;
            content.append(line(s, 58, y, 9, "F1"));
            y -= 14;
        }

        if (b.getNotes() != null && !b.getNotes().isBlank() && y > 145) {
            y -= 7;
            section(content, "CUSTOMER NOTES", y);
            y -= 23;
            content.append(INK).append(" rg\n");
            for (String s : wrap(b.getNotes(), 92)) {
                if (y < 105) break;
                content.append(line(s, 58, y, 9, "F1"));
                y -= 14;
            }
        }

        content.append(GOLD).append(" RG 45 72 m 550 72 l S\n");
        content.append(MUTED).append(" rg\n");
        content.append(line("Generated by ECLAT Booking Management", 47, 54, 8, "F2"));
        content.append(line("Keep this receipt for your records.", 385, 54, 8, "F1"));

        byte[] stream = content.toString().getBytes(StandardCharsets.US_ASCII);
        String[] objects = {
            "<< /Type /Catalog /Pages 2 0 R >>",
            "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
            "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R /F2 5 0 R /F3 7 0 R >> >> /Contents 6 0 R >>",
            "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
            "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>",
            "<< /Length " + stream.length + " >>\nstream\n" + content + "endstream",
            "<< /Type /Font /Subtype /Type1 /BaseFont /Times-Bold >>"
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write("%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII));
            List<Integer> offsets = new ArrayList<>();
            offsets.add(0);
            for (int i = 0; i < objects.length; i++) {
                offsets.add(out.size());
                out.write((i + 1 + " 0 obj\n" + objects[i] + "\nendobj\n").getBytes(StandardCharsets.US_ASCII));
            }
            int xref = out.size();
            out.write(("xref\n0 " + (objects.length + 1) + "\n0000000000 65535 f \n").getBytes(StandardCharsets.US_ASCII));
            for (int i = 1; i < offsets.size(); i++) {
                out.write(String.format("%010d 00000 n \n", offsets.get(i)).getBytes(StandardCharsets.US_ASCII));
            }
            out.write(("trailer\n<< /Size " + (objects.length + 1) + " /Root 1 0 R >>\nstartxref\n"
                    + xref + "\n%%EOF\n").getBytes(StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return out.toByteArray();
    }
}
