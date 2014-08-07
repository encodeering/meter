package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.naming.Context;
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

    public final static String AttrReporterWeb = "com.codahale.metrics.reporter";
    public final static String AttrRegistryWeb = "com.codahale.metrics.servlet.InstrumentedFilter.registry";

    public final static String AttrRegistryJndi = "java:comp/env/metrics/registry";

    private final static Logger Logger = LoggerFactory.getLogger (MetricListener.class);

    private final static AtomicBoolean invoked = new AtomicBoolean (false);

    private MetricRegistry registry;

    private ScheduledReporter reporter;

    public final static Optional<ServletContextListener> ifNotCalled () {
        if (invoked.get ()) return Optional.absent ();
        else
            return Optional.of ((ServletContextListener) new MetricListener ());
    }

    @Override
    public final void contextInitialized (ServletContextEvent event) {
        try {
            Context jndi = null;

            try {
                jndi = new InitialContext ();
            } catch (NamingException e) {
                Logger.error (e.getMessage ());
            }

            registry = define (jndi, event.getServletContext ());

            ServletContext context = event.getServletContext ();
                           context.setAttribute (AttrRegistryWeb, registry);

            MetricReportMediator mediator;

                       mediator = new MetricReportMediator (registry, jndi (jndi), graphite ());
            reporter = mediator.reporter (context.getInitParameter (AttrReporterWeb));
        } finally {
            invoked.set (true);
        }
    }

    @Override
    public final void contextDestroyed (ServletContextEvent sce) {
        try {
            if (reporter != null)
                reporter.stop ();

            registry = null;
            reporter = null;
        } finally {
            invoked.compareAndSet (true, false);
        }
    }

    final MetricRegistry define (Context jndi, ServletContext context) {
        Object registry;

            registry = context.getAttribute (AttrRegistryWeb);
        if (registry instanceof MetricRegistry) return (MetricRegistry) registry;

        if (jndi != null)
            try {
                    registry = jndi.lookup (AttrRegistryJndi);
                if (registry instanceof MetricRegistry) return (MetricRegistry) registry;
            } catch (NamingException e) {
                Logger.error (e.getMessage ());
            }

        return new MetricRegistry ();
    }

    final MetricReportHandler jndi (Context jndi) {
        return jndi == null ? new MetricReportHandlerNoop () :
                              new MetricReportHandlerJndi (jndi);
    }

    final MetricReportHandler graphite () {
        return new MetricReportHandlerGraphite ();
    }

}
