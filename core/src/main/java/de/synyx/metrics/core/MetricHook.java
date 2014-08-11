package de.synyx.metrics.core;

/**
 * Date: 16.07.2014
 * Time: 08:46
 */
public interface MetricHook {

    public abstract void before ();

    public abstract void after  (Object response, Throwable throwable);

}
