package de.synyx.metrics.core.util;

import de.synyx.metrics.core.internal.DefaultClock;

/**
 * Date: 05.09.2014
 * Time: 12:53
 */
public interface Clock {

    public final static Class<? extends Clock> Default = DefaultClock.class;

    public abstract long tick ();

}
