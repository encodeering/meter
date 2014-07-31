package de.synyx.metrics.hook;

import com.codahale.metrics.Timer;
import de.synyx.metrics.MetricHook;
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

public class MetricHookTimerTest extends MetricHookTest {

    protected final Timer metric = mock (Timer.class);

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (de.synyx.metrics.annotation.Timer.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void test () throws InterruptedException {
        de.synyx.metrics.annotation.Timer annotation = annotation (TimeUnit.NANOSECONDS);

        MetricHook timer;

        timer = new MetricHookTimer (locator, metric, annotation);
        timer.before ();

        Thread.sleep (Random.nextInt (1000));

        timer.after (null, null);

        ArgumentCaptor<Long> duration = ArgumentCaptor.forClass (Long.class);

        verify (metric).update (duration.capture (), eq (TimeUnit.NANOSECONDS));

        assertThat (duration.getValue (), greaterThanOrEqualTo (0L));
    }

    @Test
    public void testTimeunit () throws InterruptedException {
        de.synyx.metrics.annotation.Timer annotation = annotation (TimeUnit.MILLISECONDS);

        MetricHook timer;

        timer = new MetricHookTimer (locator, metric, annotation);
        timer.before ();

        Thread.sleep (Random.nextInt (1000));

        timer.after (null, null);

        ArgumentCaptor<Long> duration = ArgumentCaptor.forClass (Long.class);

        verify (metric).update (duration.capture (), eq (TimeUnit.MILLISECONDS));

        assertThat (duration.getValue (), greaterThanOrEqualTo (0L));
    }

    private de.synyx.metrics.annotation.Timer annotation (TimeUnit unit) {
        de.synyx.metrics.annotation.Timer annotation;

              annotation = mock (de.synyx.metrics.annotation.Timer.class);
        when (annotation.unit ()).thenReturn (unit);

        return annotation;
    }

}