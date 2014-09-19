package de.synyx.meter.core;

/**
 * Date: 16.07.2014
 * Time: 13:04
 */
public interface Measure {

    public abstract long determine (Object response, Throwable throwable);

}
