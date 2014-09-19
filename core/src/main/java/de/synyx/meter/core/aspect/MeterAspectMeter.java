package de.synyx.meter.core.aspect;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import de.synyx.meter.core.Metriculate;
import de.synyx.meter.core.annotation.Meter;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

/**
 * Date: 16.07.2014
 * Time: 11:22
 */
public final class MeterAspectMeter extends MeterAspectSupport {

    private final Meter annotation;

    private final Supplier<de.synyx.meter.core.Meter<Dimensionless>> meter;

    private final Metriculate metriculate;

    public MeterAspectMeter (Meter annotation, Supplier<de.synyx.meter.core.Meter<Dimensionless>> meter, Optional<Metriculate> metriculate) {
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
        meter.get ().update (Measure.valueOf (metriculate.determine (response, throwable), Unit.ONE));
    }

    final class MeterMetriculate implements Metriculate {

        @Override
        public final long determine (Object response, Throwable throwable) {
            return annotation.number ();
        }
    }

}
