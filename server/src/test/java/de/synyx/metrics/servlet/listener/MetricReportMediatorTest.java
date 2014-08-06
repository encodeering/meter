package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
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

@RunWith (MockitoJUnitRunner.class)
public class MetricReportMediatorTest {

    @Mock
    private MetricRegistry registry;

    @Mock
    private ScheduledReporter reporter;

    private final URI anyurl = anyuri (UUID.randomUUID ());

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

    private MetricReportHandler handler (final String scheme, final ScheduledReporter reporter) {
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

    private URI anyuri (UUID uuid) {
        /* prefix with a letter as uri schemes aren't allowed to start with a numerical value */
        return URI.create ("A" + anytext (uuid) + "://" + anytext (UUID.randomUUID ()));
    }

    private String anytext (UUID uuid) {
        return uuid.toString ().replace ("-", "");
    }

}