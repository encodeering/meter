package de.synyx.metrics.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Date: 15.07.2014
 * Time: 16:02
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.LOCAL_VARIABLE)
public @interface Timer {

    public abstract String value ()  default "#";

    public abstract TimeUnit unit () default TimeUnit.NANOSECONDS;

}