package de.synyx.meter.example.service;

import de.synyx.meter.core.annotation.Metric;
import de.synyx.meter.core.annotation.Timer;

/**
 * Date: 30.07.2014
 * Time: 08:22
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public class BambooApplicationService implements BambooService<String> {

    /** {@inheritDoc} */
    @Metric (timers = @Timer ("{name}-{locale}"))
    @Override
    public String call (String application) {
        return "[" + application + "]";
    }

}
