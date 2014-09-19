package de.synyx.meter.core.internal;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import de.synyx.meter.core.Injector;
import de.synyx.meter.core.MeterNaming;
import de.synyx.meter.core.MeterProvider;
import de.synyx.meter.core.MeterAdvisor;
import de.synyx.meter.core.MeterAspect;
import de.synyx.meter.core.Metriculate;
import de.synyx.meter.core.annotation.Counter;
import de.synyx.meter.core.annotation.Histogram;
import de.synyx.meter.core.annotation.Meter;
import de.synyx.meter.core.annotation.Metric;
import de.synyx.meter.core.annotation.Timer;
import de.synyx.meter.core.aspect.MeterAspectCounter;
import de.synyx.meter.core.aspect.MeterAspectHistogram;
import de.synyx.meter.core.aspect.MeterAspectMeter;
import de.synyx.meter.core.aspect.MeterAspectTimer;
import de.synyx.meter.core.util.Clock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Iterables.addAll;
import static de.synyx.meter.core.MeterProviders.counterOf;
import static de.synyx.meter.core.MeterProviders.histogramOf;
import static de.synyx.meter.core.MeterProviders.meter;
import static de.synyx.meter.core.MeterProviders.meterOf;
import static de.synyx.meter.core.MeterProviders.timerOf;
import static java.util.Arrays.asList;

/**
* Date: 16.07.2014
* Time: 11:05
*/
public final class DefaultMeterMethodInterceptor implements MethodInterceptor {

    private final Injector injector;

    private final MeterProvider provider;

    private final MeterNaming  naming;
    private final MeterAdvisor advisor;

    public DefaultMeterMethodInterceptor (Injector injector, MeterProvider provider, MeterNaming naming, MeterAdvisor advisor) {
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

        List<MeterAspect> aspects;

                aspects = new ArrayList<> ();
        addAll (aspects, collect (metric.counters (),   counters   (basename)));
        addAll (aspects, collect (metric.histograms (), histograms (basename)));
        addAll (aspects, collect (metric.meters (),     meters     (basename)));
        addAll (aspects, collect (metric.timers (),     timers     (basename)));

        return advisor.around (invocation, aspects);
    }

    /* following hooks could be extracted to a product factory */

    final Function<Counter, MeterAspect> counters (final String basename) {
        return new Function<Counter, MeterAspect> () {

            @Override
            public final MeterAspect apply (final Counter annotation) {
                return new MeterAspectCounter (annotation,
                                               meter (counterOf (provider), dynname (basename, annotation.value ())),
                                               metriculate (annotation.metriculate ())
                );
            }

        };
    }

    final Function<Histogram, MeterAspect> histograms (final String basename) {
        return new Function<Histogram, MeterAspect> () {

            @Override
            public final MeterAspect apply (final Histogram annotation) {
                return new MeterAspectHistogram (annotation,
                                                  meter       (histogramOf (provider), dynname (basename, annotation.value ())),
                                                  metriculate (                                           annotation.metriculate ())
                );
            }

        };
    }

    final Function<Meter, MeterAspect> meters (final String basename) {
        return new Function<Meter, MeterAspect> () {

            @Override
            public final MeterAspect apply (final Meter annotation) {
                return new MeterAspectMeter (annotation,
                                              meter       (meterOf (provider), dynname (basename, annotation.value ())),
                                              metriculate (                                       annotation.metriculate ())
                );
            }
        };
    }

    final Function<Timer, MeterAspect> timers (final String basename) {
        return new Function<Timer, MeterAspect> () {

            @Override
            public final MeterAspect apply (final Timer annotation) {
                return new MeterAspectTimer (annotation,
                                              meter      (timerOf (provider), dynname (basename, annotation.value ())),
                                              clock      (                                       annotation.clock ())

                );
            }

        };
    }

    final Clock clock (Class<? extends Clock> clock) {
        return injector.create (clock);
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

    final <T extends Annotation> Iterable<MeterAspect> collect (final T[] annotation, Function<T, MeterAspect> fn) {
        return Iterables.transform (asList (annotation), fn);
    }

    final String basename (Method method) {
        return method.getDeclaringClass ().getName () + "." + method.getName ();
    }

}
