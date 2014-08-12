package de.synyx.metrics.core.aspect;

import com.google.common.base.Optional;
import de.synyx.metrics.core.Metriculate;
import de.synyx.metrics.core.annotation.Histogram;

/**
 * Date: 16.07.2014
 * Time: 11:25
 */
public final class MetricAspectHistogram extends MetricAspectSupport {

    private final com.codahale.metrics.Histogram histogram;
    private final Histogram                      annotation;

    private final Metriculate metriculate;

    public MetricAspectHistogram (com.codahale.metrics.Histogram histogram, Histogram annotation, Optional<Metriculate> metriculate) {
        this.histogram   = histogram;
        this.annotation  = annotation;
        this.metriculate = metriculate.or (new HistogramMetriculate ());
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
        histogram.update (metriculate.determine (response, throwable));
    }

    final class HistogramMetriculate implements Metriculate {

        @Override
        public final long determine (Object response, Throwable throwable) {
            return annotation.number ();
        }

    }

}
