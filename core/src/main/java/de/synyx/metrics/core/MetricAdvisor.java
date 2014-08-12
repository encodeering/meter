package de.synyx.metrics.core;

import org.aopalliance.intercept.MethodInvocation;

import java.util.List;

/**
 * Date: 16.07.2014
 * Time: 10:59
 */
public abstract interface MetricAdvisor {

    public abstract Object around (MethodInvocation invocation, List<MetricAspect> aspects) throws Throwable;

}
