package de.synyx.metrics.core.internal;

import de.synyx.metrics.core.util.Clock;

/**
 * Date: 05.09.2014
 * Time: 12:55
 */
public class DefaultClock implements Clock {

    @Override
    public long tick () {
        return System.nanoTime ();
    }

}
