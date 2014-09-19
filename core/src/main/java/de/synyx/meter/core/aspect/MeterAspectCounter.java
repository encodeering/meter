package de.synyx.meter.core.aspect;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import de.synyx.meter.core.Measure;
import de.synyx.meter.core.Meter;
import de.synyx.meter.core.annotation.Counter;

import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public final class MeterAspectCounter extends MeterAspectSupport {

    private final Counter annotation;

    private final Supplier<Meter<Dimensionless>> meter;

    private final Measure measure;

    public MeterAspectCounter (Counter annotation, Supplier<Meter<Dimensionless>> meter, Optional<Measure> measure) {
        this.meter      = meter;
        this.annotation = annotation;
        this.measure    = measure.or (new CounterMeasure ());
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
        long measurable = measure.determine (response, throwable);

        switch (annotation.operation ()) {
            case Increment: meter.get ().update (javax.measure.Measure.valueOf (measurable, Unit.ONE)); break;
            case Decrement: meter.get ().update (javax.measure.Measure.valueOf (-measurable, Unit.ONE)); break;
        }
    }

    final class CounterMeasure implements Measure {

        @Override
        public final long determine (Object response, Throwable throwable) {
            return annotation.number ();
        }

    }

}
