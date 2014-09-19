package de.synyx.meter.jersey.internal;

import de.synyx.meter.core.Injector;
import de.synyx.meter.core.MeterProvider;
import de.synyx.meter.core.annotation.Metric;
import de.synyx.meter.core.internal.DefaultSubstitution;
import de.synyx.meter.core.internal.aop.DefaultAdvisor;
import de.synyx.meter.core.internal.aop.DefaultMeterInterceptor;
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
public final class DefaultMeterInterception implements InterceptionService {

    private final List<MethodInterceptor> interceptors;

    @Inject
    public DefaultMeterInterception (Injector injector, MeterProvider provider) {
        interceptors = Collections.<MethodInterceptor>singletonList (new DefaultMeterInterceptor (injector, provider, injector.create (DefaultSubstitution.class),
                                                                                                                      injector.create (DefaultAdvisor.class)
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
