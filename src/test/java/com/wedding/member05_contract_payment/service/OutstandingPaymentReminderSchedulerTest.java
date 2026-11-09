package com.wedding.member05_contract_payment.service;

import com.wedding.member04_booking.service.BookingNotificationService;
import com.wedding.member05_contract_payment.model.ContractStatus;
import com.wedding.member05_contract_payment.model.VendorContract;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class OutstandingPaymentReminderSchedulerTest {

    @Test
    void sendsOneReminderSevenDaysBeforeEndDate() {
        ContractService contracts = mock(ContractService.class);
        PaymentService payments = mock(PaymentService.class);
        BookingNotificationService notifications = mock(BookingNotificationService.class);
        VendorContract contract = activeContract(LocalDate.now().plusDays(7));

        when(contracts.getAllContracts()).thenReturn(List.of(contract));
        when(contracts.getById("CTR-1")).thenReturn(contract);
        when(payments.getOutstandingBalance(contract)).thenReturn(new BigDecimal("12500.00"));
        when(notifications.forRecipient("CUS-1")).thenReturn(List.of());

        new OutstandingPaymentReminderScheduler(contracts, payments, notifications)
                .sendOutstandingPaymentReminders();

        verify(notifications).send(eq("CUS-1"), eq("BKG-1"), contains("[PAYMENT-REMINDER:CTR-1:7D]"));
    }

    @Test
    void doesNotSendDuplicateReminderForSameWindow() {
        ContractService contracts = mock(ContractService.class);
        PaymentService payments = mock(PaymentService.class);
        BookingNotificationService notifications = mock(BookingNotificationService.class);
        VendorContract contract = activeContract(LocalDate.now().plusDays(7));

        when(contracts.getAllContracts()).thenReturn(List.of(contract));
        when(contracts.getById("CTR-1")).thenReturn(contract);
        when(payments.getOutstandingBalance(contract)).thenReturn(new BigDecimal("12500.00"));
        when(notifications.forRecipient("CUS-1")).thenReturn(List.of(notification(
                "[PAYMENT-REMINDER:CTR-1:7D] already sent")));

        new OutstandingPaymentReminderScheduler(contracts, payments, notifications)
                .sendOutstandingPaymentReminders();

        verify(notifications, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void skipsFullyPaidContracts() {
        ContractService contracts = mock(ContractService.class);
        PaymentService payments = mock(PaymentService.class);
        BookingNotificationService notifications = mock(BookingNotificationService.class);
        VendorContract contract = activeContract(LocalDate.now().plusDays(3));

        when(contracts.getAllContracts()).thenReturn(List.of(contract));
        when(contracts.getById("CTR-1")).thenReturn(contract);
        when(payments.getOutstandingBalance(contract)).thenReturn(BigDecimal.ZERO);

        new OutstandingPaymentReminderScheduler(contracts, payments, notifications)
                .sendOutstandingPaymentReminders();

        verify(notifications, never()).send(anyString(), anyString(), anyString());
    }

    private VendorContract activeContract(LocalDate endDate) {
        return new VendorContract("CTR-1", "BKG-1", "CUS-1", "VEN-1",
                LocalDate.now().toString(), LocalDate.now().toString(), endDate.toString(),
                new BigDecimal("50000.00"), "Test terms", ContractStatus.ACTIVE,
                LocalDate.now().toString(), BigDecimal.ZERO, "UNPAID");
    }

    private com.wedding.member04_booking.model.BookingNotification notification(String message) {
        com.wedding.member04_booking.model.BookingNotification n =
                new com.wedding.member04_booking.model.BookingNotification();
        n.setMessage(message);
        return n;
    }
}
