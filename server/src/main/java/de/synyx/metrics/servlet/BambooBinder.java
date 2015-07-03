package de.synyx.metrics.servlet;

import com.codahale.metrics.MetricRegistry;
import de.synyx.metrics.MetricInterceptorService;
import de.synyx.metrics.service.BambooApplicationService;
import de.synyx.metrics.service.BambooService;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;


/**
 * @author  Michael Clausen - clausen@synyx.de
 */
@Provider
class BambooBinder extends AbstractBinder {

    @Override
    protected final void configure() {
        bind (BambooApplicationService.class).to(new TypeLiteral<BambooService<String>> () {});

        bind (MetricInterceptorService.class).to(InterceptionService.class).in (Singleton.class);

        bindFactory (MetricRegistryFactory.class).to (MetricRegistry.class).in (Singleton.class);
    }

    public final static class MetricRegistryFactory implements Factory<MetricRegistry> {

        private final ServletContext session;

        @Inject
        public MetricRegistryFactory (@Context ServletContext session) {
            this.session = session;
        }

        @Override
        public final MetricRegistry provide () {
            return (MetricRegistry) session.getAttribute ("com.codahale.metrics.servlet.InstrumentedFilter.registry");
        }

        @Override
        public void dispose (MetricRegistry instance) {}

    }
}
