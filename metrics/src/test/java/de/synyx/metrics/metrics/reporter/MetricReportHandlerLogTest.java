package de.synyx.metrics.metrics.reporter;

import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.base.Optional;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;

public class MetricReportHandlerLogTest extends MetricReportTestSupport {

    @Test
    public void testScheme () throws Exception {
        assertThat (new MetricReportHandlerLog ().scheme (), equalTo ("log"));
    }

    @Test
    public void testReporter () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerLog ().select (mediator (), provider, URI.create ("log://sample.logger?marker=sample.marker"));
        assertThat     (reporter.get (), instanceOf (Slf4jReporter.class));
        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        Slf4jReporter slf4j = (Slf4jReporter) reporter.get ();

        assertOption (slf4j, "sample.logger", "sample.marker");
    }

    @Test
    public void testReporterQuery () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerLog ().select (mediator (), provider, URI.create ("log://sample.logger?rate=m&duration=h"));
        assertThat     (reporter.get (), instanceOf (Slf4jReporter.class));
        assertReporter (reporter.get (), registry, "minute", "hours");

        Slf4jReporter slf4j = (Slf4jReporter) reporter.get ();

        assertOption (slf4j, "sample.logger", null);
    }

    @Test
    public void testReporterNoAuthority () throws NoSuchFieldException, IllegalAccessException, URISyntaxException {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerLog ().select (mediator (), provider, URI.create ("log://?rate=m&duration=h"));
        assertThat     (reporter.get (), instanceOf (Slf4jReporter.class));
        assertReporter (reporter.get (), registry, "minute", "hours");

        Slf4jReporter slf4j = (Slf4jReporter) reporter.get ();

        assertOption (slf4j, "com.codahale.metrics.application.logger", null);
    }

    private void assertOption (Slf4jReporter slf4j, String logname, String markername) throws NoSuchFieldException, IllegalAccessException {
        assertThat (field (Slf4jReporter.class, "logger", Logger.class, slf4j).getName (), equalTo (logname));

        Marker marker;

            marker = field (Slf4jReporter.class, "marker", Marker.class, slf4j);
        if (marker == null)
            assertThat (markername, nullValue ());
        else
            assertThat (marker.getName (), equalTo (markername));
    }

}
