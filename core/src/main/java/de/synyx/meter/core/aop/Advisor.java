package de.synyx.meter.core.aop;

import org.aopalliance.intercept.MethodInvocation;

import java.util.List;

/**
 * <p>Any type implementing this advisor should execute the dynamic code parts using an execute-around strategy.</p>
 * <p>
 *     The order of the underlying iterator specifies the aspect's execution order, which means, they will be executed
 *     from the first to the last entry.
 *
 *     The order should be kept constant during different invocations, as the behaviour is undefined, if the provided metrics
 *     are coupled directly or indirectly.
 * </p>
 *
 * Date: 16.07.2014
 * Time: 10:59
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public abstract interface Advisor {

    /**
     * <p>Executes all {@code aspects} before and after the real method invocation.</p>

     *     <ol>
     *         <li>Execute all provided aspects beforehand and ignore any runtime-exception thrown silently.</li>
     *         <li>Delegate to the invocation context and keep either the return value or exception.</li>
     *         <li>Execute all provided aspects in the reverse order using the results from 2 and ignore any runtime-exception thrown silently.</li>
     *         <li>Return the value from 2 either by re-throwing the exception or by returning the value.</li>
     *     </ol>

     * <p>
     *     Any checked exception thrown by an aspect will be treated as if they would have been thrown by the invocation context.
     *     An aspect is allowed to throw <b>runtime-exceptions</b> only.
     * </p>
     *
     * @param invocation specifies the target {@link org.aopalliance.intercept.MethodInvocation} object.
     * @param aspects specifies the {@link java.util.List} of aspects executed around the real invocation.
     *
     * @return an {@link java.lang.Object object} returned from the real invocation.
     * @throws java.lang.Throwable if the invocation failed or if an aspect is challenged by a checked exception.
     */
    public abstract Object around (MethodInvocation invocation, List<Aspect> aspects) throws Throwable;

}
