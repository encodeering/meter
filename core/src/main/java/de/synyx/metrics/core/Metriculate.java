package de.synyx.metrics.core;

/**
 * Date: 16.07.2014
 * Time: 13:04
 */
public interface Metriculate {

    public abstract long determine (Object response, Throwable throwable);

}
