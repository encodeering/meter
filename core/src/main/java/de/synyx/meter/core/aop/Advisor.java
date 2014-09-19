package de.synyx.meter.core.aop;

import org.aopalliance.intercept.MethodInvocation;

import java.util.List;

/**
 * Date: 16.07.2014
 * Time: 10:59
 */
public abstract interface Advisor {

    public abstract Object around (MethodInvocation invocation, List<Aspect> aspects) throws Throwable;

}
