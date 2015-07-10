package de.synyx.meter.jersey;

import com.google.common.collect.ImmutableSet;
import de.synyx.meter.core.Injector;
import de.synyx.meter.core.MeterProvider;
import de.synyx.meter.core.Substitution;
import de.synyx.meter.jersey.internal.DefaultJerseyInjector;
import de.synyx.meter.jersey.internal.DefaultJerseySubstitution;
import de.synyx.meter.jersey.internal.DefaultMeterInterceptionService;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class MeterFeatureTest extends JerseyTest {

    @Override
    protected Application configure () {
        Application application = new Application () {

            @Override
            public Set<Class<?>> getClasses () {
                return ImmutableSet.of (MeterFeature.class,
                                        MeterFeatureTestRestlet.class);
            }
        };

        return application;
    }

    @Test
    public void testRegistration () {
        assertThat (target ("/").request ().get (Boolean.class), is (true));
    }

    @Path ("/")
    public final static class MeterFeatureTestRestlet {

        @Inject
        private MeterProvider provider;

        @Inject
        private Injector injector;

        @Inject
        private Substitution substitution;

        @Inject
        private InterceptionService interception;

        @GET
        public boolean check () {
            assertThat (provider,     is (MeterFeatureTestWebContext.Provider));
            assertThat (injector,     instanceOf (DefaultJerseyInjector.class));
            assertThat (substitution, instanceOf (DefaultJerseySubstitution.class));
            assertThat (interception, instanceOf (DefaultMeterInterceptionService.class));

            return true;
        }

    }
}
