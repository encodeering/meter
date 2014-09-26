package de.synyx.meter.core.internal.aop;

import de.synyx.meter.core.aop.Advisor;
import de.synyx.meter.core.aop.Aspect;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 30.07.2014
 * Time: 08:48
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class DefaultAdvisor implements Advisor {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    /** {@inheritDoc} */
    @Override
    public final Object around (MethodInvocation invocation, List<Aspect> aspects) throws Throwable {
        logger.trace ("create metrics for invocable: {}", invocation.getMethod ());

        Object response = null;
        Throwable throwable = null;

        List<Aspect> auxiliary = new ArrayList<> (aspects);
        List<Aspect> called    = new ArrayList<> ();

        try {
            for (Aspect aspect : auxiliary) {
                try {
                        aspect.before ();
                } catch (RuntimeException e) {
                    logger.warn ("around before {} {}", e.getMessage (), e.getClass ());
                } finally {
                    called.add (aspect);
                }
            }

            return response = invocation.proceed ();
        } catch (Throwable e) {
                  throwable = e;
            throw throwable;
        } finally {
            for (Aspect aspect : called) {
                try {
                        aspect.after (response, throwable);
                } catch (RuntimeException e) {
                    logger.warn ("around after {} {}", e.getMessage (), e.getClass ());
                }
            }
        }
    }

}
