package de.synyx.metrics.core.aspect;

import com.google.common.base.Optional;
import de.synyx.metrics.core.Metriculate;
import de.synyx.metrics.core.annotation.Counter;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public final class MetricAspectCounter extends MetricAspectSupport {

    private final com.codahale.metrics.Counter counter;
    private final Counter                      annotation;

    private final Metriculate metriculate;

    public MetricAspectCounter (com.codahale.metrics.Counter counter, Counter annotation, Optional<Metriculate> metriculate) {
        this.counter     = counter;
        this.annotation  = annotation;
        this.metriculate = metriculate.or (new CounterMetriculate ());
    }

    @Override
    public final void after (Object response, Throwable throwable) {
        switch (annotation.kind ()) {
            case Success:  if (throwable == null) call (response, null); break;
            case Error:    if (throwable != null) call (response, throwable); break;
            case Both:                            call (response, throwable); break;
        }
    }

    private void call (Object response, Throwable throwable) {
        long value = metriculate.determine (response, throwable);

        switch (annotation.operation ()) {
            case Increment: counter.inc (value); break;
            case Decrement: counter.dec (value); break;
        }
    }

    final class CounterMetriculate implements Metriculate {

        @Override
        public final long determine (Object response, Throwable throwable) {
            return annotation.number ();
        }

    }

}
