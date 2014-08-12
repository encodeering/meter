package de.synyx.metrics.core.aspect;

import com.codahale.metrics.Meter;
import com.google.common.base.Optional;
import de.synyx.metrics.core.MetricAspect;
import de.synyx.metrics.core.Metriculate;
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

public class MetricAspectMeterTest extends MetricAspectTest {

    protected final Meter metric = mock (Meter.class);

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (de.synyx.metrics.core.annotation.Meter.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void testBoth () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Both, number);

        MetricAspect meter;

        meter = new MetricAspectMeter (metric, annotation, Optional.<Metriculate>absent ());
        meter.before ();

        verifyZeroInteractions (metric, annotation);

        meter.after (null, null);
        verify (metric, times (1)).mark (number);

        meter.after (response, null);
        verify (metric, times (2)).mark (number);

        meter.after (null, exception);
        verify (metric, times (3)).mark (number);

        verify (annotation, times (3)).number ();
    }

    @Test
    public void testError () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Error, number);

        MetricAspect meter;

        meter = new MetricAspectMeter (metric, annotation, Optional.<Metriculate>absent ());
        meter.before ();

        verifyZeroInteractions (metric, annotation);

        meter.after (null, null);
        verify (metric, never ()).mark (number);

        meter.after (response, null);
        verify (metric, never ()).mark (number);

        meter.after (null, exception);
        verify (metric, times (1)).mark (number);

        verify (annotation).number ();
    }

    @Test
    public void testSuccess () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Success, number);

        MetricAspect meter;

        meter = new MetricAspectMeter (metric, annotation, Optional.<Metriculate>absent ());
        meter.before ();

        verifyZeroInteractions (metric, annotation);

        meter.after (null, exception);
        verify (metric, never ()).mark (number);

        meter.after (null, null);
        verify (metric, times (1)).mark (number);

        meter.after (response, null);
        verify (metric, times (2)).mark (number);

        verify (annotation, times (2)).number ();
    }

    @Test
    public void testMetriculate () {
        TestMetriculate test = new TestMetriculate (metriculate);

        {
            de.synyx.metrics.core.annotation.Meter annotation;

                  annotation = annotation (Kind.Both, number);
            when (annotation.metriculate ()).thenReturn ((Class) TestMetriculate.class);

            MetricAspect meter;

            meter = new MetricAspectMeter (metric, annotation, Optional.<Metriculate>of (test));
            meter.before ();

            verifyZeroInteractions (metric, annotation);

            meter.after (null, null);
            verify (metric, times (1)).mark (metriculate);

            meter.after (response, null);
            verify (metric, times (2)).mark (metriculate);

            meter.after (null, exception);
            verify (metric, times (3)).mark (metriculate);

            verify (annotation, never ()).number ();
        }
    }

    private de.synyx.metrics.core.annotation.Meter annotation (Kind kind, long number) {
        de.synyx.metrics.core.annotation.Meter annotation;

              annotation = mock (de.synyx.metrics.core.annotation.Meter.class);
        when (annotation.number ()).thenReturn (number);
        when (annotation.kind ()).thenReturn (kind);

        return annotation;
    }

}