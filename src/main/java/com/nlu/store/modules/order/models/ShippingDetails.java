package com.nlu.store.modules.order.models;

import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class ShippingDetails {
    private ULID id;

    // --- Recipient Info ---
    private String contactName;
    private String contactPhone;
    private String contactEmail;

    // --- Address Details ---
    private String province;
    private String district;
    private String ward;
    private String addressDetail; // e.g., "123 Main St"

    // Pre-formatted full address for display/printing labels
    private String fullAddress;

    // --- Geo Identifiers (For Shipping APIs like GHN/GHTK) ---
    private String provinceCode;
    private String districtCode;
    private String wardCode;

    // --- Logistics & Tracking ---
    private String carrierName; // e.g., "GHTK", "ViettelPost"
    private String trackingCode; // The waybill code
    private LocalDate estimatedDeliveryDate;
    private String shippingNote; // Note for the shipper (e.g., "Call before delivery")

    // Getters & Setters...
}
