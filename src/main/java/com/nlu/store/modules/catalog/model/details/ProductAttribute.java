package com.nlu.store.modules.catalog.model.details;

import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductAttribute {
    private ULID id;            // ID định danh (VD: attr_color)
    private String code;        // Mã code để dev dùng (VD: "color", "size")
    private String label;        // Tên hiển thị (VD: "Màu sắc")
    private int position;       // Thứ tự hiển thị nhóm
    private String visualType; // Loại hiển thị: 'text', 'color', 'image', 'select'

    private List<AttributeOption> options = new ArrayList<>();

    @Builder
    public ProductAttribute(ULID id, String code, String label, int position, String visualType, List<AttributeOption> options) {
        this.id = id;
        this.code = code;
        this.label = label;
        this.position = position;
        this.visualType = visualType;
        this.options = options;
    }

    public ProductAttribute() {
    }

    // Helper: Lấy ID dạng String cho JSP
    public String getIdAsString() {
        return id != null ? id.toString() : "";
    }
}