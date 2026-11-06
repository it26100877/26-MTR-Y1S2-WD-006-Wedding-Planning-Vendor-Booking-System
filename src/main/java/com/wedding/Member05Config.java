package com.wedding;

import com.wedding.member02_wedding_plan.repository.WeddingPlanRepository;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member03_vendor.repository.VendorRepository;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.repository.BookingRepository;
import com.wedding.member04_booking.service.BookingService;
import com.wedding.member05_contract_payment.repository.ContractRepository;
import com.wedding.member05_contract_payment.repository.PaymentRepository;
import com.wedding.member05_contract_payment.service.ContractService;
import com.wedding.member05_contract_payment.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Member05Config {
    @Bean
    public WeddingPlanRepository weddingPlanRepository() {
        return new WeddingPlanRepository();
    }

    @Bean
    public WeddingPlanService weddingPlanService(WeddingPlanRepository repository) {
        return new WeddingPlanService(repository);
    }

    @Bean
    public VendorRepository vendorRepository() {
        return new VendorRepository();
    }

    @Bean
    public VendorService vendorService(VendorRepository repository) {
        return new VendorService(repository);
    }

    @Bean
    public BookingRepository bookingRepository() {
        return new BookingRepository();
    }

    @Bean
    public BookingService bookingService(BookingRepository repository, VendorService vendorService) {
        return new BookingService(repository, vendorService);
    }

    @Bean
    public ContractRepository contractRepository() {
        return new ContractRepository();
    }

    @Bean
    public PaymentRepository paymentRepository() {
        return new PaymentRepository();
    }

    @Bean
    public PaymentService paymentService(PaymentRepository repository) {
        return new PaymentService(repository);
    }

    @Bean
    public ContractService contractService(ContractRepository repository,
                                           BookingRepository bookingRepository,
                                           PaymentService paymentService) {
        return new ContractService(repository, bookingRepository, paymentService);
    }
}
