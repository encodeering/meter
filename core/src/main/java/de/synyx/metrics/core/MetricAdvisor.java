package de.synyx.metrics.core;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Date: 16.07.2014
 * Time: 10:59
 */
public abstract interface MetricAdvisor {

    public abstract Object around (Callable<Object> invocable, List<MetricAspect> aspects) throws Throwable;

}
