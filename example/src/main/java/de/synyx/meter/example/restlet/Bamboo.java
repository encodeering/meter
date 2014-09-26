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
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
@Path ("bamboo")
public class Bamboo {

    @Inject
    private BambooService<String> service;

    /**
     * <p>echo.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param locale a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    @GET
    @Metric (
        timers     = @Timer,
        histograms = @Histogram (value = "#size", measure = BambooResponseSize.class)
    )
    @Path ("{name}")
    public String echo (@PathParam ("name") String name, @QueryParam ("locale") String locale) {
        return service.call (name + "::" + locale);
    }

}
