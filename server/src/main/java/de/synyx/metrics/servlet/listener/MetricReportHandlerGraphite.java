package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.google.common.base.Optional;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

/**
 * Date: 31.07.2014
 * Time: 14:40
 */
public final class MetricReportHandlerGraphite extends MetricReportHandler {

    @Override
    protected final Optional<ScheduledReporter> reporter (MetricRegistry registry, URI uri) {
        if (!        "graphite".equals (uri.getScheme ())) return Optional.absent ();

        logger.info ("graphite reporter detected: " + uri);

        Map<String, String> parameters = parameters (uri.getQuery ());

        Graphite graphite = new Graphite (new InetSocketAddress (uri.getHost (), uri.getPort ()));

        GraphiteReporter reporter = GraphiteReporter.forRegistry (registry)
                                            .convertRatesTo     (timeunit (or (parameters.get ("rate"),    "ms")))
                                            .convertDurationsTo (timeunit (or (parameters.get ("duration"), "s")))
                                                .filter (MetricFilter.ALL)
                                                    .build (graphite);

        return Optional.of (start (reporter, parameters.get ("refresh")));
    }

}
