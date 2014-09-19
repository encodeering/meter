package de.synyx.meter.core;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;

import static com.google.common.base.Suppliers.compose;

/**
 * Date: 12.08.2014
 * Time: 10:37
 */
public final class MeterProviders {

    private MeterProviders () {}

    public final static <Q extends Quantity> Supplier<Meter<Q>> meter (Function<String, Meter<Q>> constructor, Supplier<String> dynname) {
        return compose (constructor, dynname);
    }

    /* lambda function pointer would be a great replacement */

    public final static Function<String, Meter<Dimensionless>> counterOf (final MeterProvider provider) {
        return new Function<String, Meter<Dimensionless>> () {

            @Override
            public final Meter<Dimensionless> apply (String name) {
                return provider.counter (name);
            }

        };
    }

    public final static Function<String, Meter<Dimensionless>> histogramOf (final MeterProvider provider) {
        return new Function<String, Meter<Dimensionless>> () {

            @Override
            public final Meter<Dimensionless> apply (String name) {
                return provider.histogram (name);
            }

        };
    }

    public final static Function<String, Meter<Dimensionless>> meterOf (final MeterProvider provider) {
        return new Function<String, Meter<Dimensionless>> () {

            @Override
            public final Meter<Dimensionless> apply (String name) {
                return provider.meter (name);
            }

        };
    }

    public final static Function<String, Meter<Duration>> timerOf (final MeterProvider provider) {
        return new Function<String, Meter<Duration>> () {

            @Override
            public final Meter<Duration> apply (String name) {
                return provider.timer (name);
            }

        };
    }

}
