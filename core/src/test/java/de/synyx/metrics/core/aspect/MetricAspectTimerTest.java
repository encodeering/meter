package de.synyx.metrics.core.aspect;

import com.codahale.metrics.Clock;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.synyx.metrics.core.Meter;
import de.synyx.metrics.core.MetricAspect;
import de.synyx.metrics.core.annotation.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith (MockitoJUnitRunner.class)
public class MetricAspectTimerTest extends MetricAspectTest {

    @Mock
    private de.synyx.metrics.core.Meter<Duration> meter;

    @Mock
    private Clock clock;

    private Supplier<Meter<Duration>> supplier;

    private final long                 start = System.nanoTime ();
    private final long end   = (long) (start + 100000 * Math.random ());

    @Before
    public void before () {
        supplier = Suppliers.ofInstance (meter);

        when (clock.getTick ()).thenReturn (start, end);
    }

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (de.synyx.metrics.core.annotation.Timer.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void test () {
        de.synyx.metrics.core.annotation.Timer annotation = annotation (TimeUnit.NANOSECONDS);

        MetricAspect timer;

        timer = new MetricAspectTimer (annotation, supplier, clock);
        timer.before ();
        timer.after  (null, null);

        verify (meter).update (Measure.valueOf (end - start, SI.NANO (SI.SECOND)));
    }

    @Test
    public void testTimeunit () {
        de.synyx.metrics.core.annotation.Timer annotation = annotation (TimeUnit.MILLISECONDS);

        MetricAspect timer;

        timer = new MetricAspectTimer (annotation, supplier, clock);
        timer.before ();
        timer.after (null, null);

        verify (meter).update (Measure.valueOf (TimeUnit.NANOSECONDS.toMillis (end - start), SI.MILLI (SI.SECOND)));
    }

    @Test
    public void testTimeunitConversion () {
        MetricAspectTimer timer;

                    timer = new MetricAspectTimer (mock (Timer.class), supplier, clock);
        assertThat (timer.convert (TimeUnit.NANOSECONDS),  equalTo (SI.NANO  (SI.SECOND)));
        assertThat (timer.convert (TimeUnit.MICROSECONDS), equalTo (SI.MICRO (SI.SECOND)));
        assertThat (timer.convert (TimeUnit.MILLISECONDS), equalTo (SI.MILLI (SI.SECOND)));
        assertThat (timer.convert (TimeUnit.SECONDS),      equalTo ((Unit <Duration>) SI.SECOND));
        assertThat (timer.convert (TimeUnit.MINUTES),      equalTo (NonSI.MINUTE));
        assertThat (timer.convert (TimeUnit.HOURS),        equalTo (NonSI.HOUR));
        assertThat (timer.convert (TimeUnit.DAYS),         equalTo (NonSI.DAY));
    }

    private de.synyx.metrics.core.annotation.Timer annotation (TimeUnit unit) {
        de.synyx.metrics.core.annotation.Timer annotation;

              annotation = mock (de.synyx.metrics.core.annotation.Timer.class);
        when (annotation.unit ()).thenReturn (unit);

        return annotation;
    }

}