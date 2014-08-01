package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Date: 31.07.2014
 * Time: 09:15
 */
public final class MetricListener implements ServletContextListener {

    private final static Logger logger = LoggerFactory.getLogger (MetricListener.class);

    private final static String AttrRegistry = "com.codahale.metrics.servlet.InstrumentedFilter.registry";
    private final static String AttrReporter = "metrics-reporter";

    private final MetricRegistry       registry = new MetricRegistry ();
    private final MetricReportMediator mediator = new MetricReportMediator (registry, jndi (), graphite ());

    private ScheduledReporter reporter;

    @Override
    public final void contextInitialized (ServletContextEvent event) {
        ServletContext context = event.getServletContext ();
                       context.setAttribute (AttrRegistry, registry);

        reporter = mediator.reporter (context.getInitParameter (AttrReporter));
    }

    @Override
    public final void contextDestroyed (ServletContextEvent sce) {
        if (reporter != null)
            reporter.stop ();
    }

    final static MetricReportHandler jndi () {
        try {
            return new MetricReportHandlerJndi (new InitialContext ());
        } catch (NamingException e) {
            logger.error (e.getMessage (), e);
        }

        return new NoopMetricReportHandler ();
    }

    final static MetricReportHandler graphite () {
        return new MetricReportHandlerGraphite ();
    }

    final static class NoopMetricReportHandler extends MetricReportHandler {

        @Override
        protected final String scheme () {
            return null;
        }

        @Override
        protected final ScheduledReporter reporter (MetricReportMediator mediator, MetricRegistry registry, URI location) {
            return null;
        }

    }

}
