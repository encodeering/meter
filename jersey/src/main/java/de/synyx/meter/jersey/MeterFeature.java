package de.synyx.meter.jersey;

import com.google.common.collect.Iterators;
import de.synyx.meter.core.Injector;
import de.synyx.meter.core.MeterProvider;
import de.synyx.meter.core.Substitution;
import de.synyx.meter.core.web.WebContext;
import de.synyx.meter.jersey.internal.DefaultJerseyInjector;
import de.synyx.meter.jersey.internal.DefaultJerseySubstitution;
import de.synyx.meter.jersey.internal.DefaultMeterInterceptorService;
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
public final class MeterFeature implements Feature {

    @Override
    public final boolean configure (FeatureContext context) {
        context.register (new MeterFeatureBinder ());

        return true;
    }

    final static class MeterFeatureBinder extends AbstractBinder {

        @Override
        protected final void configure () {
            bindFactory (DefaultMeterProviderFactory.class).to (MeterProvider.class).in (Singleton.class);
            bind (DefaultJerseyInjector.class).to (Injector.class).in (Singleton.class);
            bind (DefaultJerseySubstitution.class).to (Substitution.class).in (Singleton.class);
            bind (DefaultMeterInterceptorService.class).to (InterceptionService.class).in (Singleton.class);
        }

    }

    public final static class DefaultMeterProviderFactory implements Factory<MeterProvider> {

        private final WebContext web;

        private final ServletContext servlet;

        @Inject
        public DefaultMeterProviderFactory (@Context ServletContext servlet) {
            /* extension: create a compound web/meter provider using all providers or choose the correct one using environment facilities */

            this.web     = Iterators.get (ServiceLoader.load (WebContext.class).iterator (), 0, null);
            this.servlet = servlet;
        }

        @Override
        public final MeterProvider provide () {
            if (web != null) return web.initialize (servlet);
            else
                throw new IllegalStateException ("no web-context available to create a provider");
        }

        @Override
        public final void dispose (MeterProvider provider) {
            try {
                if (web != null) web.close ();
            } catch (Exception e) {
                LoggerFactory.getLogger (getClass ()).warn (e.getMessage ());
            }
        }

    }

}
