package de.synyx.meter.core.aspect;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.synyx.meter.core.annotation.Timer;
import de.synyx.meter.core.Meter;
import de.synyx.meter.core.aop.Aspect;
import de.synyx.meter.core.util.Clock;
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
public class MeterAspectTimerTest extends MeterAspectTest {

    @Mock
    private Meter<Duration> meter;

    @Mock
    private Clock clock;

    private Supplier<Meter<Duration>> supplier;

    private final long start = System.nanoTime ();
    private final long end   = (long) (start + 100000 * Math.random ());

    @Before
    public void before () {
        supplier = Suppliers.ofInstance (meter);

        when (clock.tick ()).thenReturn (start, end);
    }

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (Timer.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void test () {
        Timer annotation = annotation (TimeUnit.NANOSECONDS);

        Aspect timer;

        timer = new MeterAspectTimer (annotation, supplier, clock);
        timer.before ();
        timer.after (null, null);

        verify (meter).update (Measure.valueOf (end - start, SI.NANO (SI.SECOND)));
    }

    @Test
    public void testTimeunit () {
        Timer annotation = annotation (TimeUnit.MILLISECONDS);

        Aspect timer;

        timer = new MeterAspectTimer (annotation, supplier, clock);
        timer.before ();
        timer.after (null, null);

        verify (meter).update (Measure.valueOf (TimeUnit.NANOSECONDS.toMillis (end - start), SI.MILLI (SI.SECOND)));
    }

    @Test
    public void testTimeunitConversion () {
        MeterAspectTimer timer;

                    timer = new MeterAspectTimer (mock (Timer.class), supplier, clock);
        assertThat (timer.convert (TimeUnit.NANOSECONDS),  equalTo (SI.NANO  (SI.SECOND)));
        assertThat (timer.convert (TimeUnit.MICROSECONDS), equalTo (SI.MICRO (SI.SECOND)));
        assertThat (timer.convert (TimeUnit.MILLISECONDS), equalTo (SI.MILLI (SI.SECOND)));
        assertThat (timer.convert (TimeUnit.SECONDS),      equalTo ((Unit <Duration>) SI.SECOND));
        assertThat (timer.convert (TimeUnit.MINUTES),      equalTo (NonSI.MINUTE));
        assertThat (timer.convert (TimeUnit.HOURS),        equalTo (NonSI.HOUR));
        assertThat (timer.convert (TimeUnit.DAYS),         equalTo (NonSI.DAY));
    }

    private Timer annotation (TimeUnit unit) {
        Timer annotation;

              annotation = mock (Timer.class);
        when (annotation.unit ()).thenReturn (unit);

        return annotation;
    }

}