package de.synyx.meter.core;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

/**
 * Date: 11.08.2014
 * Time: 16:01
 */
public interface Meter<Q extends Quantity> {

    public abstract Meter<Q> update (Measurable<Q> point);

}