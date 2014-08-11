package de.synyx.metrics.core.hook;

import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.Metriculate;
import de.synyx.metrics.core.annotation.Counter;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public final class MetricHookCounter extends MetricHookSupport {

    private final com.codahale.metrics.Counter counter;
    private final Counter annotation;

    public MetricHookCounter (Injector injector, com.codahale.metrics.Counter counter, Counter annotation) {
        super (injector);

        this.counter    = counter;
        this.annotation = annotation;
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
        long value = determine (custom (annotation.metriculate ()).or (new CounterMetriculate ()), response, throwable);

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
