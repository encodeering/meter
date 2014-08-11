package de.synyx.metrics.example.service;

import de.synyx.metrics.core.annotation.Metric;
import de.synyx.metrics.core.annotation.Timer;

/**
 * Date: 30.07.2014
 * Time: 08:22
 */
public class BambooApplicationService implements BambooService<String> {

    @Metric (timers = @Timer ("{name}-{locale}"))
    @Override
    public String call (String application) {
        return "[" + application + "]";
    }

}
