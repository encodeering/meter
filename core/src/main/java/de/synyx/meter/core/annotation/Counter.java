package de.synyx.meter.core.annotation;

import de.synyx.meter.core.Measure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>A counter can for instance be used to measure the number of occurring events.</p>
 *
 * Date: 15.07.2014
 * Time: 16:11
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.LOCAL_VARIABLE)
public @interface Counter {

    /**
     * <p>Defines the {@link de.synyx.meter.core.annotation name} for {@code this} metric.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String value () default "#";

    /**
     * <p>Defines which {@link de.synyx.meter.core.annotation.Kind kind} of event should be taken into account.</p>
     *
     * @return a {@link de.synyx.meter.core.annotation.Kind} object.
     */
    public abstract Kind kind () default Kind.Both;

    /**
     * <p>Replaces the default measure mechanism with a customized procedure.</p>
     * <p>
     *     The default mechanism uses the {@link #number()} to update the counter in the direction of the {@link #operation()}.
     * </p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public abstract Class<? extends Measure> measure () default Measure.class;

    /**
     * <p>Defines the math. operation.</p>
     *
     * @return a {@link de.synyx.meter.core.annotation.Counter.Operation} object.
     */
    public abstract Operation operation () default Operation.Increment;

    /**
     * <p>Defines the value for the increment or decrement operation.</p>
     *
     * @return a long.
     */
    public abstract long number () default 1;

    public enum Operation {

        Increment, Decrement

    }

}
