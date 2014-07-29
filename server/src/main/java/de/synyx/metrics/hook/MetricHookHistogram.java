package de.synyx.metrics.hook;

import de.synyx.metrics.Metriculate;
import de.synyx.metrics.annotation.Histogram;
import org.glassfish.hk2.api.ServiceLocator;

/**
 * Date: 16.07.2014
 * Time: 11:25
 */
public final class MetricHookHistogram extends MetricHookSupport {

    private final com.codahale.metrics.Histogram histogram;
    private final Histogram annotation;

    public MetricHookHistogram (ServiceLocator locator, com.codahale.metrics.Histogram histogram, Histogram annotation) {
        super (locator);

        this.histogram  = histogram;
        this.annotation = annotation;
    }

    @Override
    public final void before () {
        histogram.update (annotation.number ());
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
        histogram.update (determine (custom (annotation.metriculate ()).or (new HistogramMetriculate ()), response, throwable));
    }

    final class HistogramMetriculate implements Metriculate {

        @Override
        public final long determine (Object response, Throwable throwable) {
            return annotation.number ();
        }

    }

}
