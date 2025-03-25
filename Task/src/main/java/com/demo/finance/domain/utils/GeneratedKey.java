package com.demo.finance.domain.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code GeneratedKey} annotation is used to mark fields in an entity that represent
 * a generated key (e.g., an auto-generated primary key in a database). This annotation
 * is typically used in conjunction with persistence logic to identify fields that should
 * be populated with generated values after an entity is persisted.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GeneratedKey {
}