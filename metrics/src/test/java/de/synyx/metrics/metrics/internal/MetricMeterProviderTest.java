package de.synyx.metrics.metrics.internal;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import de.synyx.metrics.core.Meter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith (MockitoJUnitRunner.class)
public class MetricMeterProviderTest {

    private final Random Random = new Random ();

    private final long measurement = Random.nextInt (100);

    @Mock
    private MetricRegistry registry;

    @Test
    public void testCounter () {
        Counter counter = mock (Counter.class);

        String name;

                                name = anytext ();
        when (registry.counter (name)).thenReturn (counter);

        Meter<Dimensionless> service;

                    service = new MetricMeterProvider (registry).counter (name);
        assertThat (service.update (Measure.valueOf (measurement, Unit.ONE)), is (service));

        verify (counter).inc (measurement);
    }

    @Test
    public void testHistogram () {
        Histogram histogram = mock (Histogram.class);

        String name;

                                  name = anytext ();
        when (registry.histogram (name)).thenReturn (histogram);

        Meter<Dimensionless> service;

                    service = new MetricMeterProvider (registry).histogram (name);
        assertThat (service.update (Measure.valueOf (measurement, Unit.ONE)), is (service));

        verify (histogram).update (measurement);
    }

    @Test
    public void testMeter () {
        com.codahale.metrics.Meter meter = mock (com.codahale.metrics.Meter.class);

        String name;

                              name = anytext ();
        when (registry.meter (name)).thenReturn (meter);

        Meter<Dimensionless> service;

                    service = new MetricMeterProvider (registry).meter (name);
        assertThat (service.update (Measure.valueOf (measurement, Unit.ONE)), is (service));

        verify (meter).mark (measurement);
    }

    @Test
    public void testTimer () {
        Timer timer = mock (Timer.class);

        String name;

                              name = anytext ();
        when (registry.timer (name)).thenReturn (timer);

        Meter<Duration> service;

                    service = new MetricMeterProvider (registry).timer (name);
        assertThat (service.update (Measure.valueOf (measurement, SI.NANO (SI.SECOND))), is (service));

        verify (timer).update (measurement, TimeUnit.NANOSECONDS);
    }

    @Test
    public void testUnwrap () {
        MetricMeterProvider provider = new MetricMeterProvider (registry);

        assertThat (provider.unwrap (MetricRegistry.class), is (registry));
        assertThat (provider.unwrap (String.class), nullValue ());
    }

    private String anytext () {
        return UUID.randomUUID ().toString ();
    }

}