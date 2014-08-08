package de.synyx.metrics.servlet.listener;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class MetricReportHandlerNoopTest extends MetricReportTestSupport {

    @Test
    public void testScheme () throws Exception {
        assertThat (new MetricReportHandlerNoop ().scheme (), nullValue ());
    }

    @Test
    public void testReporter () throws Exception {
        assertThat (new MetricReportHandlerNoop ().reporter (null, null, null), nullValue ());
    }

}