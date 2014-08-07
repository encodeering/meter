package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Optional;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

public class MetricReportHandlerCsvTest extends MetricReportTestSupport {

    private String filename = anytext (UUID.randomUUID ());

    @Test
    public void testScheme () throws Exception {
        assertThat (new MetricReportHandlerCsv ().scheme (), equalTo ("csv"));
    }

    @Test
    public void testReporter () throws Exception {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerCsv ().select (mediator (), registry, URI.create ("csv://c:/" + filename));
        assertThat     (reporter.get (), instanceOf (CsvReporter.class));
        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        CsvReporter csv = (CsvReporter) reporter.get ();

        assertThat (field (CsvReporter.class, "directory", File.class, csv), equalTo (new File ("c:/" + filename)));
    }

    @Test
    public void testReporterQuery () throws NoSuchFieldException, IllegalAccessException {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerCsv ().select (mediator (), registry, URI.create ("csv://c:/" + filename + "?rate=m&duration=h"));
        assertThat     (reporter.get (), instanceOf (CsvReporter.class));
        assertReporter (reporter.get (), registry, "minute", "hours");

        CsvReporter csv = (CsvReporter) reporter.get ();

        assertThat (field (CsvReporter.class, "directory", File.class, csv), equalTo (new File ("c:/" + filename)));
    }

    @Test
    public void testReporterQueryEmpty () throws NoSuchFieldException, IllegalAccessException {
        Optional<ScheduledReporter> reporter;

                        reporter = new MetricReportHandlerCsv ().select (mediator (), registry, URI.create ("csv://c:/" + filename + "?"));
        assertThat     (reporter.get (), instanceOf (CsvReporter.class));
        assertReporter (reporter.get (), registry, "millisecond", "seconds");

        CsvReporter csv = (CsvReporter) reporter.get ();

        assertThat (field (CsvReporter.class, "directory", File.class, csv), equalTo (new File ("c:/" + filename)));
    }

}