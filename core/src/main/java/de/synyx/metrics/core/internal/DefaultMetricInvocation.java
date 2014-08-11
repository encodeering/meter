package de.synyx.metrics.core.internal;

import de.synyx.metrics.core.MetricHook;
import de.synyx.metrics.core.MetricInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Date: 30.07.2014
 * Time: 08:48
 */
public final class DefaultMetricInvocation implements MetricInvocation {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    @Override
    public final Object invoke (Callable<Object> invocable, List<MetricHook> hooks) throws Throwable {
        logger.trace ("create metrics for invocable: {}", invocable);

        Object response = null;
        Throwable throwable = null;

        List<MetricHook> metrics = new ArrayList<> (hooks);
        List<MetricHook> called  = new ArrayList<> ();

        try {
            for (MetricHook metric : metrics) {
                try {
                            metric.before ();
                } catch (RuntimeException e) {
                    logger.warn (e.getMessage ());
                } finally {
                    called.add (metric);
                }
            }

            return response = invocable.call ();
        } catch (Throwable e) {
                  throwable = e;
            throw throwable;
        } finally {
            for (MetricHook metric : called) {
                try {
                            metric.after (response, throwable);
                } catch (RuntimeException e) {
                    logger.warn (e.getMessage ());
                }
            }
        }
    }

}
