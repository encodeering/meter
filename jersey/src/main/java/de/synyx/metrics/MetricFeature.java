package de.synyx.metrics;

import com.codahale.metrics.MetricRegistry;
import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.Substitution;
import de.synyx.metrics.internal.DefaultJerseyInjector;
import de.synyx.metrics.internal.DefaultJerseySubstitution;
import de.synyx.metrics.internal.DefaultMetricInterceptorService;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Date: 01.08.2014
 * Time: 11:40
 */
public final class MetricFeature implements Feature {

    @Override
    public final boolean configure (FeatureContext context) {
        context.register (new MetricFeatureBinder ());

        return true;
    }

    final static class MetricFeatureBinder extends AbstractBinder {

        @Override
        protected final void configure () {
            bindFactory (MetricFeature.MetricRegistryFactory.class).to (MetricRegistry.class).in (Singleton.class);
            bind (DefaultJerseyInjector.class).to (Injector.class).in (Singleton.class);
            bind (DefaultJerseySubstitution.class).to (Substitution.class).in (Singleton.class);
            bind (DefaultMetricInterceptorService.class).to (InterceptionService.class).in (Singleton.class);
        }

    }

    public final static class MetricRegistryFactory implements Factory<MetricRegistry> {

        private final MetricSession session;

        @Inject
        public MetricRegistryFactory (@Context ServletContext context) {
            this.session = new MetricSession (context);
        }

        @Override
        public final MetricRegistry provide () {
            return session.initialize ();
        }

        @Override
        public void dispose (MetricRegistry instance) {
            session.close ();
        }

    }

}
