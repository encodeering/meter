package de.synyx.metrics.core.aspect;

import java.util.Random;

/**
 * Date: 30.07.2014
 * Time: 13:55
 */
public abstract class MetricAspectTest {

    protected final Random Random = new Random ();

    protected final Throwable exception = new RuntimeException ("fail");

    protected final Object response = new Object ();

    protected final long number = Random.nextInt (100000);

    protected final long metriculate = Random.nextInt (100000);

}
