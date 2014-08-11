package de.synyx.metrics.core.reporter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 01.08.2014
 * Time: 08:20
 */
public final class MetricReportMediator {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    private final MetricRegistry            registry;

    private final List<MetricReportHandler> handlers;

    public MetricReportMediator (MetricRegistry registry, MetricReportHandler... handlers) {
        this.registry = registry;
        this.handlers = Arrays.asList (handlers);
    }

    public final ScheduledReporter reporter (String reporturl) {
        try {
            URI location = URI.create (reporturl);

            for (MetricReportHandler handler : handlers) {
                Optional<ScheduledReporter> reporter;

                    reporter = handler.select (this, registry, location);
                if (reporter.isPresent ()) return reporter.get ();
            }
        } catch (RuntimeException e) {
            logger.error (e.getMessage ());
        }

        return null;
    }

}
