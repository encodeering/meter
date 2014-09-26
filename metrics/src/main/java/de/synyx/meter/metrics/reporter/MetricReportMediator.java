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
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class MetricReportMediator {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    private final MeterProvider provider;

    private final List<MetricReportHandler> handlers;

    /**
     * <p>Constructor for MetricReportMediator.</p>
     *
     * @param provider a {@link de.synyx.meter.core.MeterProvider} object.
     * @param handlers a {@link de.synyx.meter.metrics.reporter.MetricReportHandler} object.
     */
    public MetricReportMediator (MeterProvider provider, MetricReportHandler... handlers) {
        this.provider = provider;
        this.handlers = Arrays.asList (handlers);
    }

    /**
     * <p>Creates a scheduled reporter for the given url.</p>
     *
     * @param reporturl specifies a {@link java.lang.String URI} to determine and configure the right logger.
     * @return a {@link com.codahale.metrics.ScheduledReporter} object or null if a reporter cannot be created.
     */
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
