package de.synyx.meter.core;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

/**
 * <p>A meter <i>collects</i> discrete measurement points and is allowed to pack the input values internally.</p>
 *
 * Date: 11.08.2014
 * Time: 16:01
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface Meter<Q extends Quantity> {

    /**
     * <p>Updates {@code this} meter by the provided point</p>
     *
     * @param point specifies a {@link javax.measure.Measurable} point of quantity {@link Q}.
     * @return {@code this} meter.
     */
    public abstract Meter<Q> update (Measurable<Q> point);

}
