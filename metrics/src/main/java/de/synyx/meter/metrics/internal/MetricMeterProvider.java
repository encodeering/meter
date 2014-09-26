package de.synyx.meter.metrics.internal;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import de.synyx.meter.core.Meter;
import de.synyx.meter.core.MeterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.measure.Measurable;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 * Date: 12.08.2014
 * Time: 14:47
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class MetricMeterProvider implements MeterProvider {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    private final MetricRegistry registry;

    /**
     * <p>Constructor for MetricMeterProvider.</p>
     *
     * @param registry a {@link com.codahale.metrics.MetricRegistry} object.
     */
    @Inject
    public MetricMeterProvider (MetricRegistry registry) {
        this.registry = registry;
    }

    /** {@inheritDoc} */
    @Override
    public final Meter<Dimensionless> counter (final String name) {
        logger.trace ("requesting counter {}", name);

        return new Meter<Dimensionless> () {

            private final Counter counter = registry.counter (name);

            @Override
            public final Meter<Dimensionless> update (Measurable<Dimensionless> point) {
                       this.counter.inc (point.longValue (Unit.ONE));
                return this;
            }

        };
    }

    /** {@inheritDoc} */
    @Override
    public final Meter<Dimensionless> histogram (final String name) {
        logger.trace ("requesting histogram {}", name);

        return new Meter<Dimensionless> () {

            private final Histogram histogram = registry.histogram (name);

            @Override
            public final Meter<Dimensionless> update (Measurable<Dimensionless> point) {
                       this.histogram.update (point.longValue (Unit.ONE));
                return this;
            }

        };
    }

    /** {@inheritDoc} */
    @Override
    public final Meter<Dimensionless> meter (final String name) {
        logger.trace ("requesting meter {}", name);

        return new Meter<Dimensionless> () {

            private final com.codahale.metrics.Meter meter = registry.meter (name);

            @Override
            public final Meter<Dimensionless> update (Measurable<Dimensionless> point) {
                       this.meter.mark (point.longValue (Unit.ONE));
                return this;
            }

        };
    }

    /** {@inheritDoc} */
    @Override
    public final Meter<Duration> timer (final String name) {
        logger.trace ("requesting timer {}", name);

        return new Meter<Duration> () {

            private final Timer timer = registry.timer (name);

            @Override
            public final Meter<Duration> update (Measurable<Duration> point) {
                       this.timer.update (point.longValue (SI.NANO (SI.SECOND)), TimeUnit.NANOSECONDS);
                return this;
            }

        };
    }

    /** {@inheritDoc} */
    @Override
    public final <T> T unwrap (Class<? extends T> type) {
        return type.isInstance (registry) ? type.cast (registry) : null;
    }
}
