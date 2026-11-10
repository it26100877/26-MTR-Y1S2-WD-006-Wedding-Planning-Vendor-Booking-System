package com.wedding.member04_booking;

import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;
import com.wedding.member04_booking.service.BookingPdfReceipt;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class BookingPdfReceiptTest {
    @Test
    void createsValidPdfWithEclatDocumentBrandingAndBookingDetails() {
        Booking booking = new Booking();
        booking.setBookingId("BKG-TEST-001");
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingDate("2028-04-14");
        booking.setAgreedAmount(new BigDecimal("40000.00"));
        booking.setRequestedAt("2026-11-08 10:00");
        booking.setConfirmedAt("2026-11-08 10:01");
        booking.setServiceDetails("Wedding planning and decor consultation");

        byte[] pdf = BookingPdfReceipt.generate(booking, "Test Customer", "Test Vendor", "Our Plan");
        String pdfSource = new String(pdf, StandardCharsets.US_ASCII);

        assertTrue(pdfSource.startsWith("%PDF-1.4"));
        assertTrue(pdfSource.contains("ECLAT"));
        assertTrue(pdfSource.contains("BOOKING RECEIPT"));
        assertTrue(pdfSource.contains("BOOKING DETAILS"));
        assertTrue(pdfSource.contains("BKG-TEST-001"));
        assertTrue(pdfSource.contains("40000.00"));
        assertTrue(pdfSource.contains("xref"));
        assertTrue(pdf.length > 700);
    }
}
