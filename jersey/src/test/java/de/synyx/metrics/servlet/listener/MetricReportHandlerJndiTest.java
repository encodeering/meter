package de.synyx.metrics.servlet.listener;

import org.junit.Test;
import org.mockito.Mock;

import java.net.URI;
import java.util.UUID;
import javax.naming.Context;
import javax.naming.NamingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MetricReportHandlerJndiTest extends MetricReportTestSupport {

    @Mock
    private Context jndicontext;

    private String endpoint = "java:comp/env/reporter/" + anytext (UUID.randomUUID ());

    @Test
    public void testScheme () throws Exception {
        assertThat (new MetricReportHandlerJndi (jndicontext).scheme (), equalTo ("java"));
    }

    @Test
    public void testReporterUrl () throws Exception {
        MetricReportHandler handler = spy (handler (anyurl.getScheme (), reporter));

        {
            when (jndicontext.lookup (endpoint)).thenReturn (anyurl);

            assertThat (new MetricReportHandlerJndi (jndicontext).select (mediator (handler), registry, URI.create (endpoint)).get (),

                equalTo (reporter)

            );
        }

        verify (handler).reporter (any (MetricReportMediator.class), eq (registry), eq (anyurl));
    }

    @Test
    public void testReporterUrlAsString () throws Exception {
        MetricReportHandler handler = spy (handler (anyurl.getScheme (), reporter));

        {
            when (jndicontext.lookup (endpoint)).thenReturn (anyurl.toString ());

            assertThat (new MetricReportHandlerJndi (jndicontext).select (mediator (handler), registry, URI.create (endpoint)).get (),

                equalTo (reporter)

            );
        }

        verify (handler).reporter (any (MetricReportMediator.class), eq (registry), eq (anyurl));
    }

    @Test
    public void testReporterNotFound () throws Exception {
        MetricReportHandler handler = spy (handler (anyurl.getScheme (), reporter));

        {
            when (jndicontext.lookup (endpoint)).thenReturn (null);

            assertThat (new MetricReportHandlerJndi (jndicontext).select (mediator (handler), registry, URI.create (endpoint)).isPresent (),

                is (false)

            );
        }

        verify (handler, never ()).reporter (any (MetricReportMediator.class), eq (registry), eq (anyurl));
    }

    @Test
    public void testReporterLookupFail () throws NamingException {
        when (jndicontext.lookup (endpoint)).thenThrow (new NamingException ());

        assertThat (new MetricReportHandlerJndi (jndicontext).select (mediator (handler (anyurl.getScheme (), reporter)), registry, URI.create (endpoint)).isPresent (),

            is (false)

        );
    }


}