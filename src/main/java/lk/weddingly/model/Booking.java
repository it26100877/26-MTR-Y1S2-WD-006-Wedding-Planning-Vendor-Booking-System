package lk.weddingly.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One customer's reservation with a wedding vendor. */
public class Booking extends BookingRecord {
    private String customerId;
    private String customerName;
    private String vendorId;
    private String vendorName;
    private String serviceName;
    private String status;
    private BigDecimal amount;

    public Booking(String id, String customerId, String customerName, String vendorId, String vendorName,
                   String serviceName, LocalDate eventDate, String status, BigDecimal amount, String notes) {
        super(id, eventDate, notes);
        this.customerId = customerId;
        this.customerName = customerName;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.serviceName = serviceName;
        this.status = status;
        this.amount = amount;
    }

    @Override public String getRecordType() { return "Vendor booking"; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
