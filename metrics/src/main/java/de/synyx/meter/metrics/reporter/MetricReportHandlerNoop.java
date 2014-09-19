package de.synyx.meter.metrics.reporter;

import com.codahale.metrics.ScheduledReporter;
import de.synyx.meter.core.MeterProvider;

import java.net.URI;

/**
* Date: 01.08.2014
* Time: 11:27
*/
public final class MetricReportHandlerNoop extends MetricReportHandler {

    @Override
    protected final String scheme () {
        return null;
    }

    @Override
    protected final ScheduledReporter reporter (MetricReportMediator mediator, MeterProvider provider, URI location) {
        return null;
    }

}
