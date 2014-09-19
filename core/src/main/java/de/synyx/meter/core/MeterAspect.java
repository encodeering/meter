package de.synyx.meter.core;

/**
 * Date: 16.07.2014
 * Time: 08:46
 */
public interface MeterAspect {

    public abstract void before ();

    public abstract void after  (Object response, Throwable throwable);

}
