package de.synyx.metrics.metrics.reporter;

import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.ganglia.GangliaReporter;
import com.google.common.base.Optional;
import info.ganglia.gmetric4j.gmetric.GMetric;
import info.ganglia.gmetric4j.gmetric.Protocol;
import info.ganglia.gmetric4j.gmetric.Protocolv30x;
import info.ganglia.gmetric4j.gmetric.Protocolv31x;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

public class MetricReportHandlerGangliaTest extends MetricReportTestSupport {

    @Test
    public void testScheme () throws Exception {
        assertThat (new MetricReportHandlerGanglia ().scheme (), Matchers.equalTo ("ganglia"));
    }

    @Test
    public void testReporter () throws Exception {
        Optional<ScheduledReporter> reporter;

                       reporter = new MetricReportHandlerGanglia ().select (mediator (), provider, URI.create ("ganglia://localhost:23443"));
        assertThat     (reporter.get (), Matchers.instanceOf (GangliaReporter.class));
        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        GangliaReporter ganglia = (GangliaReporter) reporter.get ();

        assertOption   (ganglia, null, 0, 60);
        assertProtocol (ganglia, Protocolv31x.class);
    }

    @Test
    public void testReporterExplicitV30 () throws NoSuchFieldException, IllegalAccessException {
        Optional<ScheduledReporter> reporter;

                    reporter = new MetricReportHandlerGanglia ().select (mediator (), provider, URI.create ("ganglia://localhost:23443?version=3.0"));
        assertThat (reporter.get (), Matchers.instanceOf (GangliaReporter.class));

        assertProtocol ((GangliaReporter) reporter.get (), Protocolv30x.class);
    }

    @Test
    public void testReporterExplicitV31 () throws NoSuchFieldException, IllegalAccessException {
        Optional<ScheduledReporter> reporter;

                    reporter = new MetricReportHandlerGanglia ().select (mediator (), provider, URI.create ("ganglia://localhost:23443?version=3.1"));
        assertThat (reporter.get (), Matchers.instanceOf (GangliaReporter.class));

        assertProtocol ((GangliaReporter) reporter.get (), Protocolv31x.class);
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testReporterExplicitUnknown () throws NoSuchFieldException, IllegalAccessException {
        Optional<ScheduledReporter> reporter;

                    reporter = new MetricReportHandlerGanglia ().select (mediator (), provider, URI.create ("ganglia://localhost:23443?version=w00t"));
        assertThat (reporter.get (), Matchers.instanceOf (GangliaReporter.class));
    }

    @Test
    public void testReporterQuery () throws NoSuchFieldException, IllegalAccessException {
        String prefix = anytext (UUID.randomUUID ());

        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerGanglia ().select (mediator (), provider, URI.create ("ganglia://localhost:23443?rate=m&duration=h&tmax=232&dmax=768&prefix=" + prefix));
        assertThat     (reporter.get (), Matchers.instanceOf (GangliaReporter.class));
        assertReporter (reporter.get (), registry, "minute", "hours");

        GangliaReporter ganglia = (GangliaReporter) reporter.get ();

        assertOption   (ganglia, prefix, 768, 232);
        assertProtocol (ganglia, Protocolv31x.class);
    }

    private void assertOption (GangliaReporter ganglia, String prefix, int dmax, int tmax) throws NoSuchFieldException, IllegalAccessException {
        assertThat (field (GangliaReporter.class, "prefix", String.class, ganglia), Matchers.equalTo (prefix));
        assertThat (field (GangliaReporter.class, "dMax", Integer.class, ganglia),  Matchers.equalTo (dmax));
        assertThat (field (GangliaReporter.class, "tMax", Integer.class, ganglia),  Matchers.equalTo (tmax));
    }

    private void assertProtocol (GangliaReporter ganglia, Class<? extends Protocol> protocol) throws NoSuchFieldException, IllegalAccessException {
        GMetric g = field (GangliaReporter.class, "ganglia", GMetric.class, ganglia);

        assertThat (field (GMetric.class, "protocol", Protocol.class, g), Matchers.instanceOf (protocol));
    }

}