package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.ScheduledReporter;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MetricReportMediatorTest extends MetricReportTestSupport {

    @Test
    public void testReportHandlerSchemeKnown () {
        MetricReportHandler handler = handler (anyurl.getScheme (), reporter);

        MetricReportMediator mediator;

                    mediator = new MetricReportMediator (registry, handler);
        assertThat (mediator.reporter (anyurl.toString ()), equalTo (reporter));
    }

    @Test
    public void testReportHandlerSchemeUnknown () {
        MetricReportHandler handler = spy (handler (anyuri (UUID.randomUUID ()).getScheme (), mock (ScheduledReporter.class)));

        MetricReportMediator mediator;

                    mediator = new MetricReportMediator (registry, handler);
        assertThat (mediator.reporter (anyurl.toString ()), nullValue ());

        verify (handler, never ()).reporter (mediator, registry, anyurl);
    }

    @Test
    public void testReportHandlerEmpty () {
        MetricReportMediator mediator;

                    mediator = new MetricReportMediator (registry);
        assertThat (mediator.reporter (anyurl.toString ()), nullValue ());

        Mockito.verifyZeroInteractions (registry);
    }

    @Test
    public void testReportHandlerMultipleOneKnown () {
        MetricReportHandler handlerA = spy (handler (anyuri (UUID.randomUUID ()).getScheme (), mock (ScheduledReporter.class)));
        MetricReportHandler handlerB = spy (handler (anyurl.getScheme (), reporter));

        MetricReportMediator mediator;

                    mediator = new MetricReportMediator (registry, handlerA, handlerB);
        assertThat (mediator.reporter (anyurl.toString ()), equalTo (reporter));

        verify (handlerA).scheme ();
        verify (handlerA, never ()).reporter (mediator, registry, anyurl);
    }

    @Test
    public void testReportHandlerMultipleUnknown () {
        MetricReportHandler handlerA = spy (handler (anyuri (UUID.randomUUID ()).getScheme (), mock (ScheduledReporter.class)));
        MetricReportHandler handlerB = spy (handler (anyuri (UUID.randomUUID ()).getScheme (), mock (ScheduledReporter.class)));

        MetricReportMediator mediator;

                    mediator = new MetricReportMediator (registry, handlerA, handlerB);
        assertThat (mediator.reporter (anyurl.toString ()), nullValue ());

        verify (handlerA).scheme ();
        verify (handlerA, never ()).reporter (mediator, registry, anyurl);

        verify (handlerB).scheme ();
        verify (handlerB, never ()).reporter (mediator, registry, anyurl);
    }

    @Test
    public void testReportHandlerException () {
        MetricReportHandler handler;

              handler = spy (handler (anyurl.getScheme (), reporter));
        when (handler.reporter (any (MetricReportMediator.class), eq (registry), eq (anyurl))).thenThrow (new IllegalStateException ());

        MetricReportMediator mediator;

                    mediator = new MetricReportMediator (registry, handler);
        assertThat (mediator.reporter (anyurl.toString ()), nullValue ());

        verify (handler).reporter (mediator, registry, anyurl);
    }

}