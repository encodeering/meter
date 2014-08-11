package de.synyx.metrics.example.restlet;

import de.synyx.metrics.core.Metriculate;

/**
 * Date: 16.07.2014
 * Time: 11:49
 */
public final class BambooHistogramHook implements Metriculate {

    @Override
    public final long determine (Object response, Throwable throwable) {
        if (! (response instanceof String)) return 0;

        return ((String) response).length ();
    }

}
