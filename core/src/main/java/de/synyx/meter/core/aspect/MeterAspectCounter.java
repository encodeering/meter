package de.synyx.meter.core.aspect;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import de.synyx.meter.core.Metriculate;
import de.synyx.meter.core.Meter;
import de.synyx.meter.core.annotation.Counter;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public final class MeterAspectCounter extends MeterAspectSupport {

    private final Counter annotation;

    private final Supplier<Meter<Dimensionless>> meter;

    private final Metriculate metriculate;

    public MeterAspectCounter (Counter annotation, Supplier<Meter<Dimensionless>> meter, Optional<Metriculate> metriculate) {
        this.meter       = meter;
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
        long measurable = metriculate.determine (response, throwable);

        switch (annotation.operation ()) {
            case Increment: meter.get ().update (Measure.valueOf (  measurable, Unit.ONE)); break;
            case Decrement: meter.get ().update (Measure.valueOf (- measurable, Unit.ONE)); break;
        }
    }

    final class CounterMetriculate implements Metriculate {

        @Override
        public final long determine (Object response, Throwable throwable) {
            return annotation.number ();
        }

    }

}
