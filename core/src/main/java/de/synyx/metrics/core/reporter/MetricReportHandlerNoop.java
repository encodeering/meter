package de.synyx.metrics.core.reporter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;

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
    protected final ScheduledReporter reporter (MetricReportMediator mediator, MetricRegistry registry, URI location) {
        return null;
    }

}
