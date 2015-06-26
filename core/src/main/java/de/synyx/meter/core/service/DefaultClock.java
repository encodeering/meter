package de.synyx.meter.core.service;

import de.synyx.meter.core.util.Clock;

/**
 * Date: 05.09.2014
 * Time: 12:55
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public class DefaultClock implements Clock {

    /** {@inheritDoc} */
    @Override
    public long tick () {
        return System.nanoTime ();
    }

}
