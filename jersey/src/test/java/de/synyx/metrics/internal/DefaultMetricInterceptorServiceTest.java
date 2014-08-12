package de.synyx.metrics.internal;

import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.MeterProvider;
import de.synyx.metrics.core.Substitution;
import de.synyx.metrics.core.annotation.Metric;
import de.synyx.metrics.core.internal.DefaultMetricAdvisor;
import de.synyx.metrics.core.internal.DefaultMetricMethodInterceptor;
import de.synyx.metrics.core.internal.DefaultMetricNaming;
import org.glassfish.hk2.api.InterceptionService;
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
    private Injector injector;

    @Mock
    private MeterProvider provider;

    @Mock
    private Substitution substitution;

    @Before
    public void before () {
        when (injector.create (DefaultMetricNaming.class)).thenReturn (new DefaultMetricNaming (substitution));
        when (injector.create (DefaultMetricAdvisor.class)).thenReturn (new DefaultMetricAdvisor ());
    }

    @Test
    public void testFilterAll () throws Exception {
        InterceptionService service;

                    service = new DefaultMetricInterceptorService (injector, provider);
        assertThat (service.getDescriptorFilter (), equalTo (BuilderHelper.allFilter ()));
    }

    @Test
    public void testMethodAOP () throws Exception {
        InterceptionService service;

                    service = new DefaultMetricInterceptorService (injector, provider);
        assertThat (service.getMethodInterceptors (null), nullValue ());
        assertThat (service.getMethodInterceptors (TestClass.class.getMethod ("no")), nullValue ());
        assertThat (service.getMethodInterceptors (TestClass.class.getMethod ("yes")).get (0), instanceOf (DefaultMetricMethodInterceptor.class));
    }

    @Test
    public void testConstructorAOPUnsupported () throws Exception {
        InterceptionService service;

                    service = new DefaultMetricInterceptorService (injector, provider);
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
