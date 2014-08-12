package de.synyx.metrics.core.aspect;

import de.synyx.metrics.core.MetricAspect;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public abstract class MetricAspectSupport implements MetricAspect {

    public final static MetricAspect Noop = new MetricAspectSupport () {};

    @Override
    public void before () {}

    @Override
    public void after (Object response, Throwable throwable) {}

}
