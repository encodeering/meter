package de.synyx.meter.core.aop;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Date: 05.06.2015
 * Time: 14:06
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface Advice {

    public abstract Object perform (MethodInvocation invocation) throws Throwable;

}
