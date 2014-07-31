package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Optional;

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

    @Override
    protected final Optional<ScheduledReporter> reporter (MetricRegistry registry, URI location) {
        if (!"java".equals (location.getScheme ())) return Optional.absent ();

        try {
            Object lookup;

                lookup = context.lookup (location.toString ());
            if (lookup instanceof ScheduledReporter) return Optional.of ((ScheduledReporter) lookup);
        } catch (NamingException e) {
            logger.error (e.getMessage ());
        }

        return Optional.absent ();
    }

}
