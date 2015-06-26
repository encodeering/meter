package de.synyx.meter.jersey.internal;

import de.synyx.meter.core.Injector;
import de.synyx.meter.core.MeterProvider;
import de.synyx.meter.core.Substitution;
import de.synyx.meter.core.annotation.Metric;
import de.synyx.meter.core.service.DefaultSubstitution;
import de.synyx.meter.core.service.aop.DefaultAdvisor;
import de.synyx.meter.core.service.aop.DefaultMeterInterceptor;
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
public class DefaultMeterInterceptionServiceTest {

    @Mock
    private Injector injector;

    @Mock
    private MeterProvider provider;

    @Mock
    private Substitution substitution;

    @Before
    public void before () {
        when (injector.create (DefaultSubstitution.class)).thenReturn (new DefaultSubstitution (substitution));
        when (injector.create (DefaultAdvisor.class)).thenReturn (new DefaultAdvisor ());
    }

    @Test
    public void testFilterAll () throws Exception {
        InterceptionService service;

                    service = new DefaultMeterInterceptionService (injector, provider);
        assertThat (service.getDescriptorFilter (), equalTo (BuilderHelper.allFilter ()));
    }

    @Test
    public void testMethodAOP () throws Exception {
        InterceptionService service;

                    service = new DefaultMeterInterceptionService (injector, provider);
        assertThat (service.getMethodInterceptors (null), nullValue ());
        assertThat (service.getMethodInterceptors (TestClass.class.getMethod ("no")), nullValue ());
        assertThat (service.getMethodInterceptors (TestClass.class.getMethod ("yes")).get (0), instanceOf (DefaultMeterInterceptor.class));
    }

    @Test
    public void testConstructorAOPUnsupported () throws Exception {
        InterceptionService service;

                    service = new DefaultMeterInterceptionService (injector, provider);
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
