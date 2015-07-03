package de.synyx.metrics;

import com.google.common.collect.Iterators;
import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.MeterProvider;
import de.synyx.metrics.core.Substitution;
import de.synyx.metrics.core.web.WebContext;
import de.synyx.metrics.internal.DefaultJerseyInjector;
import de.synyx.metrics.internal.DefaultJerseySubstitution;
import de.synyx.metrics.internal.DefaultMetricInterceptorService;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
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
            bindFactory (DefaultMeterProviderFactory.class).to (MeterProvider.class).in (Singleton.class);
            bind (DefaultJerseyInjector.class).to (Injector.class).in (Singleton.class);
            bind (DefaultJerseySubstitution.class).to (Substitution.class).in (Singleton.class);
            bind (DefaultMetricInterceptorService.class).to (InterceptionService.class).in (Singleton.class);
        }

    }

    public final static class DefaultMeterProviderFactory implements Factory<MeterProvider> {

        private final WebContext web;

        private final ServletContext servlet;

        @Inject
        public DefaultMeterProviderFactory (@Context ServletContext servlet) {
            /* extension: create a compound web/meter provider using all providers or choose the correct one using environment facilities */

            this.web     = Iterators.get (ServiceLoader.load (WebContext.class).iterator (), 0);
            this.servlet = servlet;
        }

        @Override
        public final MeterProvider provide () {
            return web.initialize (servlet);
        }

        @Override
        public final void dispose (MeterProvider provider) {
            try {
                web.close ();
            } catch (Exception e) {
                LoggerFactory.getLogger (getClass ()).warn (e.getMessage ());
            }
        }

    }

}
