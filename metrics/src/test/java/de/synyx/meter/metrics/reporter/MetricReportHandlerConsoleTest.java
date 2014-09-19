package de.synyx.meter.metrics.reporter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.PrintStream;
import java.net.URI;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

public class MetricReportHandlerConsoleTest extends MetricReportTestSupport {

    @Test
    public void testScheme () throws Exception {
        assertThat (new MetricReportHandlerConsole ().scheme (), Matchers.equalTo ("console"));
    }

    @Test
    public void testReporterOut () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerConsole ().select (mediator (), provider, URI.create ("console://stdout"));
        assertThat     (reporter.get (), Matchers.instanceOf (ConsoleReporter.class));
        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        ConsoleReporter console = (ConsoleReporter) reporter.get ();

        assertThat (field (ConsoleReporter.class, "output", PrintStream.class, console), Matchers.equalTo (System.out));
    }

    @Test
    public void testReporterErr () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerConsole ().select (mediator (), provider, URI.create ("console://stderr"));
        assertThat     (reporter.get (), Matchers.instanceOf (ConsoleReporter.class));
        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        ConsoleReporter console = (ConsoleReporter) reporter.get ();

        assertThat (field (ConsoleReporter.class, "output", PrintStream.class, console), Matchers.equalTo (System.err));
    }

    @Test
    public void testReporterNotFound () throws Exception {
        Optional<ScheduledReporter> reporter;

                    reporter = new MetricReportHandlerConsole ().select (mediator (), provider, URI.create ("console://" + anytext (UUID.randomUUID ())));
        assertThat (reporter.get (), Matchers.instanceOf (ConsoleReporter.class));

        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        ConsoleReporter console = (ConsoleReporter) reporter.get ();

        PrintStream stream = field (ConsoleReporter.class, "output", PrintStream.class, console);

        MatcherAssert.assertThat (stream, Matchers.not (Matchers.equalTo (System.out)));
        MatcherAssert.assertThat (stream, Matchers.not (Matchers.equalTo (System.err)));
        MatcherAssert.assertThat (stream, Matchers.notNullValue ());
    }

    @Test
    public void testReporterQuery () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerConsole ().select (mediator (), provider, URI.create ("console://stdout?rate=m&duration=h"));
        assertThat     (reporter.get (), Matchers.instanceOf (ConsoleReporter.class));
        assertReporter (reporter.get (), registry, "minute", "hours");

        ConsoleReporter console = (ConsoleReporter) reporter.get ();

        assertThat (field (ConsoleReporter.class, "output", PrintStream.class, console), Matchers.equalTo (System.out));
    }
}