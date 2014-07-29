package de.synyx.metrics.hook;

import de.synyx.metrics.MetricHook;
import de.synyx.metrics.Metriculate;
import com.google.common.base.Optional;
import org.glassfish.hk2.api.ServiceLocator;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public abstract class MetricHookSupport implements MetricHook {

    public final static MetricHook Noop = new MetricHookSupport (null) {};

    private final ServiceLocator locator;

    protected MetricHookSupport (ServiceLocator locator) {
        this.locator = locator;
    }

    @Override
    public void before () {}

    @Override
    public void after (Object response, Throwable throwable) {}

    protected final Optional<Metriculate> custom (Class<? extends Metriculate> type) {
        if (Metriculate.class.equals (type)) return Optional.absent ();
        else
            return Optional.fromNullable (locator.create (type));
    }

    protected final long determine (Metriculate metriculate, Object response, Throwable throwable) {
        try {
            locator.inject (metriculate);
            locator.postConstruct (metriculate);

            return metriculate.determine (response, throwable);
        } finally {
            locator.preDestroy (metriculate);
        }
    }

}
