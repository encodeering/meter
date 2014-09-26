package de.synyx.meter.core;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;

import static com.google.common.base.Suppliers.compose;

/**
 * <p>A set of utilities to compose units in a functional way</p>
 *
 * Date: 12.08.2014
 * Time: 10:37
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class MeterProviders {

    private MeterProviders () {}

    /**
     * <p>Creates a supplier to obtain a named meter.</p>
     * <p>
     *     return meter = constructor (dynname ())
     * </p>
     *
     * @param constructor specifies a {@link com.google.common.base.Function function} to obtain a named meter.
     * @param dynname specifies a lazy obtainable {@link com.google.common.base.Supplier name}.
     * @param <Q> specifies the quantity of the meter.
     * @return a supplier returning a named meter.
     */
    public final static <Q extends Quantity> Supplier<Meter<Q>> meter (Function<String, Meter<Q>> constructor, Supplier<String> dynname) {
        return compose (constructor, dynname);
    }

    /* lambda function pointer would be a great replacement */

    /**
     * <p>Resolution of the counter is deferred until the function is executed.</p>
     *
     * @param provider specifies the provider.
     * @return a {@link com.google.common.base.Function} object.
     */
    public final static Function<String, Meter<Dimensionless>> counterOf (final MeterProvider provider) {
        return new Function<String, Meter<Dimensionless>> () {

            @Override
            public final Meter<Dimensionless> apply (String name) {
                return provider.counter (name);
            }

        };
    }

    /**
     * <p>Resolution of the histogram is deferred until the function is executed.</p>
     *
     * @param provider specifies the provider.
     * @return a {@link com.google.common.base.Function} object.
     */
    public final static Function<String, Meter<Dimensionless>> histogramOf (final MeterProvider provider) {
        return new Function<String, Meter<Dimensionless>> () {

            @Override
            public final Meter<Dimensionless> apply (String name) {
                return provider.histogram (name);
            }

        };
    }

    /**
     * <p>Resolution of the meter is deferred until the function is executed.</p>
     *
     * @param provider specifies the provider.
     * @return a {@link com.google.common.base.Function} object.
     */
    public final static Function<String, Meter<Dimensionless>> meterOf (final MeterProvider provider) {
        return new Function<String, Meter<Dimensionless>> () {

            @Override
            public final Meter<Dimensionless> apply (String name) {
                return provider.meter (name);
            }

        };
    }

    /**
     * <p>Resolution of the timer is deferred until the function is executed.</p>
     *
     * @param provider specifies the provider.
     * @return a {@link com.google.common.base.Function} object.
     */
    public final static Function<String, Meter<Duration>> timerOf (final MeterProvider provider) {
        return new Function<String, Meter<Duration>> () {

            @Override
            public final Meter<Duration> apply (String name) {
                return provider.timer (name);
            }

        };
    }

}
