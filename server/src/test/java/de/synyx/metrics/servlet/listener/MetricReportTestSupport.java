package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.util.UUID;

/**
 * Date: 06.08.2014
 * Time: 15:44
 */
@RunWith (MockitoJUnitRunner.class)
public class MetricReportTestSupport {

    @Mock
    protected MetricRegistry registry;

    @Mock
    protected ScheduledReporter reporter;

    final URI anyurl = anyuri (UUID.randomUUID ());

    final MetricReportHandler handler (final String scheme, final ScheduledReporter reporter) {
        class TestReportHandler extends MetricReportHandler {

            @Override
            protected String scheme () {
                return scheme;
            }

            @Override
            protected ScheduledReporter reporter (MetricReportMediator mediator, MetricRegistry registry, URI location) {
                return reporter;
            }
        }

        return new TestReportHandler ();
    }

    final URI anyuri (UUID uuid) {
        /* prefix with a letter as uri schemes aren't allowed to start with a numerical value */
        return URI.create ("A" + anytext (uuid) + "://" + anytext (UUID.randomUUID ()));
    }

    final String anytext (UUID uuid) {
        return uuid.toString ().replace ("-", "");
    }

}
