package de.synyx.metrics.hook;

import org.glassfish.hk2.api.ServiceLocator;

import java.util.Random;

import static org.mockito.Mockito.mock;

/**
 * Date: 30.07.2014
 * Time: 13:55
 */
public abstract class MetricHookTest {

    protected final Random Random = new Random ();

    protected final ServiceLocator locator = mock (ServiceLocator.class);

    protected final Throwable exception = new RuntimeException ("fail");

    protected final Object response = new Object ();

    protected final long number = Random.nextInt (100000);

    protected final long metriculate = Random.nextInt (100000);

}
