package com.wedding.member05_contract_payment.service;

import com.wedding.member04_booking.service.BookingNotificationService;
import com.wedding.member05_contract_payment.model.ContractStatus;
import com.wedding.member05_contract_payment.model.VendorContract;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Sends persistent in-app reminders when an active contract still has an
 * outstanding balance approaching its recorded end date.
 *
 * The contract model has no separate payment due-date field, so reminders
 * deliberately refer to the contract end date rather than inventing one.
 */
@Component
public class OutstandingPaymentReminderScheduler {

    private final ContractService contractService;
    private final PaymentService paymentService;
    private final BookingNotificationService notificationService;

    public OutstandingPaymentReminderScheduler(ContractService contractService,
                                                PaymentService paymentService,
                                                BookingNotificationService notificationService) {
        this.contractService = contractService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${eclat.payment-reminders.cron:0 0 9 * * *}")
    public void sendOutstandingPaymentReminders() {
        LocalDate today = LocalDate.now();
        for (VendorContract contract : contractService.getAllContracts()) {
            if (contract.getStatus() != ContractStatus.ACTIVE) continue;

            try {
                LocalDate endDate = LocalDate.parse(contract.getEndDate());
                long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, endDate);
                String reminderWindow = reminderWindow(daysRemaining);
                if (reminderWindow == null) continue;

                contractService.refreshTotals(contract.getContractId());
                VendorContract refreshed = contractService.getById(contract.getContractId());
                BigDecimal outstanding = paymentService.getOutstandingBalance(refreshed);
                if (outstanding == null || outstanding.compareTo(BigDecimal.ZERO) <= 0) continue;

                String marker = "[PAYMENT-REMINDER:" + refreshed.getContractId() + ":" + reminderWindow + "]";
                boolean alreadySent = notificationService.forRecipient(refreshed.getCustomerId()).stream()
                        .anyMatch(n -> n.getMessage() != null && n.getMessage().contains(marker));
                if (alreadySent) continue;

                String timing = daysRemaining > 0
                        ? "in " + daysRemaining + " day(s)"
                        : daysRemaining == 0 ? "today" : Math.abs(daysRemaining) + " day(s) ago";
                String message = "Payment reminder: LKR " + outstanding.toPlainString()
                        + " remains outstanding for contract " + refreshed.getContractId()
                        + ". The contract end date is " + timing + ". Please contact the administrator "
                        + "to arrange the remaining payment. " + marker;
                notificationService.send(refreshed.getCustomerId(), refreshed.getBookingId(), message);
            } catch (DateTimeParseException ex) {
                // Ignore legacy or malformed contract dates; never stop reminders for other contracts.
            } catch (RuntimeException ex) {
                // One damaged record must not prevent reminders for other customers.
                System.err.println("[PaymentReminder] Contract " + contract.getContractId()
                        + " could not be processed: " + ex.getMessage());
            }
        }
    }

    private String reminderWindow(long daysRemaining) {
        if (daysRemaining == 7) return "7D";
        if (daysRemaining == 3) return "3D";
        if (daysRemaining == 1) return "1D";
        if (daysRemaining == 0) return "DUE";
        if (daysRemaining < 0 && Math.abs(daysRemaining) % 7 == 0) {
            return "OVERDUE-" + Math.abs(daysRemaining);
        }
        return null;
    }
}
