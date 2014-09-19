package de.synyx.meter.core;

import org.aopalliance.intercept.MethodInvocation;

import java.util.List;

/**
 * Date: 16.07.2014
 * Time: 10:59
 */
public abstract interface MeterAdvisor {

    public abstract Object around (MethodInvocation invocation, List<MeterAspect> aspects) throws Throwable;

}
