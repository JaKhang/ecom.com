package com.nlu.store.core.web;


import java.io.Serializable;

/**
 * Represents a UI notification message containing a type (style) and a content key.
 * Used to transfer alert data from Controller to View.
 */
public class Alert implements Serializable {

    private final AlertType type;
    private final String messageKey;

    /**
     * Constructor.
     *
     * @param type       The type of the alert (defines the CSS style).
     * @param messageKey The i18n key for the message text.
     */
    public Alert(AlertType type, String messageKey) {
        this.type = type;
        this.messageKey = messageKey;
    }

    // ==================================================================
    // STATIC FACTORY METHODS (For cleaner code)
    // ==================================================================

    public static Alert success(String messageKey) {
        return new Alert(AlertType.SUCCESS, messageKey);
    }

    public static Alert error(String messageKey) {
        return new Alert(AlertType.ERROR, messageKey);
    }

    public static Alert warning(String messageKey) {
        return new Alert(AlertType.WARNING, messageKey);
    }

    public static Alert info(String messageKey) {
        return new Alert(AlertType.INFO, messageKey);
    }

    // ==================================================================
    // GETTERS
    // ==================================================================

    public AlertType getType() {
        return type;
    }

    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Helper to get the CSS class directly in JSP/View.
     * Usage: ${alert.cssClass}
     */
    public String getCssClass() {
        return type.getCssClass();
    }
}
