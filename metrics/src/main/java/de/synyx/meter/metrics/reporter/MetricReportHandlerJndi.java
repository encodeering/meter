package de.synyx.meter.metrics.reporter;

import com.codahale.metrics.ScheduledReporter;
import de.synyx.meter.core.MeterProvider;

import java.net.URI;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Date: 31.07.2014
 * Time: 15:50
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class MetricReportHandlerJndi extends MetricReportHandler {

    private final Context context;

    /**
     * <p>Constructor for MetricReportHandlerJndi.</p>
     *
     * @param context a {@link javax.naming.Context} object.
     */
    public MetricReportHandlerJndi (Context context) {
        this.context = context;
    }

    /**
     * <p>scheme.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected final String scheme () {
        return "java";
    }

    /** {@inheritDoc} */
    @Override
    protected final ScheduledReporter reporter (MetricReportMediator mediator, MeterProvider registry, URI location) {
        try {
            Object lookup;

                lookup = context.lookup (location.toString ());
            if (lookup instanceof String) return mediator.reporter ((String) lookup);
            if (lookup instanceof URI)    return mediator.reporter (lookup.toString ());
        } catch (NamingException e) {
            logger.error (e.getMessage ());
        }

        return null;
    }

}
