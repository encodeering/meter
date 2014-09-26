package de.synyx.meter.core;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;

/**
 * <p>Defines a SPI Factory to resolve named meter instances from an underlying provider.</p>
 * <p>
 *     A provider shall return semantically identical meters for the same name, but may return different meters with respect to their referential identities.
 * </p>
 * Date: 12.08.2014
 * Time: 09:06
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface MeterProvider {

    /**
     * <p>Returns a named counter meter.</p>
     *
     * @param name specifies the name of the counter.
     * @return a unique meter.
     * @see de.synyx.meter.core.annotation.Counter
     */
    public abstract Meter<Dimensionless> counter (String name);

    /**
     * <p>Returns a named histogram meter.</p>
     *
     * @param name specifies the name of the histogram.
     * @return a unique meter.
     * @see de.synyx.meter.core.annotation.Histogram
     */
    public abstract Meter<Dimensionless> histogram (String name);

    /**
     * <p>Returns a named meter.</p>
     *
     * @param name specifies the name of the meter.
     * @return a unique meter.
     * @see de.synyx.meter.core.annotation.Meter
     */
    public abstract Meter<Dimensionless> meter (String name);

    /**
     * <p>Returns a named timer.</p>
     *
     * @param name specifies the name of the timer.
     * @return a unique meter.
     * @see de.synyx.meter.core.annotation.Timer
     */
    public abstract Meter<Duration> timer (String name);

    /**
     * <p>Unwraps the underlying provider if the {@code type} is compatible.</p>
     *
     * @param type specifies the {@link java.lang.Class class type} of the underlying provider.
     * @param <T> specifies the the generic type.
     * @return an instance of the provider or {@code null} if the {@code type} is not compatible.
     */
    public abstract <T> T unwrap (Class<? extends T> type);
}
