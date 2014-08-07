package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.google.common.base.Optional;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

public class MetricReportHandlerGraphiteTest extends MetricReportTestSupport {

    @Test
    public void testScheme () throws Exception {
        assertThat (new MetricReportHandlerGraphite ().scheme (), equalTo ("graphite"));
    }

    @Test
    public void testReporter () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerGraphite ().select (mediator (), registry, URI.create ("graphite://localhost:23443"));
        assertThat     (reporter.get (), instanceOf (GraphiteReporter.class));
        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        GraphiteReporter graphite = (GraphiteReporter) reporter.get ();

        assertOption  (graphite, null);
        assertAddress (graphite, new InetSocketAddress ("locahost", 23443));
    }

    @Test
    public void testReporterQuery () throws Exception {
        String prefix = anytext (UUID.randomUUID ());

        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerGraphite ().select (mediator (), registry, URI.create ("graphite://localhost:23443?rate=m&duration=h&prefix=" + prefix));
        assertThat     (reporter.get (), instanceOf (GraphiteReporter.class));
        assertReporter (reporter.get (), registry, "minute", "hours");

        GraphiteReporter graphite = (GraphiteReporter) reporter.get ();

        assertOption  (graphite, prefix);
        assertAddress (graphite, new InetSocketAddress ("locahost", 23443));
    }

    @Test (expected = RuntimeException.class)
    public void testReporterNoAddress () {
        new MetricReportHandlerGraphite ().select (mediator (), registry, URI.create ("graphite://?"));
    }

    private void assertOption (GraphiteReporter graphite, String prefix) throws NoSuchFieldException, IllegalAccessException {
        assertThat (field (GraphiteReporter.class, "prefix", String.class, graphite), equalTo (prefix));
    }

    private void assertAddress (GraphiteReporter graphite, InetSocketAddress address) throws NoSuchFieldException, IllegalAccessException {
        Graphite g = field (GraphiteReporter.class, "graphite", Graphite.class, graphite);

        assertThat (field (Graphite.class, "address", InetSocketAddress.class, g), equalTo (new InetSocketAddress ("localhost", 23443)));
    }

}