package de.synyx.metrics.core.internal;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.MetricAspect;
import de.synyx.metrics.core.MetricAdvisor;
import de.synyx.metrics.core.MetricNaming;
import de.synyx.metrics.core.aspect.MetricAspectMeter;
import de.synyx.metrics.core.aspect.MetricAspectSupport;
import de.synyx.metrics.core.aspect.MetricAspectTimer;
import de.synyx.metrics.core.aspect.MetricAspectHistogram;
import de.synyx.metrics.core.aspect.MetricAspectCounter;
import de.synyx.metrics.core.annotation.Counter;
import de.synyx.metrics.core.annotation.Histogram;
import de.synyx.metrics.core.annotation.Meter;
import de.synyx.metrics.core.annotation.Metric;
import de.synyx.metrics.core.annotation.Timer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.collect.Iterables.addAll;
import static java.util.Arrays.asList;

/**
* Date: 16.07.2014
* Time: 11:05
*/
public final class DefaultMetricMethodInterceptor implements MethodInterceptor {

    private final Injector injector;

    private final MetricRegistry registry;
    private final MetricNaming   naming;
    private final MetricAdvisor  advisor;

    public DefaultMetricMethodInterceptor (Injector injector, MetricRegistry registry, MetricNaming naming, MetricAdvisor advisor) {
        this.injector = injector;
        this.registry = registry;
        this.advisor  = advisor;
        this.naming   = naming;
    }

    @Override
    public final Object invoke (final MethodInvocation invocation) throws Throwable {
        Metric metric;

        Method method;

                     method = invocation.getMethod ();
            metric = method.getAnnotation (Metric.class);
        if (metric == null) throw new IllegalStateException ("Method [" + method.getName () + "] not annotated");

        List<MetricAspect> aspects;

                aspects = new ArrayList<> ();
        addAll (aspects, collect (metric.counters (),   counter   (method)));
        addAll (aspects, collect (metric.histograms (), histogram (method)));
        addAll (aspects, collect (metric.meters (),     meter     (method)));
        addAll (aspects, collect (metric.timers (),     timer     (method)));

        return advisor.around (wrap (invocation), aspects);
    }

    /* following hooks could be extracted to a product factory */

    final Function<Counter, MetricAspect> counter (final Method method) {
        return new Function<Counter, MetricAspect> () {

            @Override
            public final MetricAspect apply (final Counter counter) {
                return name (method, counter.value ()).transform (new Function<String, MetricAspect> () {

                    @Override
                    public final MetricAspect apply (String name) {
                        return new MetricAspectCounter (injector, registry.counter (name), counter);
                    }

                }).or (MetricAspectSupport.Noop);
            }
        };
    }

    final Function<Histogram, MetricAspect> histogram (final Method method) {
        return new Function<Histogram, MetricAspect> () {

            @Override
            public final MetricAspect apply (final Histogram histogram) {
                return name (method, histogram.value ()).transform (new Function<String, MetricAspect> () {

                    @Override
                    public final MetricAspect apply (String name) {
                        return new MetricAspectHistogram (injector, registry.histogram (name), histogram);
                    }

                }).or (MetricAspectSupport.Noop);
            }

        };
    }

    final Function<Meter, MetricAspect> meter (final Method method) {
        return new Function<Meter, MetricAspect> () {

            @Override
            public final MetricAspect apply (final Meter meter) {
                return name (method, meter.value ()).transform (new Function<String, MetricAspect> () {

                    @Override
                    public final MetricAspect apply (String name) {
                        return new MetricAspectMeter (injector, registry.meter (name), meter);
                    }

                }).or (MetricAspectSupport.Noop);
            }
        };
    }

    final Function<Timer, MetricAspect> timer (final Method method) {
        return new Function<Timer, MetricAspect> () {

            @Override
            public final MetricAspect apply (final Timer timer) {
                return name (method, timer.value ()).transform (new Function<String, MetricAspect> () {

                    @Override
                    public final MetricAspect apply (String name) {
                        return new MetricAspectTimer (injector, registry.timer (name), timer);
                    }

                }).or (MetricAspectSupport.Noop);
            }

        };
    }

    final Optional<String> name (Method method, String value) {
        if (value.startsWith ("#")) return Optional.fromNullable (MetricRegistry.name (method.getDeclaringClass (), method.getName (), naming.name (value.substring (1))));
        else
            return Optional.of (naming.name (value));
    }

    /* */

    private Callable<Object> wrap (final MethodInvocation invocation) {
        return new Callable<Object> () {

            @Override
            public final Object call () throws Exception {
                try {
                    return invocation.proceed ();
                } catch (Exception e) {
                    throw e;
                } catch (Throwable e) {
                    throw new Exception (e.getMessage (), e.getCause ());
                }
            }

            @Override
            public final String toString () {
                Method method = invocation.getMethod ();
                return method.getDeclaringClass ().getSimpleName () + " " + method.getName ();
            }

        };
    }

    private <T extends Annotation> Iterable<MetricAspect> collect (final T[] annotation, Function<T, MetricAspect> fn) {
        return Iterables.transform (asList (annotation), fn);
    }

}
