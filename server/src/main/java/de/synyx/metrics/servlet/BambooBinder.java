package de.synyx.metrics.servlet;

import de.synyx.metrics.service.BambooApplicationService;
import de.synyx.metrics.service.BambooService;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.ws.rs.ext.Provider;


/**
 * @author  Michael Clausen - clausen@synyx.de
 */
@Provider
class BambooBinder extends AbstractBinder {

    @Override
    protected final void configure () {
        bind (BambooApplicationService.class).to (new TypeLiteral<BambooService<String>> () {});
    }

}
