package de.synyx.meter.metrics.reporter;

import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Optional;
import de.synyx.meter.core.MeterProvider;
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

    private final MeterProvider provider;

    private final List<MetricReportHandler> handlers;

    public MetricReportMediator (MeterProvider provider, MetricReportHandler... handlers) {
        this.provider = provider;
        this.handlers = Arrays.asList (handlers);
    }

    public final ScheduledReporter reporter (String reporturl) {
        try {
            URI location = URI.create (reporturl);

            for (MetricReportHandler handler : handlers) {
                Optional<ScheduledReporter> reporter;

                    reporter = handler.select (this, provider, location);
                if (reporter.isPresent ()) return reporter.get ();
            }
        } catch (RuntimeException e) {
            logger.error (e.getMessage ());
        }

        return null;
    }

}
