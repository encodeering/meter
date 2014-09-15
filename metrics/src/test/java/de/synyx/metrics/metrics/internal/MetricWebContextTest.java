package de.synyx.metrics.metrics.internal;

import com.codahale.metrics.MetricRegistry;
import de.synyx.metrics.core.MeterProvider;
import de.synyx.metrics.metrics.reporter.MetricReportHandlerConsole;
import de.synyx.metrics.metrics.reporter.MetricReportHandlerCsv;
import de.synyx.metrics.metrics.reporter.MetricReportHandlerGanglia;
import de.synyx.metrics.metrics.reporter.MetricReportHandlerGraphite;
import de.synyx.metrics.metrics.reporter.MetricReportHandlerJndi;
import de.synyx.metrics.metrics.reporter.MetricReportHandlerLog;
import de.synyx.metrics.metrics.reporter.MetricReportHandlerNoop;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith (MockitoJUnitRunner.class)
public class MetricWebContextTest {

    @Mock
    private ServletContext context;

    @Mock
    private Context jndi;

    @Mock
    private MetricRegistry registry;

    private MetricWebContext web = new MetricWebContext ();

    @Test
    public void testLifecycle () {
        ArgumentCaptor<MetricRegistry> captor = ArgumentCaptor.forClass (MetricRegistry.class);

        MeterProvider provider;

                    provider = web.initialize (context);
        assertThat (provider, instanceOf (MetricMeterProvider.class));

        verify (context).setAttribute (eq ("com.codahale.metrics.servlet.InstrumentedFilter.registry"), captor.capture ());
        verify (context).getInitParameter ("com.codahale.metrics.reporter");

        assertThat (provider.unwrap (MetricRegistry.class), is (captor.getValue ()));

        web.close ();
    }

    @Test
    public void testDefineWebPresent () {
        when (context.getAttribute ("com.codahale.metrics.servlet.InstrumentedFilter.registry")).thenReturn (registry);

        assertThat (web.define (null, context), is (registry));
    }

    @Test
    public void testDefineWebNotPresent () {
        MetricRegistry defined = web.define (null, context);

        assertThat (defined, is (not (registry)));
        assertThat (defined, notNullValue ());
    }

    @Test
    public void testDefineJndiPresent () throws NamingException {
        when (jndi.lookup ("java:comp/env/metrics/registry")).thenReturn (registry);

        assertThat (web.define (jndi, context), is (registry));
    }

    @Test
    public void testDefineJndiNotPresent () throws NamingException {
        when (jndi.lookup ("java:comp/env/metrics/registry")).thenReturn ("");

        MetricRegistry defined = web.define (jndi, context);

        assertThat (defined, is (not (registry)));
        assertThat (defined, notNullValue ());
    }

    @Test
    public void testDefineJndiException () throws NamingException {
        when (jndi.lookup ("java:comp/env/metrics/registry")).thenThrow (new NamingException ());

        MetricRegistry defined = web.define (jndi, context);

        assertThat (defined, is (not (registry)));
        assertThat (defined, notNullValue ());
    }

    @Test
    public void testDefineConstruct () {
        MetricRegistry defined = web.define (jndi, context);

        assertThat (defined, is (not (registry)));
        assertThat (defined, notNullValue ());
    }

    @Test
    public void testCsv () {
        assertThat (web.csv (), instanceOf (MetricReportHandlerCsv.class));
    }

    @Test
    public void testConsole () {
        assertThat (web.console (), instanceOf (MetricReportHandlerConsole.class));
    }

    @Test
    public void testLogging () {
        assertThat (web.logging (), instanceOf (MetricReportHandlerLog.class));
    }

    @Test
    public void testJndi () {
        assertThat (web.jndi (jndi), instanceOf (MetricReportHandlerJndi.class));
        assertThat (web.jndi (null), instanceOf (MetricReportHandlerNoop.class));
    }

    @Test
    public void testGraphite () {
        assertThat (web.graphite (), instanceOf (MetricReportHandlerGraphite.class));
    }

    @Test
    public void testGanglia () {
        assertThat (web.ganglia (), instanceOf (MetricReportHandlerGanglia.class));
    }

}