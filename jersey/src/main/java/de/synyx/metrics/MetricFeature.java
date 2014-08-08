package de.synyx.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import de.synyx.metrics.internal.DefaultJerseySubstitution;
import de.synyx.metrics.internal.DefaultMetricInterceptorService;
import de.synyx.metrics.servlet.listener.MetricListener;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
            bind (DefaultMetricInterceptorService.class).to(InterceptionService.class).in (Singleton.class);
            bind (DefaultJerseySubstitution.class).to (Substitution.class).in (Singleton.class);
        }

    }

    public final static class MetricRegistryFactory implements Factory<MetricRegistry> {

        private final ServletContext session;
        private final Optional<ServletContextListener> listener;

        @Inject
        public MetricRegistryFactory (@Context ServletContext session) {
            this.listener = MetricListener.ifNotCalled ();
            this.session  = session;
        }

        @Override
        public final MetricRegistry provide () {
            listener.transform (new MetricRegistrySessionInit (session));

            return (MetricRegistry) session.getAttribute (MetricListener.AttrRegistryWeb);
        }

        @Override
        public void dispose (MetricRegistry instance) {
            listener.transform (new MetricRegistrySessionDestroy (session));
        }

    }

    final static class MetricRegistrySessionInit implements Function<ServletContextListener, ServletContextListener> {

        private final ServletContext session;

        MetricRegistrySessionInit (ServletContext session) {
            this.session = session;
        }

        @Override
        public final ServletContextListener apply (ServletContextListener listener) {
                   listener.contextInitialized (new ServletContextEvent (session));
            return listener;
        }

    }

    final static class MetricRegistrySessionDestroy implements Function<ServletContextListener, ServletContextListener> {

        private final ServletContext session;

        MetricRegistrySessionDestroy (ServletContext session) {
            this.session = session;
        }

        @Override
        public final ServletContextListener apply (ServletContextListener listener) {
                   listener.contextDestroyed (new ServletContextEvent (session));
            return listener;
        }

    }

}
