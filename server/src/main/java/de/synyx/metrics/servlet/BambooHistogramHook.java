package de.synyx.metrics.servlet;

import de.synyx.metrics.Metriculate;

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
