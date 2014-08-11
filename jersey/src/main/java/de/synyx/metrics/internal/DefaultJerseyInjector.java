package de.synyx.metrics.internal;

import de.synyx.metrics.core.Injector;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;

/**
 * Date: 11.08.2014
 * Time: 09:52
 */
public final class DefaultJerseyInjector implements Injector {

    private final ServiceLocator locator;

    @Inject
    public DefaultJerseyInjector (ServiceLocator locator) {
        this.locator = locator;
    }

    @Override
    public <T> T create (Class<T> type) {
        return locator.create (type);
    }

    @Override
    public <T> T inject (T instance) {
        locator.inject (instance);

        return instance;
    }

}
