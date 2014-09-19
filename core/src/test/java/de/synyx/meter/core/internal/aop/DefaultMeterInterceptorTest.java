package de.synyx.meter.core.internal.aop;

import de.synyx.meter.core.Injector;
import de.synyx.meter.core.MeterNaming;
import de.synyx.meter.core.MeterProvider;
import de.synyx.meter.core.annotation.Counter;
import de.synyx.meter.core.annotation.Histogram;
import de.synyx.meter.core.annotation.Kind;
import de.synyx.meter.core.annotation.Meter;
import de.synyx.meter.core.annotation.Metric;
import de.synyx.meter.core.annotation.Timer;
import de.synyx.meter.core.aop.Advisor;
import de.synyx.meter.core.aop.Aspect;
import de.synyx.meter.core.aspect.MeterAspectCounter;
import de.synyx.meter.core.aspect.MeterAspectHistogram;
import de.synyx.meter.core.aspect.MeterAspectMeter;
import de.synyx.meter.core.aspect.MeterAspectTimer;
import de.synyx.meter.core.internal.DefaultClock;
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

public class DefaultMeterInterceptorTest {

    private final Injector injector = mock (Injector.class);

    private final MeterNaming naming = mock (MeterNaming.class);

    private final MeterProvider provider = mock (MeterProvider.class);

    private final Advisor invoker = mock (Advisor.class);

    private final DefaultMeterInterceptor interceptor = new DefaultMeterInterceptor (injector, provider, naming, invoker);

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
        List<Aspect> hooks = invoke (all ());

        assertThat (hooks.size (), equalTo (4));
        assertThat (hooks.get (0), instanceOf (MeterAspectCounter.class));
        assertThat (hooks.get (1), instanceOf (MeterAspectHistogram.class));
        assertThat (hooks.get (2), instanceOf (MeterAspectMeter.class));
        assertThat (hooks.get (3), instanceOf (MeterAspectTimer.class));
    }

    @Test
    public void testInvokeOne () throws Throwable {
        List<Aspect> hooks = invoke (one ());

        assertThat (hooks.size (), equalTo (1));
        assertThat (hooks.get (0), instanceOf (MeterAspectCounter.class));
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
    private List<Aspect> invoke (Method method) throws Throwable {
        MethodInvocation invocation;

              invocation = mock (MethodInvocation.class);
        when (invocation.getMethod ()).thenReturn (method);

        interceptor.invoke (invocation);

        ArgumentCaptor<List<Aspect>> captor = ArgumentCaptor.forClass ((Class) List.class);

        verify (invoker).around (Mockito.any (MethodInvocation.class), captor.capture ());

        return captor.getValue ();
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testCounter () throws Exception {
        de.synyx.meter.core.Meter meter = mock (de.synyx.meter.core.Meter.class);

        when (provider.counter (name)).thenReturn (meter);

        Counter annotation;

              annotation = mock (Counter.class);
        when (annotation.value ()).thenReturn (name);
        when (annotation.kind ()).thenReturn (Kind.Both);
        when (annotation.operation ()).thenReturn (Counter.Operation.Increment);

        Aspect aspect = interceptor.counters ("").apply (annotation);
                     aspect.before ();
                     aspect.after  (null, null);

        assertThat (aspect, instanceOf (MeterAspectCounter.class));

        verify (provider).counter (name);
        verify (meter).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testHistogram () throws Exception {
        de.synyx.meter.core.Meter meter = mock (de.synyx.meter.core.Meter.class);

        when (provider.histogram (name)).thenReturn (meter);

        Histogram annotation;

              annotation = mock (Histogram.class);
        when (annotation.value ()).thenReturn (name);
        when (annotation.kind  ()).thenReturn (Kind.Both);

        Aspect aspect = interceptor.histograms ("").apply (annotation);
                     aspect.before ();
                     aspect.after  (null, null);

        assertThat (aspect, instanceOf (MeterAspectHistogram.class));

        verify (provider).histogram (name);
        verify (meter).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testMeter () throws Exception {
        de.synyx.meter.core.Meter meter = mock (de.synyx.meter.core.Meter.class);

        when (provider.meter (name)).thenReturn (meter);

        Meter annotation;

              annotation = mock (Meter.class);
        when (annotation.value ()).thenReturn (name);
        when (annotation.kind ()).thenReturn (Kind.Both);

        Aspect aspect = interceptor.meters ("").apply (annotation);
                     aspect.before ();
                     aspect.after  (null, null);

        assertThat (aspect, instanceOf (MeterAspectMeter.class));

        verify (provider).meter (name);
        verify (meter).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testTimer () throws Exception {
        de.synyx.meter.core.Meter meter = mock (de.synyx.meter.core.Meter.class);

        when (provider.timer (name)).thenReturn (meter);

        Timer annotation;

              annotation = mock (Timer.class);
        when (annotation.value ()).thenReturn (name);
        when (annotation.unit ()).thenReturn (TimeUnit.NANOSECONDS);
        when (annotation.clock ()).thenReturn ((Class) DefaultClock.class);

        Aspect aspect = interceptor.timers ("").apply (annotation);
                     aspect.before ();
                     aspect.after (null, null);

        assertThat (aspect, instanceOf (MeterAspectTimer.class));

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
