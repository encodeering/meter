package de.synyx.metrics.core.aspect;

import com.codahale.metrics.Timer;
import de.synyx.metrics.core.MetricAspect;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MetricAspectTimerTest extends MetricAspectTest {

    protected final Timer metric = mock (Timer.class);

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (de.synyx.metrics.core.annotation.Timer.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void test () throws InterruptedException {
        de.synyx.metrics.core.annotation.Timer annotation = annotation (TimeUnit.NANOSECONDS);

        MetricAspect timer;

        timer = new MetricAspectTimer (metric, annotation);
        timer.before ();

        Thread.sleep (Random.nextInt (1000));

        timer.after (null, null);

        ArgumentCaptor<Long> duration = ArgumentCaptor.forClass (Long.class);

        verify (metric).update (duration.capture (), eq (TimeUnit.NANOSECONDS));

        assertThat (duration.getValue (), greaterThanOrEqualTo (0L));
    }

    @Test
    public void testTimeunit () throws InterruptedException {
        de.synyx.metrics.core.annotation.Timer annotation = annotation (TimeUnit.MILLISECONDS);

        MetricAspect timer;

        timer = new MetricAspectTimer (metric, annotation);
        timer.before ();

        Thread.sleep (Random.nextInt (1000));

        timer.after (null, null);

        ArgumentCaptor<Long> duration = ArgumentCaptor.forClass (Long.class);

        verify (metric).update (duration.capture (), eq (TimeUnit.MILLISECONDS));

        assertThat (duration.getValue (), greaterThanOrEqualTo (0L));
    }

    private de.synyx.metrics.core.annotation.Timer annotation (TimeUnit unit) {
        de.synyx.metrics.core.annotation.Timer annotation;

              annotation = mock (de.synyx.metrics.core.annotation.Timer.class);
        when (annotation.unit ()).thenReturn (unit);

        return annotation;
    }

}