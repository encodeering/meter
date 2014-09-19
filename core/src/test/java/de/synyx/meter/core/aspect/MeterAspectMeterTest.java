package de.synyx.meter.core.aspect;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.synyx.meter.core.Measure;
import de.synyx.meter.core.annotation.Meter;
import de.synyx.meter.core.MeterAspect;
import de.synyx.meter.core.annotation.Kind;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.measure.Measurable;
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
public class MeterAspectMeterTest extends MeterAspectTest {

    @Mock
    private de.synyx.meter.core.Meter<Dimensionless> meter;

    private Supplier<de.synyx.meter.core.Meter<Dimensionless>> supplier;

    @Before
    public void before () {
        supplier = Suppliers.ofInstance (meter);
    }

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (Meter.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void testBoth () {
        Meter annotation = annotation (Kind.Both, number);

        MeterAspect aspect;

        aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after (null, null);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testBothRN () {
        Meter annotation = annotation (Kind.Both, number);

        MeterAspect aspect;

        aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after (response, null);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testBothNT () {
        Meter annotation = annotation (Kind.Both, number);

        MeterAspect aspect;

        aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after (null, exception);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorNN () {
        Meter annotation = annotation (Kind.Error, number);

        MeterAspect aspect;

        aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after (null, null);

        verify (meter, never ()).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorRN () {
        Meter annotation = annotation (Kind.Error, number);

        MeterAspect aspect;

        aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after (response, null);

        verify (meter, never ()).update (any (Measurable.class));
    }

    @Test
    public void testErrorNT () {
        Meter annotation = annotation (Kind.Error, number);

        MeterAspect aspect;

        aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after (null, exception);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testSuccessNN () {
        Meter annotation = annotation (Kind.Success, number);

        MeterAspect aspect;

        aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after (null, null);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testSuccessRN () {
        Meter annotation = annotation (Kind.Success, number);

        MeterAspect aspect;

        aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after (response, null);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testSuccessNT () {
        Meter annotation = annotation (Kind.Success, number);

        MeterAspect aspect;

        aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after (null, exception);

        verify (meter, never ()).update (any (Measurable.class));
    }

    @Test
    public void testMetriculateNN () {
        TestMeasure test = new TestMeasure (measure);

        {
            Meter annotation = annotation (Kind.Both, number);

            MeterAspect aspect;

            aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>of (test));
            aspect.before ();
            aspect.after (null, null);

            verify (meter).update (javax.measure.Measure.valueOf (measure, Unit.ONE));
        }
    }

    @Test
    public void testMetriculateRN () {
        TestMeasure test = new TestMeasure (measure);

        {
            Meter annotation = annotation (Kind.Both, number);

            MeterAspect aspect;

            aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>of (test));
            aspect.before ();
            aspect.after (response, null);

            verify (meter).update (javax.measure.Measure.valueOf (measure, Unit.ONE));
        }
    }

    @Test
    public void testMetriculateNT () {
        TestMeasure test = new TestMeasure (measure);

        {
            Meter annotation = annotation (Kind.Both, number);

            MeterAspect aspect;

            aspect = new MeterAspectMeter (annotation, supplier, Optional.<Measure>of (test));
            aspect.before ();
            aspect.after (null, exception);

            verify (meter).update (javax.measure.Measure.valueOf (measure, Unit.ONE));
        }
    }

    private Meter annotation (Kind kind, long number) {
        Meter annotation;

              annotation = mock (Meter.class);
        when (annotation.number ()).thenReturn (number);
        when (annotation.kind ()).thenReturn (kind);

        return annotation;
    }

}