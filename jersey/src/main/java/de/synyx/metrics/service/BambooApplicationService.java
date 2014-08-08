package de.synyx.metrics.service;

import de.synyx.metrics.annotation.Metric;
import de.synyx.metrics.annotation.Timer;

/**
 * Date: 30.07.2014
 * Time: 08:22
 */
public class BambooApplicationService implements BambooService<String> {

    @Metric (timers = @Timer ("#{foo}-{larry}"))
    @Override
    public String call (String application) {
        return "[" + application + "]";
    }

}
