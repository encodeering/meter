package de.synyx.metrics.jersey;

import de.synyx.metrics.core.MeterProvider;
import de.synyx.metrics.core.web.WebContext;

import javax.servlet.ServletContext;

import static org.mockito.Mockito.mock;

/**
 * Date: 15.09.2014
 * Time: 10:01
 */
public class MetricFeatureTestWebContext implements WebContext {

    public final static MeterProvider Provider = mock (MeterProvider.class);

    @Override
    public MeterProvider initialize (ServletContext context) {
        return Provider;
    }

    @Override
    public void close () throws Exception {

    }
}
