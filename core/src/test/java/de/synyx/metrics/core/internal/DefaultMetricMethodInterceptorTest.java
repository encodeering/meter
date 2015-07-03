package de.synyx.metrics.core.internal;

import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.MeterProvider;
import de.synyx.metrics.core.MetricAdvisor;
import de.synyx.metrics.core.MetricAspect;
import de.synyx.metrics.core.MetricNaming;
import de.synyx.metrics.core.annotation.Counter;
import de.synyx.metrics.core.annotation.Histogram;
import de.synyx.metrics.core.annotation.Kind;
import de.synyx.metrics.core.annotation.Meter;
import de.synyx.metrics.core.annotation.Metric;
import de.synyx.metrics.core.annotation.Timer;
import de.synyx.metrics.core.aspect.MetricAspectCounter;
import de.synyx.metrics.core.aspect.MetricAspectHistogram;
import de.synyx.metrics.core.aspect.MetricAspectMeter;
import de.synyx.metrics.core.aspect.MetricAspectTimer;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.measure.Measurable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultMetricMethodInterceptorTest {

    private final Injector injector = mock (Injector.class);

    private final MetricNaming naming = mock (MetricNaming.class);

    private final MeterProvider provider = mock (MeterProvider.class);

    private final MetricAdvisor invoker = mock (MetricAdvisor.class);

    private final DefaultMetricMethodInterceptor interceptor = new DefaultMetricMethodInterceptor (injector, provider, naming, invoker);

    {
        when (injector.create (DefaultClock.class)).thenReturn (new DefaultClock ());

        when (naming.name (anyString ())).thenAnswer (new Answer<Object> () {

            @Override
            public Object answer (InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments ()[0];
            }

        });

        try {
            //noinspection unchecked
            when (invoker.around (any (MethodInvocation.class), any (List.class))).thenAnswer (new Answer<Object> () {

                @SuppressWarnings ("unchecked")
                @Override
                public Object answer (InvocationOnMock invocation) throws Throwable {
                    /* test do not care about the hook execution -> tested by separate units */
                    return ((MethodInvocation) invocation.getArguments ()[0]).proceed ();
                }

            });
        } catch (Throwable throwable) {
            throw new RuntimeException (throwable.getCause ());
        }
    }

    private final String name = UUID.randomUUID ().toString ();


    @SuppressWarnings ("unchecked")
    @Test
    public void testInvokeAll () throws Throwable {
        List<MetricAspect> hooks = invoke (all ());

        assertThat (hooks.size (), equalTo (4));
        assertThat (hooks.get (0), instanceOf (MetricAspectCounter.class));
        assertThat (hooks.get (1), instanceOf (MetricAspectHistogram.class));
        assertThat (hooks.get (2), instanceOf (MetricAspectMeter.class));
        assertThat (hooks.get (3), instanceOf (MetricAspectTimer.class));
    }

    @Test
    public void testInvokeOne () throws Throwable {
        List<MetricAspect> hooks = invoke (one ());

        assertThat (hooks.size (), equalTo (1));
        assertThat (hooks.get (0), instanceOf (MetricAspectCounter.class));
    }

    @Test (expected = IllegalStateException.class)
    public void testInvokeNoAnnotation () throws Throwable {
        MethodInvocation invocation;

              invocation = mock (MethodInvocation.class);
        when (invocation.getMethod ()).thenReturn (nothing ());

        interceptor.invoke (invocation);
    }

    @Test (expected = RuntimeException.class)
    public void testInvokeFail () throws Throwable {
        MethodInvocation invocation;

              invocation = mock (MethodInvocation.class);
        when (invocation.getMethod ()).thenReturn (all ());
        when (invocation.proceed ()).thenThrow (new RuntimeException ("fail"));

        interceptor.invoke (invocation);
    }

    @Test (expected = Throwable.class)
    public void testInvokeFailHarder () throws Throwable {
        MethodInvocation invocation;

              invocation = mock (MethodInvocation.class);
        when (invocation.getMethod ()).thenReturn (all ());
        when (invocation.proceed ()).thenThrow (new Throwable ("fail"));

        interceptor.invoke (invocation);
    }

    @SuppressWarnings ("unchecked")
    private List<MetricAspect> invoke (Method method) throws Throwable {
        MethodInvocation invocation;

              invocation = mock (MethodInvocation.class);
        when (invocation.getMethod ()).thenReturn (method);

        interceptor.invoke (invocation);

        ArgumentCaptor<List<MetricAspect>> captor = ArgumentCaptor.forClass ((Class) List.class);

        verify (invoker).around (Mockito.any (MethodInvocation.class), captor.capture ());

        return captor.getValue ();
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testCounter () throws Exception {
        de.synyx.metrics.core.Meter meter = mock (de.synyx.metrics.core.Meter.class);

        when (provider.counter (name)).thenReturn (meter);

        Counter annotation;

              annotation = mock (Counter.class);
        when (annotation.value ()).thenReturn (name);
        when (annotation.kind ()).thenReturn (Kind.Both);
        when (annotation.operation ()).thenReturn (Counter.Operation.Increment);

        MetricAspect aspect = interceptor.counters ("").apply (annotation);
                     aspect.before ();
                     aspect.after  (null, null);

        assertThat (aspect, instanceOf (MetricAspectCounter.class));

        verify (provider).counter (name);
        verify (meter).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testHistogram () throws Exception {
        de.synyx.metrics.core.Meter meter = mock (de.synyx.metrics.core.Meter.class);

        when (provider.histogram (name)).thenReturn (meter);

        Histogram annotation;

              annotation = mock (Histogram.class);
        when (annotation.value ()).thenReturn (name);
        when (annotation.kind  ()).thenReturn (Kind.Both);

        MetricAspect aspect = interceptor.histograms ("").apply (annotation);
                     aspect.before ();
                     aspect.after  (null, null);

        assertThat (aspect, instanceOf (MetricAspectHistogram.class));

        verify (provider).histogram (name);
        verify (meter).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testMeter () throws Exception {
        de.synyx.metrics.core.Meter meter = mock (de.synyx.metrics.core.Meter.class);

        when (provider.meter (name)).thenReturn (meter);

        Meter annotation;

              annotation = mock (Meter.class);
        when (annotation.value ()).thenReturn (name);
        when (annotation.kind ()).thenReturn (Kind.Both);

        MetricAspect aspect = interceptor.meters ("").apply (annotation);
                     aspect.before ();
                     aspect.after  (null, null);

        assertThat (aspect, instanceOf (MetricAspectMeter.class));

        verify (provider).meter (name);
        verify (meter).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testTimer () throws Exception {
        de.synyx.metrics.core.Meter meter = mock (de.synyx.metrics.core.Meter.class);

        when (provider.timer (name)).thenReturn (meter);

        Timer annotation;

              annotation = mock (Timer.class);
        when (annotation.value ()).thenReturn (name);
        when (annotation.unit ()).thenReturn (TimeUnit.NANOSECONDS);
        when (annotation.clock ()).thenReturn ((Class) DefaultClock.class);

        MetricAspect aspect = interceptor.timers ("").apply (annotation);
                     aspect.before ();
                     aspect.after (null, null);

        assertThat (aspect, instanceOf (MetricAspectTimer.class));

        verify (provider).timer (name);
        verify (meter).update (any (Measurable.class));
    }

    @Test
    public void testName () throws Exception {
        String val = UUID.randomUUID ().toString ();

        assertThat (interceptor.dynname (name,       val).get (), equalTo (             val));
        assertThat (interceptor.dynname (name, "#" + val).get (), equalTo (name + "." + val));
        assertThat (interceptor.dynname (name, "#").get (),       equalTo (name));
    }

    private Method nothing () throws NoSuchMethodException {
        return TestType.class.getMethod ("nothing");
    }

    private Method all () throws NoSuchMethodException {
        return TestType.class.getMethod ("all");
    }

    private Method one () throws NoSuchMethodException {
        return TestType.class.getMethod ("one");
    }

    public final static class TestType {

        public void nothing () {}

        @Metric (
            counters   = @Counter   ("counter"),
            histograms = @Histogram ("histogram"),
            meters     = @Meter     ("meter"),
            timers     = @Timer     ("timer")
        )
        public void all () {}

        @Metric (
            counters   = @Counter   ("counter")
        )
        public void one () {}

    }

}
