package de.synyx.metrics.annotation;

import de.synyx.metrics.Metriculate;

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
public @interface Histogram {

    public abstract String value () default "#";

    public abstract long number () default 1;

    public abstract Kind kind () default Kind.Both;

    public abstract Class<? extends Metriculate> metriculate () default Metriculate.class;

}
