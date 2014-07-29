package de.synyx.metrics.servlet;

import de.synyx.metrics.annotation.Histogram;
import de.synyx.metrics.annotation.Metric;
import de.synyx.metrics.annotation.Timer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Date: 15.07.2014
 * Time: 15:51
 */
@Path ("bamboo")
public class Bamboo {

    @GET
    @Metric (
            timers     = @Timer,
            histograms = @Histogram (value = "#size", metriculate = BambooHistogramHook.class)
    )
    public String echo (@QueryParam ("foo") String foo) {
        return foo;
    }

}
