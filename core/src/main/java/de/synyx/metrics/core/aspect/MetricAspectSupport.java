package de.synyx.metrics.core.aspect;

import com.google.common.base.Optional;
import de.synyx.metrics.core.Injector;
import de.synyx.metrics.core.MetricAspect;
import de.synyx.metrics.core.Metriculate;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public abstract class MetricAspectSupport implements MetricAspect {

    public final static MetricAspect Noop = new MetricAspectSupport (null) {};

    private final Injector injector;

    protected MetricAspectSupport (Injector locator) {
        this.injector = locator;
    }

    @Override
    public void before () {}

    @Override
    public void after (Object response, Throwable throwable) {}

    protected final Optional<Metriculate> custom (Class<? extends Metriculate> type) {
        if (Metriculate.class.equals (type)) return Optional.absent ();
        else
            return Optional.fromNullable (injector.create (type));
    }

    protected final long determine (Metriculate metriculate, Object response, Throwable throwable) {
        return injector.inject (metriculate).determine (response, throwable);
    }

}
