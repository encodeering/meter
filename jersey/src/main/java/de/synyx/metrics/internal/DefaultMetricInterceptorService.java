package de.synyx.metrics.internal;

import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.MeterProvider;
import de.synyx.metrics.core.annotation.Metric;
import de.synyx.metrics.core.internal.DefaultMetricAdvisor;
import de.synyx.metrics.core.internal.DefaultMetricMethodInterceptor;
import de.synyx.metrics.core.internal.DefaultMetricNaming;
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
public final class DefaultMetricInterceptorService implements InterceptionService {

    private final List<MethodInterceptor> interceptors;

    @Inject
    public DefaultMetricInterceptorService (Injector injector, MeterProvider provider) {
        interceptors = Collections.<MethodInterceptor>singletonList (new DefaultMetricMethodInterceptor (injector, provider, injector.create (DefaultMetricNaming.class),
                                                                                                                             injector.create (DefaultMetricAdvisor.class)
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
