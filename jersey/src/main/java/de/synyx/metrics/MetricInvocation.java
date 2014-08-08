package de.synyx.metrics;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Date: 16.07.2014
 * Time: 10:59
 */
public abstract interface MetricInvocation {

    public abstract Object invoke (Callable<Object> invocable, List<MetricHook> hooks) throws Throwable;

}
