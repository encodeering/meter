package de.synyx.meter.core.internal;

import de.synyx.meter.core.MeterAspect;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultMeterAdvisorTest {

    @Test
    public void testInvoke () throws Throwable {
        Object response = new Object ();

        MeterAspect advice = mock (MeterAspect.class);

        List<MeterAspect> advices = Arrays.asList (advice);

        MethodInvocation invocable;

              invocable = mock (MethodInvocation.class);
        when (invocable.proceed ()).thenReturn (response);

        assertThat (new DefaultMeterAdvisor ().around (invocable, advices), equalTo (response));

        verify (advice).before ();
        verify (advice).after (eq (response), eq ((Throwable) null));
    }

    @Test
    public void testInvokeNoHooks () throws Throwable {
        List<MeterAspect> hooks = new ArrayList<> ();

        MethodInvocation invocable;

                                            invocable = mock (MethodInvocation.class);
        new DefaultMeterAdvisor ().around (invocable, hooks);

        verify (invocable).proceed ();
    }

    @Test (expected = RuntimeException.class)
    public void testInvokeFail () throws Throwable {
        Throwable response = new RuntimeException ("fail");

        MeterAspect adviceA = mock (MeterAspect.class);
        MeterAspect adviceB = mock (MeterAspect.class);

        List<MeterAspect> hooks = Arrays.asList (adviceA, adviceB);

        MethodInvocation invocable;

              invocable = mock (MethodInvocation.class);
        when (invocable.proceed ()).thenThrow (response);

        try {
            new DefaultMeterAdvisor ().around (invocable, hooks);
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

        MeterAspect adviceA = mock (MeterAspect.class);
        MeterAspect adviceB = mock (MeterAspect.class);

        doThrow (new RuntimeException ("fail")).when (adviceA).before ();

        List<MeterAspect> advices = Arrays.asList (adviceA, adviceB);

        MethodInvocation invocable;

              invocable = mock (MethodInvocation.class);
        when (invocable.proceed ()).thenReturn (response);

        assertThat (new DefaultMeterAdvisor ().around (invocable, advices), equalTo (response));

        verify (adviceA).before ();
        verify (adviceB).before ();

        verify (adviceA).after (eq (response), eq ((Throwable) null));
        verify (adviceB).after (eq (response), eq ((Throwable) null));
    }

    @Test
    public void testInvokeHookFailAfter () throws Throwable {
        Object response = new Object ();

        MeterAspect adviceA = mock (MeterAspect.class);
        MeterAspect adviceB = mock (MeterAspect.class);

        doThrow (new RuntimeException ("fail")).when (adviceA).after (eq (response), eq ((Throwable) null));

        List<MeterAspect> advices = Arrays.asList (adviceA, adviceB);

        MethodInvocation invocable;

              invocable = mock (MethodInvocation.class);
        when (invocable.proceed ()).thenReturn (response);

        assertThat (new DefaultMeterAdvisor ().around (invocable, advices), equalTo (response));

        verify (adviceA).before ();
        verify (adviceB).before ();

        verify (adviceA).after (eq (response), eq ((Throwable) null));
        verify (adviceB).after (eq (response), eq ((Throwable) null));
    }

}