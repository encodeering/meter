package de.synyx.metrics.internal;

import de.synyx.metrics.MetricHook;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultMetricInvocationTest {

    @Test
    public void testInvoke () throws Throwable {
        Object response = new Object ();

        MetricHook hook = mock (MetricHook.class);

        List<MetricHook> hooks = Arrays.asList (hook);

        Callable<Object> invocable;

              invocable = mock (Callable.class);
        when (invocable.call ()).thenReturn (response);

        assertThat (new DefaultMetricInvocation ().invoke (invocable, hooks), equalTo (response));

        verify (hook).before ();
        verify (hook).after (eq (response), eq ((Throwable) null));
    }

    @Test
    public void testInvokeNoHooks () throws Throwable {
        List<MetricHook> hooks = new ArrayList<> ();

        Callable<Object> invocable;

                                               invocable = mock (Callable.class);
        new DefaultMetricInvocation ().invoke (invocable, hooks);

        verify (invocable).call ();
    }

    @Test (expected = RuntimeException.class)
    public void testInvokeFail () throws Throwable {
        Throwable response = new RuntimeException ("fail");

        MetricHook hookA = mock (MetricHook.class);
        MetricHook hookB = mock (MetricHook.class);

        List<MetricHook> hooks = Arrays.asList (hookA, hookB);

        Callable<Object> invocable;

              invocable = mock (Callable.class);
        when (invocable.call ()).thenThrow (response);

        try {
            new DefaultMetricInvocation ().invoke (invocable, hooks);
        } finally {
            verify (hookA).before ();
            verify (hookB).before ();

            verify (hookA).after (eq (null), eq (response));
            verify (hookB).after (eq (null), eq (response));
        }
    }

    @Test
    public void testInvokeHookFailBefore () throws Throwable {
        Object response = new Object ();

        MetricHook hookA = mock (MetricHook.class);
        MetricHook hookB = mock (MetricHook.class);

        doThrow (new RuntimeException ("fail")).when (hookA).before ();

        List<MetricHook> hooks = Arrays.asList (hookA, hookB);

        Callable<Object> invocable;

              invocable = mock (Callable.class);
        when (invocable.call ()).thenReturn (response);

        assertThat (new DefaultMetricInvocation ().invoke (invocable, hooks), equalTo (response));

        verify (hookA).before ();
        verify (hookB).before ();

        verify (hookA).after (eq (response), eq ((Throwable) null));
        verify (hookB).after (eq (response), eq ((Throwable) null));
    }

    @Test
    public void testInvokeHookFailAfter () throws Throwable {
        Object response = new Object ();

        MetricHook hookA = mock (MetricHook.class);
        MetricHook hookB = mock (MetricHook.class);

        doThrow (new RuntimeException ("fail")).when (hookA).after (eq (response), eq ((Throwable) null));

        List<MetricHook> hooks = Arrays.asList (hookA, hookB);

        Callable<Object> invocable;

              invocable = mock (Callable.class);
        when (invocable.call ()).thenReturn (response);

        assertThat (new DefaultMetricInvocation ().invoke (invocable, hooks), equalTo (response));

        verify (hookA).before ();
        verify (hookB).before ();

        verify (hookA).after (eq (response), eq ((Throwable) null));
        verify (hookB).after (eq (response), eq ((Throwable) null));
    }

}