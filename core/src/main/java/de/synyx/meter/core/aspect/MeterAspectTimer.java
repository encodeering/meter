package de.synyx.meter.core.aspect;

import com.google.common.base.Supplier;
import de.synyx.meter.core.Meter;
import de.synyx.meter.core.annotation.Timer;
import de.synyx.meter.core.util.Clock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 * <p>Aspect to measure the execution time of occurring events.</p>
 *
 * Date: 16.07.2014
 * Time: 11:03
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class MeterAspectTimer extends MeterAspectSupport {

    private final static ThreadLocal<ConcurrentMap<Timer, Long>> Durations = new ThreadLocal<ConcurrentMap<Timer, Long>> () {

        @Override
        protected final ConcurrentMap<Timer, Long> initialValue () {
            return new ConcurrentHashMap<> ();
        }

    };

    private final Timer annotation;

    private final Supplier<Meter<Duration>> meter;

    private final Clock clock;

    /**
     * <p>Constructor.</p>
     * <p>
     *    A supplier will be used to obtain the associated meter instance, which will be resolved dynamically on each invocation.
     * </p>
     *
     * @param annotation specifies a {@link de.synyx.meter.core.annotation.Meter} configuration.
     * @param meter specifies the dynamically resolved meter {@link de.synyx.meter.core.Meter} instance.
     * @param clock specifies the time emitter.
     */
    public MeterAspectTimer (Timer annotation, Supplier<Meter<Duration>> meter, Clock clock) {
        this.meter      = meter;
        this.annotation = annotation;
        this.clock      = clock;
    }

    /** {@inheritDoc} */
    @Override
    public final void before () {
        Durations.get ().put (annotation, tick ());
    }

    /** {@inheritDoc} */
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
        return clock.tick ();
    }

}
