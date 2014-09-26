package de.synyx.meter.metrics.reporter;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Strings;
import de.synyx.meter.core.MeterProvider;

import java.io.File;
import java.net.URI;
import java.util.Map;

/**
 * Date: 06.08.2014
 * Time: 11:39
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class MetricReportHandlerCsv extends MetricReportHandler {

    /** {@inheritDoc} */
    @Override
    protected final String scheme () {
        return "csv";
    }

    /** {@inheritDoc} */
    @Override
    protected final ScheduledReporter reporter (MetricReportMediator mediator, MeterProvider provider, URI location) {
        Map<String, String> parameters = parameters (location);

        File csv = new File (refine (location));

        CsvReporter reporter = CsvReporter.forRegistry (provider.unwrap (MetricRegistry.class))
                                          .convertRatesTo     (timeunit (or (parameters.get ("rate"),    "ms")))
                                          .convertDurationsTo (timeunit (or (parameters.get ("duration"), "s")))
                                              .filter (MetricFilter.ALL)
                                                  .build (csv);

        return start (reporter, parameters.get ("refresh"));
    }

    private URI refine (URI location) {
        String ssp= location.getSchemeSpecificPart ();

        return URI.create ("file:" + align (ssp) + file (ssp));
    }

    private String align (String ssp) {
        String prefix;

                                    prefix = "///";
        return Strings.repeat ("/", prefix.length () - Strings.commonPrefix (ssp, prefix).length ());
    }

    private String file (String ssp) {
        int idx = ssp.indexOf ('?');
        if (idx < 0) return ssp;
        else
            return ssp.substring (0, idx);
    }

}
