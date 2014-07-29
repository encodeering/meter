package de.synyx.metrics.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 15.07.2014
 * Time: 16:02
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.LOCAL_VARIABLE)
public @interface Timer {

    String value () default "#";

}
