package de.synyx.metrics.internal;

import com.codahale.metrics.MetricRegistry;
import de.synyx.metrics.Substitution;
import de.synyx.metrics.annotation.Metric;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

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
    public DefaultMetricInterceptorService (ServiceLocator locator, MetricRegistry registry) {
        ServiceLocatorUtilities.addOneConstant (locator, locator.createAndInitialize (DefaultJerseySubstitution.class), "substitution", Substitution.class);

        interceptors = Collections.<MethodInterceptor>singletonList (new DefaultMetricMethodInterceptor (locator, registry, locator.createAndInitialize (DefaultMetricNaming.class),
                                                                                                            locator.createAndInitialize (DefaultMetricInvocation.class)
        ));
    }

    @Override
    public final Filter getDescriptorFilter () {
        return BuilderHelper.allFilter ();
    }

    @Override
    public final List<MethodInterceptor> getMethodInterceptors (Method method) {
        return method.isAnnotationPresent (Metric.class) ? interceptors : null;
    }

    @Override
    public final List<ConstructorInterceptor> getConstructorInterceptors (Constructor<?> constructor) {
        return null;
    }

}
