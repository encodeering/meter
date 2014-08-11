package de.synyx.metrics.core.internal;

import de.synyx.metrics.core.MetricAspect;
import de.synyx.metrics.core.MetricAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Date: 30.07.2014
 * Time: 08:48
 */
public final class DefaultMetricAdvisor implements MetricAdvisor {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    @Override
    public final Object around (Callable<Object> invocable, List<MetricAspect> aspects) throws Throwable {
        logger.trace ("create metrics for invocable: {}", invocable);

        Object response = null;
        Throwable throwable = null;

        List<MetricAspect> auxiliary = new ArrayList<> (aspects);
        List<MetricAspect> called    = new ArrayList<> ();

        try {
            for (MetricAspect aspect : auxiliary) {
                try {
                              aspect.before ();
                } catch (RuntimeException e) {
                    logger.warn (e.getMessage ());
                } finally {
                    called.add (aspect);
                }
            }

            return response = invocable.call ();
        } catch (Throwable e) {
                  throwable = e;
            throw throwable;
        } finally {
            for (MetricAspect aspect : called) {
                try {
                              aspect.after (response, throwable);
                } catch (RuntimeException e) {
                    logger.warn (e.getMessage ());
                }
            }
        }
    }

}