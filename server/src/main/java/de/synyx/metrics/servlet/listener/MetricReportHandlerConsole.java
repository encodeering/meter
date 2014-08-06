package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.io.ByteStreams;

import java.io.PrintStream;
import java.net.URI;
import java.util.Map;

/**
 * Date: 06.08.2014
 * Time: 11:39
 */
public final class MetricReportHandlerConsole extends MetricReportHandler {

    @Override
    protected final String scheme () {
        return "console";
    }

    @Override
    protected final ScheduledReporter reporter (MetricReportMediator mediator, MetricRegistry registry, URI location) {
        Map<String, String> parameters = parameters (location.getQuery ());

        PrintStream out = choose (location);

        ConsoleReporter reporter = ConsoleReporter.forRegistry (registry)
                                          .outputTo (null)
                                          .convertRatesTo     (timeunit (or (parameters.get ("rate"),     "ms")))
                                          .convertDurationsTo (timeunit (or (parameters.get ("duration"),  "s")))
                                              .filter (MetricFilter.ALL)
                                                  .build ();

        return start (reporter, parameters.get ("refresh"));
    }

    private PrintStream choose (URI location) {
        String authority;

                    authority = location.getAuthority ();
        if (        authority != null)
            switch (authority) {
                case "stdout": return System.out;
                case "stderr": return System.err;
            }

        return new PrintStream (ByteStreams.nullOutputStream ());
    }

}
