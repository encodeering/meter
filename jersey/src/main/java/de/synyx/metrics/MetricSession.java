package de.synyx.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import de.synyx.metrics.core.reporter.MetricReportHandler;
import de.synyx.metrics.core.reporter.MetricReportHandlerConsole;
import de.synyx.metrics.core.reporter.MetricReportHandlerCsv;
import de.synyx.metrics.core.reporter.MetricReportHandlerGanglia;
import de.synyx.metrics.core.reporter.MetricReportHandlerGraphite;
import de.synyx.metrics.core.reporter.MetricReportHandlerJndi;
import de.synyx.metrics.core.reporter.MetricReportHandlerLog;
import de.synyx.metrics.core.reporter.MetricReportHandlerNoop;
import de.synyx.metrics.core.reporter.MetricReportMediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

/**
 * Date: 31.07.2014
 * Time: 09:15
 */
final class MetricSession {

    public final static String AttrReporterWeb = "com.codahale.metrics.reporter";
    public final static String AttrRegistryWeb = "com.codahale.metrics.servlet.InstrumentedFilter.registry";

    public final static String AttrRegistryJndi = "java:comp/env/metrics/registry";

    private final Logger logger = LoggerFactory.getLogger (MetricSession.class);

    private final ServletContext context;

    private MetricRegistry registry;

    private ScheduledReporter reporter;

    MetricSession (ServletContext context) {
        this.context = context;
    }

    final MetricRegistry initialize () {
        Context jndi = null;

        try {
            jndi = new InitialContext ();
        } catch (NamingException e) {
            logger.error (e.getMessage ());
        }

                                               registry = define (jndi, context);
        context.setAttribute (AttrRegistryWeb, registry);

        MetricReportMediator mediator = new MetricReportMediator (registry, csv      (),
                                                                            console  (),
                                                                            logging  (),
                                                                            jndi     (jndi),
                                                                            ganglia  (),
                                                                            graphite ());

        reporter = mediator.reporter (context.getInitParameter (AttrReporterWeb));

        return registry;
    }

    final void close () {
        if (reporter != null)
            reporter.stop ();

        registry = null;
        reporter = null;
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
                logger.error ("jndi lookup failed with {}", e.getMessage ());
            }

        logger.info ("metric registry could not be resolved (session, jndi), creating a new registry");

        return new MetricRegistry ();
    }

    final MetricReportHandler csv () {
        return new MetricReportHandlerCsv ();
    }

    final MetricReportHandler console () {
        return new MetricReportHandlerConsole ();
    }

    final MetricReportHandler logging () {
        return new MetricReportHandlerLog ();
    }

    final MetricReportHandler jndi (Context jndi) {
        return jndi == null ? new MetricReportHandlerNoop () :
                              new MetricReportHandlerJndi (jndi);
    }

    /* a decoupling event mechanism or a spi provider for the mediator [auto-discovery] would be than try-load-then */

    final MetricReportHandler graphite () {
        return loadable ("com.codahale.metrics.graphite.GraphiteReporter") ? new MetricReportHandlerGraphite () :
                                                                             new MetricReportHandlerNoop ();
    }

    final MetricReportHandler ganglia () {
        return loadable ("com.codahale.metrics.ganglia.GangliaReporter") ? new MetricReportHandlerGanglia () :
                                                                           new MetricReportHandlerNoop ();
    }

    private boolean loadable (String type) {
        try {
            Class.forName (type, false, getClass ().getClassLoader ());

            return true;
        } catch (ClassNotFoundException e) {
            logger.warn ("reporter {} not available", type);
        }

        return false;
    }

}
