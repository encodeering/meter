package de.synyx.metrics.servlet;

import de.synyx.metrics.MetricInterceptorService;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import de.synyx.metrics.service.BambooApplicationService;
import de.synyx.metrics.service.BambooService;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import javax.ws.rs.ext.Provider;


/**
 * @author  Michael Clausen - clausen@synyx.de
 */
@Provider
class BambooBinder extends AbstractBinder {

    @Override
    protected final void configure() {
        bind (BambooApplicationService.class).to(new TypeLiteral<BambooService<String>> () {});

        MetricRegistry registry = new MetricRegistry ();

        ConsoleReporter reporter = ConsoleReporter.forRegistry (registry)
                                                        .convertRatesTo (TimeUnit.SECONDS)
                                                        .convertDurationsTo (TimeUnit.MILLISECONDS)
                                                        .build ();

        reporter.start (10, TimeUnit.SECONDS);

        bind (registry).to (MetricRegistry.class);
        bind (MetricInterceptorService.class).to(InterceptionService.class).in (Singleton.class);
    }
}
