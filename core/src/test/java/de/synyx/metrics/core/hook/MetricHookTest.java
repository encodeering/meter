package de.synyx.metrics.core.hook;

import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.Metriculate;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Random;

import static org.mockito.Mockito.when;

/**
 * Date: 30.07.2014
 * Time: 13:55
 */
public abstract class MetricHookTest {

    protected final Random Random = new Random ();

    protected final Injector injector = Mockito.mock (Injector.class);

    protected final Throwable exception = new RuntimeException ("fail");

    protected final Object response = new Object ();

    protected final long number = Random.nextInt (100000);

    protected final long metriculate = Random.nextInt (100000);

    @Before
    public void before () {
        when (injector.inject (Mockito.<Metriculate>any())).thenAnswer (new Answer<Object> () {

            @Override
            public Object answer (InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments ()[0];
            }

        });
    }

}
