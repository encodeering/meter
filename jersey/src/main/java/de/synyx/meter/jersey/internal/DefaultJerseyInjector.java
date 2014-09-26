package de.synyx.meter.jersey.internal;

import de.synyx.meter.core.Injector;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;

/**
 * Date: 11.08.2014
 * Time: 09:52
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class DefaultJerseyInjector implements Injector {

    private final ServiceLocator locator;

    /**
     * <p>Constructor for DefaultJerseyInjector.</p>
     *
     * @param locator a {@link org.glassfish.hk2.api.ServiceLocator} object.
     */
    @Inject
    public DefaultJerseyInjector (ServiceLocator locator) {
        this.locator = locator;
    }

    /** {@inheritDoc} */
    @Override
    public <T> T create (Class<T> type) {
        return locator.create (type);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T inject (T instance) {
        locator.inject (instance);

        return instance;
    }

}
