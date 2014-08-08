package de.synyx.metrics.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 15.07.2014
 * Time: 15:46
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface Metric {

    public abstract Counter[] counters () default {};

    public abstract Histogram[] histograms () default {};

    public abstract Meter[] meters () default {};

    public abstract Timer[] timers () default {};

}
