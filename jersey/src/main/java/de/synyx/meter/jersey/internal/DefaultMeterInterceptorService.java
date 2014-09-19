package de.synyx.meter.jersey.internal;

import de.synyx.meter.core.Injector;
import de.synyx.meter.core.MeterProvider;
import de.synyx.meter.core.annotation.Metric;
import de.synyx.meter.core.internal.DefaultMeterAdvisor;
import de.synyx.meter.core.internal.DefaultMeterMethodInterceptor;
import de.synyx.meter.core.internal.DefaultMeterNaming;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.BuilderHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

/**
 * Date: 15.07.2014
 * Time: 15:46
 */
public final class DefaultMeterInterceptorService implements InterceptionService {

    private final List<MethodInterceptor> interceptors;

    @Inject
    public DefaultMeterInterceptorService (Injector injector, MeterProvider provider) {
        interceptors = Collections.<MethodInterceptor>singletonList (new DefaultMeterMethodInterceptor (injector, provider, injector.create (DefaultMeterNaming.class),
                                                                                                                            injector.create (DefaultMeterAdvisor.class)
        ));
    }

    @Override
    public final Filter getDescriptorFilter () {
        return BuilderHelper.allFilter ();
    }

    @Override
    public final List<MethodInterceptor> getMethodInterceptors (Method method) {
        return method != null &&
               method.isAnnotationPresent (Metric.class) ? interceptors : null;
    }

    @Override
    public final List<ConstructorInterceptor> getConstructorInterceptors (Constructor<?> constructor) {
        return null;
    }

}
