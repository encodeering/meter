package de.synyx.metrics.core.aspect;

import com.codahale.metrics.Clock;
import com.google.common.base.Supplier;
import de.synyx.metrics.core.Meter;
import de.synyx.metrics.core.annotation.Timer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public final class MetricAspectTimer extends MetricAspectSupport {

    private final static ThreadLocal<ConcurrentMap<Timer, Long>> Durations = new ThreadLocal<ConcurrentMap<Timer, Long>> () {

        @Override
        protected final ConcurrentMap<Timer, Long> initialValue () {
            return new ConcurrentHashMap<> ();
        }

    };

    private final Timer annotation;

    private final Supplier<Meter<Duration>> meter;

    private final Clock clock;

    public MetricAspectTimer (Timer annotation, Supplier<Meter<Duration>> meter, Clock clock) {
        this.meter      = meter;
        this.annotation = annotation;
        this.clock      = clock;
    }

    @Override
    public final void before () {
        Durations.get ().put (annotation, tick ());
    }

    @Override
    public final void after (Object response, Throwable throwable) {
        long duration = tick () - Durations.get ().get (annotation);

        try {
            TimeUnit unit;

                                                  unit = annotation.unit ();
            meter.get ().update (Measure.valueOf (unit.convert (duration, TimeUnit.NANOSECONDS), convert (unit)));
        } finally{
            Durations.get ().remove (annotation);
        }
    }

    final Unit<Duration> convert (TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:  return SI.NANO  (SI.SECOND);
            case MICROSECONDS: return SI.MICRO (SI.SECOND);
            case MILLISECONDS: return SI.MILLI (SI.SECOND);
            case SECONDS:      return           SI.SECOND;
            case MINUTES:      return NonSI.MINUTE;
            case HOURS:        return NonSI.HOUR;
            case DAYS:         return NonSI.DAY;
        }

        throw new UnsupportedOperationException ("unit " + unit + " currently not supported");
    }

    private long tick () {
        return clock.getTick ();
    }

}
