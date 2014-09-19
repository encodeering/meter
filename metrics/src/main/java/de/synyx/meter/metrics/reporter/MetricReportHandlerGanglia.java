package de.synyx.meter.metrics.reporter;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.ganglia.GangliaReporter;
import de.synyx.meter.core.MeterProvider;
import info.ganglia.gmetric4j.gmetric.GMetric;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Date: 31.07.2014
 * Time: 14:40
 */
public final class MetricReportHandlerGanglia extends MetricReportHandler {

    protected final String scheme () {
        return "ganglia";
    }

    @Override
    protected final ScheduledReporter reporter (MetricReportMediator mediator, MeterProvider provider, URI uri) {
        Map<String, String> parameters = parameters (uri);

        GMetric ganglia = ganglia (uri, parameters);

        GangliaReporter reporter = GangliaReporter.forRegistry (provider.unwrap (MetricRegistry.class))
                                            .prefixedWith       (             (parameters.get ("prefix")        ))
                                            .convertRatesTo     (timeunit (or (parameters.get ("rate"),    "ms")))
                                            .convertDurationsTo (timeunit (or (parameters.get ("duration"), "s")))
                                            .withDMax           (number   (or (parameters.get ("dmax"),     "0")))
                                            .withTMax           (number   (or (parameters.get ("tmax"),    "60")))
                                                .filter (MetricFilter.ALL)
                                                    .build (ganglia);

        return start (reporter, parameters.get ("refresh"));
    }

    private int number (String value) {
        return Integer.parseInt (value, 10);
    }

    private GMetric ganglia (URI uri, Map<String, String> parameters) {
        int ttl = number (or (parameters.get ("ttl"), "1"));

        String mode    = or (parameters.get ("mode"),    determine (uri).name ());
        String version = or (parameters.get ("version"), "3.1");

        try {
            switch (version) {
                case "3.0": return new GMetric (uri.getHost (), uri.getPort (), udpmode (mode), ttl, false);
                case "3.1": return new GMetric (uri.getHost (), uri.getPort (), udpmode (mode), ttl, true);
            }
        } catch (IOException e) {
            throw new RuntimeException (e.getMessage (), e);
        }

         throw new UnsupportedOperationException ("Version " + version + " unsupported. Only 3.0 and 3.1 is currently supported");
    }

    private GMetric.UDPAddressingMode udpmode (String mode) {
        switch (mode.toLowerCase ()) {
            case "multicast": return GMetric.UDPAddressingMode.MULTICAST;
            case "unicast"  : return GMetric.UDPAddressingMode.UNICAST;
        }

        throw new UnsupportedOperationException ("Mode " + mode + " unsupported");
    }

    private GMetric.UDPAddressingMode determine (URI uri) {
        try {
            return GMetric.UDPAddressingMode.getModeForAddress (uri.getHost ());
        } catch (IOException e) {
            LoggerFactory.getLogger (getClass ()).warn ("udp-mode couldn't be resolved due {}", e.getMessage ());

            return GMetric.UDPAddressingMode.MULTICAST;
        }
    }

}
