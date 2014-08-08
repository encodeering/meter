package de.synyx.metrics.hook;

import com.codahale.metrics.Clock;
import de.synyx.metrics.annotation.Timer;
import org.glassfish.hk2.api.ServiceLocator;

import java.util.concurrent.TimeUnit;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public final class MetricHookTimer extends MetricHookSupport {

    private final com.codahale.metrics.Timer timer;
    private final Timer annotation;

    private long start;

    public MetricHookTimer (ServiceLocator locator, com.codahale.metrics.Timer timer, Timer annotation) {
        super (locator);

        this.timer = timer;
        this.annotation = annotation;
    }

    @Override
    public final void before () {
        start = tick ();
    }

    @Override
    public final void after (Object response, Throwable throwable) {
        long duration = tick () - start;

        TimeUnit unit;

                      unit = annotation.unit ();
        timer.update (unit.convert (duration, TimeUnit.NANOSECONDS), unit);
    }

    private long tick () {
        return Clock.defaultClock ().getTick ();
    }

}
