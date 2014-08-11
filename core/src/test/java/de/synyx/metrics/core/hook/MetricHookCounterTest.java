package de.synyx.metrics.core.hook;

import com.codahale.metrics.Counter;
import de.synyx.metrics.core.MetricHook;
import de.synyx.metrics.core.annotation.Kind;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MetricHookCounterTest extends MetricHookTest {

    private final Counter metric = mock (Counter.class);

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (de.synyx.metrics.core.annotation.Counter.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void testBoth () {
        for (de.synyx.metrics.core.annotation.Counter.Operation operation : de.synyx.metrics.core.annotation.Counter.Operation.values ()) {
            de.synyx.metrics.core.annotation.Counter annotation = annotation (Kind.Both, operation, number);

            MetricHook counter;

            counter = new MetricHookCounter (injector, metric, annotation);
            counter.before ();

            verifyZeroInteractions (metric, annotation);

            counter.after (null, null);

            switch (operation) {
                case Increment: verify (metric, times (1)).inc (number); break;
                case Decrement: verify (metric, times (1)).dec (number); break;
            }

            counter.after (response, null);

            switch (operation) {
                case Increment: verify (metric, times (2)).inc (number); break;
                case Decrement: verify (metric, times (2)).dec (number); break;
            }

            counter.after (null, exception);

            switch (operation) {
                case Increment: verify (metric, times (3)).inc (number); break;
                case Decrement: verify (metric, times (3)).dec (number); break;
            }

            verify (annotation, times (3)).number ();
        }
    }

    @Test
    public void testError () {
        for (de.synyx.metrics.core.annotation.Counter.Operation operation : de.synyx.metrics.core.annotation.Counter.Operation.values ()) {
            de.synyx.metrics.core.annotation.Counter annotation = annotation (Kind.Error, operation, number);

            MetricHook counter;

            counter = new MetricHookCounter (injector, metric, annotation);
            counter.before ();

            verifyZeroInteractions (metric, annotation);

            counter.after (null, null);

            switch (operation) {
                case Increment: verify (metric, never ()).inc (number); break;
                case Decrement: verify (metric, never ()).dec (number); break;
            }

            counter.after (response, null);

            switch (operation) {
                case Increment: verify (metric, never ()).inc (number); break;
                case Decrement: verify (metric, never ()).dec (number); break;
            }

            counter.after (null, exception);

            switch (operation) {
                case Increment: verify (metric, times (1)).inc (number); break;
                case Decrement: verify (metric, times (1)).dec (number); break;
            }

            verify (annotation).number ();
        }
    }

    @Test
    public void testSuccess () {
        for (de.synyx.metrics.core.annotation.Counter.Operation operation : de.synyx.metrics.core.annotation.Counter.Operation.values ()) {
            de.synyx.metrics.core.annotation.Counter annotation = annotation (Kind.Success, operation, number);

            MetricHook counter;

            counter = new MetricHookCounter (injector, metric, annotation);
            counter.before ();

            verifyZeroInteractions (metric, annotation);

            counter.after (null, exception);

            switch (operation) {
                case Increment: verify (metric, never ()).inc (number); break;
                case Decrement: verify (metric, never ()).dec (number); break;
            }

            counter.after (null, null);

            switch (operation) {
                case Increment: verify (metric, times (1)).inc (number); break;
                case Decrement: verify (metric, times (1)).dec (number); break;
            }

            counter.after (response, null);

            switch (operation) {
                case Increment: verify (metric, times (2)).inc (number); break;
                case Decrement: verify (metric, times (2)).dec (number); break;
            }

            verify (annotation, times (2)).number ();
        }
    }

    @Test
    public void testMetriculate () {
        TestMetriculate test = new TestMetriculate (metriculate);

        when (injector.create (TestMetriculate.class)).thenReturn (test);

        for (de.synyx.metrics.core.annotation.Counter.Operation operation : de.synyx.metrics.core.annotation.Counter.Operation.values ()) {
            de.synyx.metrics.core.annotation.Counter annotation;

                  annotation = annotation (Kind.Both, operation, number);
            when (annotation.metriculate ()).thenReturn ((Class) TestMetriculate.class);

            MetricHook counter;

            counter = new MetricHookCounter (injector, metric, annotation);
            counter.before ();

            verifyZeroInteractions (metric, annotation);

            counter.after (null, null);

            switch (operation) {
                case Increment: verify (metric, times (1)).inc (metriculate); break;
                case Decrement: verify (metric, times (1)).dec (metriculate); break;
            }

            counter.after (response, null);

            switch (operation) {
                case Increment: verify (metric, times (2)).inc (metriculate); break;
                case Decrement: verify (metric, times (2)).dec (metriculate); break;
            }

            counter.after (null, exception);

            switch (operation) {
                case Increment: verify (metric, times (3)).inc (metriculate); break;
                case Decrement: verify (metric, times (3)).dec (metriculate); break;
            }

            verify (annotation, never ()).number ();
        }

                         /* operations * after (times(3)) */

        verify (injector, times (2 * 3)).inject (test);
    }

    private de.synyx.metrics.core.annotation.Counter annotation (Kind kind, de.synyx.metrics.core.annotation.Counter.Operation operation, long number) {
        de.synyx.metrics.core.annotation.Counter annotation;

              annotation = mock (de.synyx.metrics.core.annotation.Counter.class);
        when (annotation.operation ()).thenReturn (operation);
        when (annotation.number ()).thenReturn (number);
        when (annotation.kind ()).thenReturn (kind);

        return annotation;
    }

}