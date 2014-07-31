package de.synyx.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import de.synyx.metrics.annotation.Counter;
import de.synyx.metrics.annotation.Histogram;
import de.synyx.metrics.annotation.Meter;
import de.synyx.metrics.annotation.Metric;
import de.synyx.metrics.annotation.Timer;
import de.synyx.metrics.hook.MetricHookCounter;
import de.synyx.metrics.hook.MetricHookHistogram;
import de.synyx.metrics.hook.MetricHookMeter;
import de.synyx.metrics.hook.MetricHookSupport;
import de.synyx.metrics.hook.MetricHookTimer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.ServiceLocator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;

/**
* Date: 16.07.2014
* Time: 11:05
*/
final class MetricMethodInterceptor implements MethodInterceptor {

    private final ServiceLocator locator;

    private final MetricRegistry   registry;
    private final MetricNaming     naming;
    private final MetricInvocation invoker;

    MetricMethodInterceptor (ServiceLocator locator, MetricRegistry registry, MetricNaming naming, MetricInvocation invoker) {
        this.locator  = locator;
        this.registry = registry;
        this.invoker  = invoker;
        this.naming   = naming;
    }

    @Override
    public final Object invoke (final MethodInvocation invocation) throws Throwable {
        Metric metric;

        Method method;

                     method = invocation.getMethod ();
            metric = method.getAnnotation (Metric.class);
        if (metric == null) throw new IllegalStateException ("Method [" + method.getName () + "] not annotated");

        List<MetricHook> hooks;

                hooks = new ArrayList<> ();
        addAll (hooks, collect (metric.counters (),   counter   (method)));
        addAll (hooks, collect (metric.histograms (), histogram (method)));
        addAll (hooks, collect (metric.meters (),     meter     (method)));
        addAll (hooks, collect (metric.timers (),     timer     (method)));

        return invoker.invoke (wrap (invocation), hooks);
    }

    /* following hooks could be extracted to a product factory */

    final Function<Counter, MetricHook> counter (final Method method) {
        return new Function<Counter, MetricHook> () {

            @Override
            public final MetricHook apply (final Counter counter) {
                return name (method, counter.value ()).transform (new Function<String, MetricHook> () {

                    @Override
                    public final MetricHook apply (String name) {
                        return new MetricHookCounter (locator, registry.counter (name), counter);
                    }

                }).or (MetricHookSupport.Noop);
            }
        };
    }

    final Function<Histogram, MetricHook> histogram (final Method method) {
        return new Function<Histogram, MetricHook> () {

            @Override
            public final MetricHook apply (final Histogram histogram) {
                return name (method, histogram.value ()).transform (new Function<String, MetricHook> () {

                    @Override
                    public final MetricHook apply (String name) {
                        return new MetricHookHistogram (locator, registry.histogram (name), histogram);
                    }

                }).or (MetricHookSupport.Noop);
            }

        };
    }

    final Function<Meter, MetricHook> meter (final Method method) {
        return new Function<Meter, MetricHook> () {

            @Override
            public final MetricHook apply (final Meter meter) {
                return name (method, meter.value ()).transform (new Function<String, MetricHook> () {

                    @Override
                    public final MetricHook apply (String name) {
                        return new MetricHookMeter (locator, registry.meter (name), meter);
                    }

                }).or (MetricHookSupport.Noop);
            }
        };
    }

    final Function<Timer, MetricHook> timer (final Method method) {
        return new Function<Timer, MetricHook> () {

            @Override
            public final MetricHook apply (final Timer timer) {
                return name (method, timer.value ()).transform (new Function<String, MetricHook> () {

                    @Override
                    public final MetricHook apply (String name) {
                        return new MetricHookTimer (locator, registry.timer (name), timer);
                    }

                }).or (MetricHookSupport.Noop);
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

        };
    }

    private <T extends Annotation> Iterable<MetricHook> collect (final T[] annotation, Function<T, MetricHook> fn) {
        return transform (asList (annotation), fn);
    }

}
