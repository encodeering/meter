package de.synyx.meter.core.aspect;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.synyx.meter.core.Measure;
import de.synyx.meter.core.annotation.Counter;
import de.synyx.meter.core.Meter;
import de.synyx.meter.core.aop.Aspect;
import de.synyx.meter.core.annotation.Kind;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
public class MeterAspectCounterTest extends MeterAspectTest {

    @Mock
    private Meter<Dimensionless> meter;

    private Supplier<Meter<Dimensionless>> supplier;

    @Before
    public void before () {
        supplier = Suppliers.ofInstance (meter);
    }

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (Counter.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testBothNN () {
        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Both, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>absent ());
            aspect.before ();
            aspect.after  (null, null);

            switch (operation) {
                case Increment: verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE)); break;
                case Decrement: verify (meter).update (javax.measure.Measure.valueOf (-number, Unit.ONE)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testBothRN () {
        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Both, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>absent ());
            aspect.before ();
            aspect.after  (response, null);

            switch (operation) {
                case Increment: verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE)); break;
                case Decrement: verify (meter).update (javax.measure.Measure.valueOf (-number, Unit.ONE)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testBothNT () {
        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Both, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>absent ());
            aspect.before ();
            aspect.after  (null, exception);

            switch (operation) {
                case Increment: verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE)); break;
                case Decrement: verify (meter).update (javax.measure.Measure.valueOf (-number, Unit.ONE)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorNN () {
        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Error, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>absent ());
            aspect.before ();
            aspect.after  (null, null);

            switch (operation) {
                case Increment: verify (meter, never ()).update (any (Measurable.class)); break;
                case Decrement: verify (meter, never ()).update (any (Measurable.class)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorRN () {
        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Error, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>absent ());
            aspect.before ();
            aspect.after  (response, null);

            switch (operation) {
                case Increment: verify (meter, never ()).update (any (Measurable.class)); break;
                case Decrement: verify (meter, never ()).update (any (Measurable.class)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorNT () {
        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Error, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>absent ());
            aspect.before ();
            aspect.after  (null, exception);

            switch (operation) {
                case Increment: verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE)); break;
                case Decrement: verify (meter).update (javax.measure.Measure.valueOf (-number, Unit.ONE)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testSuccessNN () {
        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Success, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>absent ());
            aspect.before ();
            aspect.after  (null, null);

            switch (operation) {
                case Increment: verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE)); break;
                case Decrement: verify (meter).update (javax.measure.Measure.valueOf (-number, Unit.ONE)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testSuccessRN () {
        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Success, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>absent ());
            aspect.before ();
            aspect.after  (response, null);

            switch (operation) {
                case Increment: verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE)); break;
                case Decrement: verify (meter).update (javax.measure.Measure.valueOf (-number, Unit.ONE)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testSuccessNT () {
        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Success, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>absent ());
            aspect.before ();
            aspect.after  (null, exception);

            switch (operation) {
                case Increment: verify (meter, never ()).update (any (Measurable.class)); break;
                case Decrement: verify (meter, never ()).update (any (Measurable.class)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testMetriculateNN () {
        TestMeasure test = new TestMeasure (measure);

        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Both, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>of (test));
            aspect.before ();
            aspect.after  (null, null);

            switch (operation) {
                case Increment: verify (meter).update (javax.measure.Measure.valueOf ( measure, Unit.ONE)); break;
                case Decrement: verify (meter).update (javax.measure.Measure.valueOf (-measure, Unit.ONE)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testMetriculateRN () {
        TestMeasure test = new TestMeasure (measure);

        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Both, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>of (test));
            aspect.before ();
            aspect.after  (response, null);

            switch (operation) {
                case Increment: verify (meter).update (javax.measure.Measure.valueOf ( measure, Unit.ONE)); break;
                case Decrement: verify (meter).update (javax.measure.Measure.valueOf (-measure, Unit.ONE)); break;
            }

            Mockito.reset (meter);
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testMetriculateNT () {
        TestMeasure test = new TestMeasure (measure);

        for (Counter.Operation operation : Counter.Operation.values ()) {
            Counter annotation = annotation (Kind.Both, operation, number);

            Aspect aspect;

            aspect = new MeterAspectCounter (annotation, supplier, Optional.<Measure>of (test));
            aspect.before ();
            aspect.after  (null, exception);

            switch (operation) {
                case Increment: verify (meter).update (javax.measure.Measure.valueOf ( measure, Unit.ONE)); break;
                case Decrement: verify (meter).update (javax.measure.Measure.valueOf (-measure, Unit.ONE)); break;
            }

            Mockito.reset (meter);
        }
    }

    private Counter annotation (Kind kind, Counter.Operation operation, long number) {
        Counter annotation;

              annotation = mock (Counter.class);
        when (annotation.operation ()).thenReturn (operation);
        when (annotation.number ()).thenReturn (number);
        when (annotation.kind ()).thenReturn (kind);

        return annotation;
    }

}