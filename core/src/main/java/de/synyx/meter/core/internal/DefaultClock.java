package de.synyx.meter.core.internal;

import de.synyx.meter.core.util.Clock;

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
