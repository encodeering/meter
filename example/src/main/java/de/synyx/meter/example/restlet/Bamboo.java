package de.synyx.meter.example.restlet;

import de.synyx.meter.core.annotation.Histogram;
import de.synyx.meter.core.annotation.Metric;
import de.synyx.meter.core.annotation.Timer;
import de.synyx.meter.example.service.BambooService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Date: 15.07.2014
 * Time: 15:51
 */
@Path ("bamboo")
public class Bamboo {

    @Inject
    private BambooService<String> service;

    @GET
    @Metric (
        timers     = @Timer,
        histograms = @Histogram (value = "#size", metriculate = BambooHistogramHook.class)
    )
    @Path ("{name}")
    public String echo (@PathParam ("name") String name, @QueryParam ("locale") String locale) {
        return service.call (name + "::" + locale);
    }

}
