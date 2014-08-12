package de.synyx.metrics.core.aspect;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import de.synyx.metrics.core.Meter;
import de.synyx.metrics.core.Metriculate;
import de.synyx.metrics.core.annotation.Histogram;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

/**
 * Date: 16.07.2014
 * Time: 11:25
 */
public final class MetricAspectHistogram extends MetricAspectSupport {

    private final Histogram annotation;

    private final Supplier<Meter<Dimensionless>> meter;

    private final Metriculate metriculate;

    public MetricAspectHistogram (Histogram annotation, Supplier<Meter<Dimensionless>> meter, Optional<Metriculate> metriculate) {
        this.meter       = meter;
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
        meter.get ().update (Measure.valueOf (metriculate.determine (response, throwable), Unit.ONE));
    }

    final class HistogramMetriculate implements Metriculate {

        @Override
        public final long determine (Object response, Throwable throwable) {
            return annotation.number ();
        }

    }

}
