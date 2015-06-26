package de.synyx.meter.core.annotation;

import de.synyx.meter.core.service.DefaultClock;
import de.synyx.meter.core.util.Clock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * <p>A timer can for instance be used to measure the execution time of occurring events.</p>
 *
 * Date: 15.07.2014
 * Time: 16:02
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.LOCAL_VARIABLE)
public @interface Timer {

    /**
     * <p>Defines the {@link de.synyx.meter.core.annotation name} for {@code this} metric.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String value ()  default "#";

    /**
     * <p>Defines the precision.</p>
     *
     * @return a {@link java.util.concurrent.TimeUnit} object.
     */
    public abstract TimeUnit unit () default TimeUnit.NANOSECONDS;

    /**
     * <p>Defines the clock generator.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public abstract Class<? extends Clock> clock () default DefaultClock.class;

}
