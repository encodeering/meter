package de.synyx.metrics.core.internal;

import de.synyx.metrics.core.MetricAspect;
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

public class DefaultMetricAdvisorTest {

    @Test
    public void testInvoke () throws Throwable {
        Object response = new Object ();

        MetricAspect advice = mock (MetricAspect.class);

        List<MetricAspect> advices = Arrays.asList (advice);

        Callable<Object> invocable;

              invocable = mock (Callable.class);
        when (invocable.call ()).thenReturn (response);

        assertThat (new DefaultMetricAdvisor ().around (invocable, advices), equalTo (response));

        verify (advice).before ();
        verify (advice).after (eq (response), eq ((Throwable) null));
    }

    @Test
    public void testInvokeNoHooks () throws Throwable {
        List<MetricAspect> hooks = new ArrayList<> ();

        Callable<Object> invocable;

                                             invocable = mock (Callable.class);
        new DefaultMetricAdvisor ().around (invocable, hooks);

        verify (invocable).call ();
    }

    @Test (expected = RuntimeException.class)
    public void testInvokeFail () throws Throwable {
        Throwable response = new RuntimeException ("fail");

        MetricAspect adviceA = mock (MetricAspect.class);
        MetricAspect adviceB = mock (MetricAspect.class);

        List<MetricAspect> hooks = Arrays.asList (adviceA, adviceB);

        Callable<Object> invocable;

              invocable = mock (Callable.class);
        when (invocable.call ()).thenThrow (response);

        try {
            new DefaultMetricAdvisor ().around (invocable, hooks);
        } finally {
            verify (adviceA).before ();
            verify (adviceB).before ();

            verify (adviceA).after (eq (null), eq (response));
            verify (adviceB).after (eq (null), eq (response));
        }
    }

    @Test
    public void testInvokeHookFailBefore () throws Throwable {
        Object response = new Object ();

        MetricAspect adviceA = mock (MetricAspect.class);
        MetricAspect adviceB = mock (MetricAspect.class);

        doThrow (new RuntimeException ("fail")).when (adviceA).before ();

        List<MetricAspect> advices = Arrays.asList (adviceA, adviceB);

        Callable<Object> invocable;

              invocable = mock (Callable.class);
        when (invocable.call ()).thenReturn (response);

        assertThat (new DefaultMetricAdvisor ().around (invocable, advices), equalTo (response));

        verify (adviceA).before ();
        verify (adviceB).before ();

        verify (adviceA).after (eq (response), eq ((Throwable) null));
        verify (adviceB).after (eq (response), eq ((Throwable) null));
    }

    @Test
    public void testInvokeHookFailAfter () throws Throwable {
        Object response = new Object ();

        MetricAspect adviceA = mock (MetricAspect.class);
        MetricAspect adviceB = mock (MetricAspect.class);

        doThrow (new RuntimeException ("fail")).when (adviceA).after (eq (response), eq ((Throwable) null));

        List<MetricAspect> advices = Arrays.asList (adviceA, adviceB);

        Callable<Object> invocable;

              invocable = mock (Callable.class);
        when (invocable.call ()).thenReturn (response);

        assertThat (new DefaultMetricAdvisor ().around (invocable, advices), equalTo (response));

        verify (adviceA).before ();
        verify (adviceB).before ();

        verify (adviceA).after (eq (response), eq ((Throwable) null));
        verify (adviceB).after (eq (response), eq ((Throwable) null));
    }

}