package de.synyx.metrics.metrics.reporter;

import com.codahale.metrics.ScheduledReporter;
import de.synyx.metrics.core.MeterProvider;

import java.net.URI;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Date: 31.07.2014
 * Time: 15:50
 */
public final class MetricReportHandlerJndi extends MetricReportHandler {

    private final Context context;

    public MetricReportHandlerJndi (Context context) {
        this.context = context;
    }

    protected final String scheme () {
        return "java";
    }

    @Override
    protected final ScheduledReporter reporter (MetricReportMediator mediator, MeterProvider registry, URI location) {
        try {
            Object lookup;

                lookup = context.lookup (location.toString ());
            if (lookup instanceof String) return mediator.reporter ((String) lookup);
            if (lookup instanceof URI)    return mediator.reporter (lookup.toString ());
        } catch (NamingException e) {
            logger.error (e.getMessage ());
        }

        return null;
    }

}
