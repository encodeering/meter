package de.synyx.meter.core.internal.aop;

import de.synyx.meter.core.aop.Advice;
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
    public final Advice around (List<Aspect> aspects) {
        return new DefaultAroundAdvice      (aspects);
    }

    private final class DefaultAroundAdvice implements Advice {

        private final List<Aspect> aspects;

        private DefaultAroundAdvice (List<Aspect> aspects) {
            this.aspects = new ArrayList<>       (aspects);
        }

        @Override
        public final Object perform (MethodInvocation invocation) throws Throwable {
            logger.trace ("create metrics for invocable: {}", invocation.getMethod ());

            List<Aspect> called = new ArrayList<> ();

            Object response = null;
            Throwable throwable = null;

            try {
                for (Aspect aspect : aspects) {
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
                        logger.warn ("around before {} {}", e.getMessage (), e.getClass ());
                    }
                }
            }
        }

    }

}
