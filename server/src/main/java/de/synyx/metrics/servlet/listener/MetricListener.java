package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public final static String AttrReporterWeb = "com.codahale.metrics.reporter";
    public final static String AttrRegistryWeb = "com.codahale.metrics.servlet.InstrumentedFilter.registry";

    public final static String AttrRegistryJndi = "java:comp/env/metrics/registry";

    private MetricRegistry registry;

    private ScheduledReporter reporter;

    @Override
    public final void contextInitialized (ServletContextEvent event) {
        InitialContext jndi = null;

        try {
            jndi = new InitialContext ();
        } catch (NamingException e) {
            logger.error (e.getMessage ());
        }

        registry = define (jndi, event.getServletContext ());

        ServletContext context = event.getServletContext ();
                       context.setAttribute (AttrRegistryWeb, registry);

        MetricReportMediator mediator;

                   mediator = new MetricReportMediator (registry, jndi (jndi), graphite ());
        reporter = mediator.reporter (context.getInitParameter (AttrReporterWeb));
    }

    @Override
    public final void contextDestroyed (ServletContextEvent sce) {
        if (reporter != null)
            reporter.stop ();

        registry = null;
        reporter = null;
    }

    final MetricRegistry define (InitialContext jndi, ServletContext context) {
        Object registry;

            registry = context.getAttribute (AttrRegistryWeb);
        if (registry instanceof MetricRegistry) return (MetricRegistry) registry;

        if (jndi != null)
            try {
                    registry = jndi.lookup (AttrRegistryJndi);
                if (registry instanceof MetricRegistry) return (MetricRegistry) registry;
            } catch (NamingException e) {
                logger.error (e.getMessage ());
            }

        return new MetricRegistry ();
    }

    final MetricReportHandler jndi (InitialContext jndi) {
        return jndi == null ? new MetricReportHandlerNoop () :
                              new MetricReportHandlerJndi (jndi);
    }

    final MetricReportHandler graphite () {
        return new MetricReportHandlerGraphite ();
    }

}
