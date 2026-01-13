package com.nlu.store.core.jawire;

import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;




/**
 * Marks a field as "Public State".
 * <p>
 * Only fields with this annotation will be:
 * 1. Serialized to the client (in the JSON snapshot).
 * 2. Allowed to be updated by the client (via jw-model).
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Model {
}

