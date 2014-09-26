package de.synyx.meter.core.aspect;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import de.synyx.meter.core.Measure;
import de.synyx.meter.core.Meter;
import de.synyx.meter.core.annotation.Counter;

import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

/**
 * <p>Aspect to measure the number of occurring events.</p>
 *
 * Date: 16.07.2014
 * Time: 11:03
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class MeterAspectCounter extends MeterAspectSupport {

    private final Counter annotation;

    private final Supplier<Meter<Dimensionless>> meter;

    private final Measure measure;

    /**
     * <p>Constructor</p>
     * <p>
     *    A supplier will be used to obtain the associated meter instance, which will be resolved dynamically on each invocation.
     * </p>
     *
     * @param annotation specifies a {@link de.synyx.meter.core.annotation.Counter} configuration.
     * @param meter specifies the dynamically resolved meter {@link de.synyx.meter.core.Meter} instance.
     * @param measure specifies the {@link com.google.common.base.Optional} measurement.
     */
    public MeterAspectCounter (Counter annotation, Supplier<Meter<Dimensionless>> meter, Optional<Measure> measure) {
        this.meter      = meter;
        this.annotation = annotation;
        this.measure    = measure.or (new CounterMeasure ());
    }

    /** {@inheritDoc} */
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
