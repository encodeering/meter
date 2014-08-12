package de.synyx.metrics.core.aspect;

import com.google.common.base.Optional;
import de.synyx.metrics.core.Metriculate;
import de.synyx.metrics.core.annotation.Meter;

/**
 * Date: 16.07.2014
 * Time: 11:22
 */
public final class MetricAspectMeter extends MetricAspectSupport {

    private final com.codahale.metrics.Meter meter;
    private final Meter annotation;

    private final Metriculate metriculate;

    public MetricAspectMeter (com.codahale.metrics.Meter meter, Meter annotation, Optional<Metriculate> metriculate) {
        this.meter       = meter;
        this.annotation  = annotation;
        this.metriculate = metriculate.or (new MeterMetriculate ());
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
        meter.mark (metriculate.determine (response, throwable));
    }

    final class MeterMetriculate implements Metriculate {

        @Override
        public final long determine (Object response, Throwable throwable) {
            return annotation.number ();
        }
    }

}
