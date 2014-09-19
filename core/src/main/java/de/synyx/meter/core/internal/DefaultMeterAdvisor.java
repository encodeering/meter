package de.synyx.meter.core.internal;

import de.synyx.meter.core.MeterAdvisor;
import de.synyx.meter.core.MeterAspect;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 30.07.2014
 * Time: 08:48
 */
public final class DefaultMeterAdvisor implements MeterAdvisor {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    @Override
    public final Object around (MethodInvocation invocation, List<MeterAspect> aspects) throws Throwable {
        logger.trace ("create metrics for invocable: {}", invocation.getMethod ());

        Object response = null;
        Throwable throwable = null;

        List<MeterAspect> auxiliary = new ArrayList<> (aspects);
        List<MeterAspect> called    = new ArrayList<> ();

        try {
            for (MeterAspect aspect : auxiliary) {
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
            for (MeterAspect aspect : called) {
                try {
                             aspect.after (response, throwable);
                } catch (RuntimeException e) {
                    logger.warn ("around after {} {}", e.getMessage (), e.getClass ());
                }
            }
        }
    }

}
