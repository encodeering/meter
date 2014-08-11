package de.synyx.metrics.core.reporter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Optional;
import org.junit.Test;

import java.io.PrintStream;
import java.net.URI;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class MetricReportHandlerConsoleTest extends MetricReportTestSupport {

    @Test
    public void testScheme () throws Exception {
        assertThat (new MetricReportHandlerConsole ().scheme (), equalTo ("console"));
    }

    @Test
    public void testReporterOut () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerConsole ().select (mediator (), registry, URI.create ("console://stdout"));
        assertThat     (reporter.get (), instanceOf (ConsoleReporter.class));
        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        ConsoleReporter console = (ConsoleReporter) reporter.get ();

        assertThat (field (ConsoleReporter.class, "output", PrintStream.class, console), equalTo (System.out));
    }

    @Test
    public void testReporterErr () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerConsole ().select (mediator (), registry, URI.create ("console://stderr"));
        assertThat     (reporter.get (), instanceOf (ConsoleReporter.class));
        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        ConsoleReporter console = (ConsoleReporter) reporter.get ();

        assertThat (field (ConsoleReporter.class, "output", PrintStream.class, console), equalTo (System.err));
    }

    @Test
    public void testReporterNotFound () throws Exception {
        Optional<ScheduledReporter> reporter;

                    reporter = new MetricReportHandlerConsole ().select (mediator (), registry, URI.create ("console://" + anytext (UUID.randomUUID ())));
        assertThat (reporter.get (), instanceOf (ConsoleReporter.class));

        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        ConsoleReporter console = (ConsoleReporter) reporter.get ();

        PrintStream stream = field (ConsoleReporter.class, "output", PrintStream.class, console);

        assertThat (stream, not (equalTo (System.out)));
        assertThat (stream, not (equalTo (System.err)));
        assertThat (stream, notNullValue ());
    }

    @Test
    public void testReporterQuery () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerConsole ().select (mediator (), registry, URI.create ("console://stdout?rate=m&duration=h"));
        assertThat     (reporter.get (), instanceOf (ConsoleReporter.class));
        assertReporter (reporter.get (), registry, "minute", "hours");

        ConsoleReporter console = (ConsoleReporter) reporter.get ();

        assertThat (field (ConsoleReporter.class, "output", PrintStream.class, console), equalTo (System.out));
    }
}