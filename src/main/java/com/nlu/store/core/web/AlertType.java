package com.nlu.store.core.web;

public enum AlertType {
    SUCCESS("success"), // Màu xanh lá
    ERROR("danger"),    // Màu đỏ
    WARNING("warning"), // Màu vàng
    INFO("info");       // Màu xanh dương

    private final String cssClass;

    AlertType(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }
}