package de.synyx.meter.core.util;

/**
 * <p>A clock can be used as a time emitter, whereas the delta between two ticks will be interpreted as the emitted time value.</p>
 *
 * Date: 05.09.2014
 * Time: 12:53
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface Clock {

    /**
     * <p>Determines a tick with an arbitrary precision. The precision has an impact on the measurement granularity.</p>
     *
     * @return a long.
     */
    public abstract long tick ();

}
