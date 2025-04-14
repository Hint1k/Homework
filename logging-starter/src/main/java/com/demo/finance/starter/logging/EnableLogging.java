package com.demo.finance.starter.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EnableLogging is a custom annotation used to enable logging functionality in the application.
 * When applied to the main class of the main application module, it activates logging-related features
 * such as request/response logging, error logging, or other logging mechanisms defined by the framework.
 * This annotation ensures that all logging configurations are consistently applied across the application.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableLogging {
}