package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Date: 31.07.2014
 * Time: 09:15
 */
public final class MetricListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    private final static String AttrRegistry = "com.codahale.metrics.servlet.InstrumentedFilter.registry";
    private final static String AttrReporter = "metrics-reporter";

    private final MetricRegistry registry = new MetricRegistry ();

    private ScheduledReporter reporter;

    @Override
    public final void contextInitialized (ServletContextEvent event) {
        ServletContext context = event.getServletContext ();

        try {
                                 context.setAttribute (AttrRegistry, registry);
        } finally {
            reporter = reporter (context.getInitParameter (AttrReporter));
        }
    }

    @Override
    public final void contextDestroyed (ServletContextEvent sce) {
        if (reporter != null)
            reporter.stop ();
    }

    final ScheduledReporter reporter (String reporturl) {
        try {
            return new MetricReportHandlerGraphite ().select (registry, URI.create (reporturl)).orNull ();
        } catch (RuntimeException e) {
            logger.error (e.getMessage ());
        }

        return null;
    }

}
