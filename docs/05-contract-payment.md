# Member 05 — Contract & Payment Management

**Responsibility:** vendor contracts, payment records, financial totals and balance tracking.

## Main classes
- `VendorContract`: contract terms, dates, amount and payment summary.
- `ContractStatus`: contract lifecycle state.
- `Payment`: abstract payment base class.
- `AdvancePayment`, `PartialPayment`, `FinalPayment`: specialised payment types.
- `ContractService`, `PaymentService`: contract and payment business rules.
- `ContractRepository`, `PaymentRepository`: file-backed persistence.
- `ContractPaymentController`: contract/payment endpoints and print view.
- `OutstandingPaymentReminderScheduler`: scheduled in-app reminders.

## Main relationships and OOP
- Each payment belongs to one contract; a contract may have zero or many payments.
- Payment subtypes inherit shared payment attributes and specialise `getPaymentType()`.
- The service layer refreshes contract totals and outstanding balances.

## Class diagram
![Member 05 — Contract & Payment UML](diagrams/05-contract-payment-class-diagram.png)
