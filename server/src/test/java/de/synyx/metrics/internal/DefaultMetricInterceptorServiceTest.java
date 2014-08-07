package de.synyx.metrics.internal;

import com.codahale.metrics.MetricRegistry;
import de.synyx.metrics.Substitution;
import de.synyx.metrics.annotation.Metric;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@RunWith (MockitoJUnitRunner.class)
public class DefaultMetricInterceptorServiceTest {

    @Mock
    private ServiceLocator locator;

    @Mock
    private MetricRegistry registry;

    @Mock
    private Substitution substitution;

    @Before
    public void before () {
        when (locator.createAndInitialize (DefaultMetricNaming.class)).thenReturn     (new DefaultMetricNaming     (substitution));
        when (locator.createAndInitialize (DefaultMetricInvocation.class)).thenReturn (new DefaultMetricInvocation ());
    }

    @Test
    public void testFilterAll () throws Exception {
        InterceptionService service;

                    service = new DefaultMetricInterceptorService (locator, registry);
        assertThat (service.getDescriptorFilter (), equalTo (BuilderHelper.allFilter ()));
    }

    @Test
    public void testMethodAOP () throws Exception {
        InterceptionService service;

                    service = new DefaultMetricInterceptorService (locator, registry);
        assertThat (service.getMethodInterceptors (null),                                      nullValue ());
        assertThat (service.getMethodInterceptors (TestClass.class.getMethod ("no")),          nullValue ());
        assertThat (service.getMethodInterceptors (TestClass.class.getMethod ("yes")).get (0), instanceOf (DefaultMetricMethodInterceptor.class));
    }

    @Test
    public void testConstructorAOPUnsupported () throws Exception {
        InterceptionService service;

                    service = new DefaultMetricInterceptorService (locator, registry);
        assertThat (service.getConstructorInterceptors (null),                              nullValue ());
        assertThat (service.getConstructorInterceptors (TestClass.class.getConstructor ()), nullValue ());
    }

    public static class TestClass {

        public TestClass () {}

        @Metric
        public void yes () {}

        public void no  () {}

    }

}