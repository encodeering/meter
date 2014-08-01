package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;

import java.net.URI;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Date: 31.07.2014
 * Time: 15:50
 */
public final class MetricReportHandlerJndi extends MetricReportHandler {

    private final InitialContext context;

    public MetricReportHandlerJndi (InitialContext context) {
        this.context = context;
    }

    protected final String scheme () {
        return "java";
    }

    @Override
    protected final ScheduledReporter reporter (MetricRegistry registry, URI location) {
        try {
            Object lookup;

                lookup = context.lookup (location.toString ());
            if (lookup instanceof ScheduledReporter) return (ScheduledReporter) lookup;
        } catch (NamingException e) {
            logger.error (e.getMessage ());
        }

        return null;
    }

}
