package de.synyx.metrics.core.aspect;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.synyx.metrics.core.MetricAspect;
import de.synyx.metrics.core.Metriculate;
import de.synyx.metrics.core.annotation.Kind;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith (MockitoJUnitRunner.class)
public class MetricAspectMeterTest extends MetricAspectTest {

    @Mock
    private de.synyx.metrics.core.Meter<Dimensionless> meter;

    private Supplier<de.synyx.metrics.core.Meter<Dimensionless>> supplier;

    @Before
    public void before () {
        supplier = Suppliers.ofInstance (meter);
    }

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (de.synyx.metrics.core.annotation.Meter.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void testBoth () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Both, number);

        MetricAspect aspect;

        aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>absent ());
        aspect.before ();
        aspect.after (null, null);

        verify (meter).update (Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testBothRN () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Both, number);

        MetricAspect aspect;

        aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>absent ());
        aspect.before ();
        aspect.after (response, null);

        verify (meter).update (Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testBothNT () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Both, number);

        MetricAspect aspect;

        aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>absent ());
        aspect.before ();
        aspect.after (null, exception);

        verify (meter).update (Measure.valueOf (number, Unit.ONE));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorNN () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Error, number);

        MetricAspect aspect;

        aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>absent ());
        aspect.before ();
        aspect.after (null, null);

        verify (meter, never ()).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorRN () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Error, number);

        MetricAspect aspect;

        aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>absent ());
        aspect.before ();
        aspect.after (response, null);

        verify (meter, never ()).update (any (Measurable.class));
    }

    @Test
    public void testErrorNT () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Error, number);

        MetricAspect aspect;

        aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>absent ());
        aspect.before ();
        aspect.after (null, exception);

        verify (meter).update (Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testSuccessNN () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Success, number);

        MetricAspect aspect;

        aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>absent ());
        aspect.before ();
        aspect.after (null, null);

        verify (meter).update (Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testSuccessRN () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Success, number);

        MetricAspect aspect;

        aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>absent ());
        aspect.before ();
        aspect.after (response, null);

        verify (meter).update (Measure.valueOf (number, Unit.ONE));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testSuccessNT () {
        de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Success, number);

        MetricAspect aspect;

        aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>absent ());
        aspect.before ();
        aspect.after (null, exception);

        verify (meter, never ()).update (any (Measurable.class));
    }

    @Test
    public void testMetriculateNN () {
        TestMetriculate test = new TestMetriculate (metriculate);

        {
            de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Both, number);

            MetricAspect aspect;

            aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>of (test));
            aspect.before ();
            aspect.after (null, null);

            verify (meter).update (Measure.valueOf (metriculate, Unit.ONE));
        }
    }

    @Test
    public void testMetriculateRN () {
        TestMetriculate test = new TestMetriculate (metriculate);

        {
            de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Both, number);

            MetricAspect aspect;

            aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>of (test));
            aspect.before ();
            aspect.after (response, null);

            verify (meter).update (Measure.valueOf (metriculate, Unit.ONE));
        }
    }

    @Test
    public void testMetriculateNT () {
        TestMetriculate test = new TestMetriculate (metriculate);

        {
            de.synyx.metrics.core.annotation.Meter annotation = annotation (Kind.Both, number);

            MetricAspect aspect;

            aspect = new MetricAspectMeter (annotation, supplier, Optional.<Metriculate>of (test));
            aspect.before ();
            aspect.after (null, exception);

            verify (meter).update (Measure.valueOf (metriculate, Unit.ONE));
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