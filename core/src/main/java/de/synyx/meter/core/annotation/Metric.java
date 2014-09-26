package de.synyx.meter.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>A metric serves as a single-point-of-definition container for all of the other metrics.</p>
 *
 * Date: 15.07.2014
 * Time: 15:46
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface Metric {

    /**
     * <p>Defines a container for counter metrics.</p>
     * <p>
     *     An empty array prevents the registration of a counter metric at the given code position.
     * </p>
     *
     * @return an array of {@link de.synyx.meter.core.annotation.Counter} objects.
     */
    public abstract Counter[] counters () default {};

    /**
     * <p>Defines a container for histogram metrics.</p>
     * <p>
     *     An empty array prevents the registration of a histogram metric at the given code position.
     * </p>
     *
     * @return an array of {@link de.synyx.meter.core.annotation.Histogram} objects.
     */
    public abstract Histogram[] histograms () default {};

    /**
     * <p>Defines a container for meter metrics.</p>
     * <p>
     *     An empty array prevents the registration of a meter metric at the given code position.
     * </p>
     *
     * @return an array of {@link de.synyx.meter.core.annotation.Meter} objects.
     */
    public abstract Meter[] meters () default {};

    /**
     * <p>Defines a container for timer metrics.</p>
     * <p>
     *     An empty array prevents the registration of a timer metric at the given code position.
     * </p>
     *
     * @return an array of {@link de.synyx.meter.core.annotation.Timer} objects.
     */
    public abstract Timer[] timers () default {};

}
