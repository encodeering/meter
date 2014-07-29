package de.synyx.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Date: 16.07.2014
 * Time: 10:59
 */
public final class MetricHookCommand {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    final Object invoke (Callable<Object> invocable, List<MetricHook> hooks) throws Throwable {
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
