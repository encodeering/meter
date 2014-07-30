package de.synyx.metrics.internal;

import de.synyx.metrics.MetricNaming;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;

import javax.inject.Inject;

/**
 * Date: 30.07.2014
 * Time: 08:47
 */
public final class DefaultMetricNaming implements MetricNaming {

    private final ServiceLocator locator;
    private final MultivaluedParameterExtractorProvider extractor;

    @Inject
    public DefaultMetricNaming (ServiceLocator locator, MultivaluedParameterExtractorProvider extractor) {
        this.locator   = locator;
        this.extractor = extractor;
    }

    @Override
    public final String name (String proposal) {
        return proposal;
    }

}
