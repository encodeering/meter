package de.synyx.metrics.core;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;

/**
 * Date: 12.08.2014
 * Time: 09:06
 */
public interface MeterProvider {

    public abstract Meter<Dimensionless> counter (String name);

    public abstract Meter<Dimensionless> histogram (String name);

    public abstract Meter<Dimensionless> meter (String name);

    public abstract Meter<Duration> timer (String name);

}
