package de.synyx.metrics.core.internal;

import com.codahale.metrics.Clock;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.MeterProvider;
import de.synyx.metrics.core.MetricAdvisor;
import de.synyx.metrics.core.MetricAspect;
import de.synyx.metrics.core.MetricNaming;
import de.synyx.metrics.core.Metriculate;
import de.synyx.metrics.core.annotation.Counter;
import de.synyx.metrics.core.annotation.Histogram;
import de.synyx.metrics.core.annotation.Meter;
import de.synyx.metrics.core.annotation.Metric;
import de.synyx.metrics.core.annotation.Timer;
import de.synyx.metrics.core.aspect.MetricAspectCounter;
import de.synyx.metrics.core.aspect.MetricAspectHistogram;
import de.synyx.metrics.core.aspect.MetricAspectMeter;
import de.synyx.metrics.core.aspect.MetricAspectTimer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Iterables.addAll;
import static de.synyx.metrics.core.MeterProviders.counterOf;
import static de.synyx.metrics.core.MeterProviders.histogramOf;
import static de.synyx.metrics.core.MeterProviders.meter;
import static de.synyx.metrics.core.MeterProviders.meterOf;
import static de.synyx.metrics.core.MeterProviders.timerOf;
import static java.util.Arrays.asList;

/**
* Date: 16.07.2014
* Time: 11:05
*/
public final class DefaultMetricMethodInterceptor implements MethodInterceptor {

    private final Clock clock = Clock.defaultClock ();

    private final Injector injector;

    private final MeterProvider provider;

    private final MetricNaming  naming;
    private final MetricAdvisor advisor;

    public DefaultMetricMethodInterceptor (Injector injector, MeterProvider provider, MetricNaming naming, MetricAdvisor advisor) {
        this.injector = injector;
        this.provider = provider;
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

        String basename = basename (method);

        List<MetricAspect> aspects;

                aspects = new ArrayList<> ();
        addAll (aspects, collect (metric.counters (),   counters   (basename)));
        addAll (aspects, collect (metric.histograms (), histograms (basename)));
        addAll (aspects, collect (metric.meters (),     meters     (basename)));
        addAll (aspects, collect (metric.timers (),     timers     (basename)));

        return advisor.around (invocation, aspects);
    }

    /* following hooks could be extracted to a product factory */

    final Function<Counter, MetricAspect> counters (final String basename) {
        return new Function<Counter, MetricAspect> () {

            @Override
            public final MetricAspect apply (final Counter annotation) {
                return new MetricAspectCounter (annotation,
                                                meter       (counterOf (provider), dynname (basename, annotation.value ())),
                                                metriculate (                                         annotation.metriculate ())
                );
            }

        };
    }

    final Function<Histogram, MetricAspect> histograms (final String basename) {
        return new Function<Histogram, MetricAspect> () {

            @Override
            public final MetricAspect apply (final Histogram annotation) {
                return new MetricAspectHistogram (annotation,
                                                  meter       (histogramOf (provider), dynname (basename, annotation.value ())),
                                                  metriculate (                                           annotation.metriculate ())
                );
            }

        };
    }

    final Function<Meter, MetricAspect> meters (final String basename) {
        return new Function<Meter, MetricAspect> () {

            @Override
            public final MetricAspect apply (final Meter annotation) {
                return new MetricAspectMeter (annotation,
                                              meter       (meterOf (provider), dynname (basename, annotation.value ())),
                                              metriculate (                                       annotation.metriculate ())
                );
            }
        };
    }

    final Function<Timer, MetricAspect> timers (final String basename) {
        return new Function<Timer, MetricAspect> () {

            @Override
            public final MetricAspect apply (final Timer annotation) {
                return new MetricAspectTimer (annotation,
                                              meter      (timerOf (provider), dynname (basename, annotation.value ())),
                                              clock

                );
            }

        };
    }

    final Supplier<String> dynname (final String basename, final String value) {
        return new Supplier<String> () {

            @Override
            public String get () {
                     if (value.equals     ("#")) return                         basename;
                else if (value.startsWith ("#")) return String.format ("%s.%s", basename, naming.name (value.substring (1)));
                else
                    return naming.name (value);
            }

        };
    }

    final Optional<Metriculate> metriculate (Class<? extends Metriculate> type) {
        if (Metriculate.class.equals (type)) return Optional.absent ();
        else
            return Optional.fromNullable (injector.create (type));
    }

    final <T extends Annotation> Iterable<MetricAspect> collect (final T[] annotation, Function<T, MetricAspect> fn) {
        return Iterables.transform (asList (annotation), fn);
    }

    final String basename (Method method) {
        return method.getDeclaringClass ().getName () + "." + method.getName ();
    }

}
