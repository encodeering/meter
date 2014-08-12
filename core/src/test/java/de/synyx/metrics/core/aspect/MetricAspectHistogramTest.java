package de.synyx.metrics.core.aspect;

import com.codahale.metrics.Histogram;
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

public class MetricAspectHistogramTest extends MetricAspectTest {

    protected final Histogram metric = mock (Histogram.class);

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (de.synyx.metrics.core.annotation.Histogram.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void testBoth () {
        de.synyx.metrics.core.annotation.Histogram annotation = annotation (Kind.Both, number);

        MetricAspect histogram;

        histogram = new MetricAspectHistogram (metric, annotation, Optional.<Metriculate>absent ());
        histogram.before ();

        verifyZeroInteractions (metric, annotation);

        histogram.after (null, null);
        verify (metric, times (1)).update (number);

        histogram.after (response, null);
        verify (metric, times (2)).update (number);

        histogram.after (null, exception);
        verify (metric, times (3)).update (number);

        verify (annotation, times (3)).number ();
    }

    @Test
    public void testError () {
        de.synyx.metrics.core.annotation.Histogram annotation = annotation (Kind.Error, number);

        MetricAspect histogram;

        histogram = new MetricAspectHistogram (metric, annotation, Optional.<Metriculate>absent ());
        histogram.before ();

        verifyZeroInteractions (metric, annotation);

        histogram.after (null, null);
        verify (metric, never ()).update (number);

        histogram.after (response, null);
        verify (metric, never ()).update (number);

        histogram.after (null, exception);
        verify (metric, times (1)).update (number);

        verify (annotation).number ();
    }

    @Test
    public void testSuccess () {
        de.synyx.metrics.core.annotation.Histogram annotation = annotation (Kind.Success, number);

        MetricAspect histogram;

        histogram = new MetricAspectHistogram (metric, annotation, Optional.<Metriculate>absent ());
        histogram.before ();

        verifyZeroInteractions (metric, annotation);

        histogram.after (null, exception);
        verify (metric, never ()).update (number);

        histogram.after (null, null);
        verify (metric, times (1)).update (number);

        histogram.after (response, null);
        verify (metric, times (2)).update (number);

        verify (annotation, times (2)).number ();
    }

    @Test
    public void testMetriculate () {
        TestMetriculate test = new TestMetriculate (metriculate);

        {
            de.synyx.metrics.core.annotation.Histogram annotation;

                  annotation = annotation (Kind.Both, number);
            when (annotation.metriculate ()).thenReturn ((Class) TestMetriculate.class);

            MetricAspect histogram;

            histogram = new MetricAspectHistogram (metric, annotation, Optional.<Metriculate>of (test));
            histogram.before ();

            verifyZeroInteractions (metric, annotation);

            histogram.after (null, null);
            verify (metric, times (1)).update (metriculate);

            histogram.after (response, null);
            verify (metric, times (2)).update (metriculate);

            histogram.after (null, exception);
            verify (metric, times (3)).update (metriculate);

            verify (annotation, never ()).number ();
        }
    }

    private de.synyx.metrics.core.annotation.Histogram annotation (Kind kind, long number) {
        de.synyx.metrics.core.annotation.Histogram annotation;

              annotation = mock (de.synyx.metrics.core.annotation.Histogram.class);
        when (annotation.number ()).thenReturn (number);
        when (annotation.kind ()).thenReturn (kind);

        return annotation;
    }

}