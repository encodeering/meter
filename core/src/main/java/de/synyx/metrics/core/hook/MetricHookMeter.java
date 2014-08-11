package de.synyx.metrics.core.hook;

import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.Metriculate;
import de.synyx.metrics.core.annotation.Meter;

/**
 * Date: 16.07.2014
 * Time: 11:22
 */
public final class MetricHookMeter extends MetricHookSupport {

    private final com.codahale.metrics.Meter meter;
    private final Meter annotation;

    public MetricHookMeter (Injector injector, com.codahale.metrics.Meter meter, Meter annotation) {
        super (injector);

        this.meter      = meter;
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
        meter.mark (determine (custom (annotation.metriculate ()).or (new MeterMetriculate ()), response, throwable));
    }

    final class MeterMetriculate implements Metriculate {

        @Override
        public final long determine (Object response, Throwable throwable) {
            return annotation.number ();
        }
    }

}
