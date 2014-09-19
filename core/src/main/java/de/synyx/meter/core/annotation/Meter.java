package de.synyx.meter.core.annotation;

import de.synyx.meter.core.Metriculate;

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
public @interface Meter {

    public abstract String value () default "#";

    public abstract Kind kind () default Kind.Both;

    public abstract Class<? extends Metriculate> metriculate () default Metriculate.class;

    public abstract long number () default 1;

}
