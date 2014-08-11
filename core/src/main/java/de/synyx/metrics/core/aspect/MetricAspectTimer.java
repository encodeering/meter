package de.synyx.metrics.core.aspect;

import com.codahale.metrics.Clock;
import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.annotation.Timer;

import java.util.concurrent.TimeUnit;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public final class MetricAspectTimer extends MetricAspectSupport {

    private final com.codahale.metrics.Timer timer;
    private final Timer annotation;

    private long start;

    public MetricAspectTimer (Injector injector, com.codahale.metrics.Timer timer, Timer annotation) {
        super (injector);

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
