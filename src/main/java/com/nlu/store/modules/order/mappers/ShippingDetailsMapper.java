package com.nlu.store.modules.order.mappers;

import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.modules.order.models.ShippingDetails;

import java.sql.SQLException;

public class ShippingDetailsMapper implements RowMapper<ShippingDetails> {

    private final String prefix;

    /**
     * Constructor with prefix support.
     * <p>
     * Example: If your SQL is "SELECT contact_name AS os_contact_name ...",
     * pass "os_" as the prefix.
     * </p>
     *
     * @param prefix The column prefix (e.g., "os_").
     */
    public ShippingDetailsMapper(String prefix) {
        this.prefix = (prefix == null) ? "" : prefix;
    }

    /**
     * Default constructor (no prefix).
     */
    public ShippingDetailsMapper() {
        this("");
    }


    @Override
    public ShippingDetails mapRow(ResultSetReader reader, int rowNum) throws SQLException {
        // 1. Check if the primary key exists.
        // If "os_id" is null, it means there is no shipping record for this row (LEFT JOIN result).
        // Returning null here prevents creating an empty object with all null fields.
        if (reader.getOptionalString(column("id")).isEmpty()) {
            return null;
        }

        return ShippingDetails.builder()
                .id(reader.getULID(column("id")))

                // --- Recipient Info ---
                .contactName(reader.getString(column("contact_name")))
                .contactPhone(reader.getString(column("contact_phone")))
                .contactEmail(reader.getString(column("contact_email")))

                // --- Address Details ---
                .province(reader.getString(column("province")))
                .district(reader.getString(column("district")))
                .ward(reader.getString(column("ward")))
                .addressDetail(reader.getString(column("address_detail")))
                .fullAddress(reader.getString(column("full_address")))

                // --- Geo Identifiers ---
                .provinceCode(reader.getString(column("province_code")))
                .districtCode(reader.getString(column("district_code")))
                .wardCode(reader.getString(column("ward_code")))

                // --- Logistics & Tracking ---
                .carrierName(reader.getString(column("carrier_name")))
                .trackingCode(reader.getString(column("tracking_code")))
                .estimatedDeliveryDate(reader.getLocalDate(column("estimated_delivery_date")))
                .shippingNote(reader.getString(column("shipping_note")))

                .build();
    }

    @Override
    public String prefix() {
        return this.prefix;
    }
}
