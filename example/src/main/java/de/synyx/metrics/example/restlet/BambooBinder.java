package de.synyx.metrics.example.restlet;

import de.synyx.metrics.example.service.BambooApplicationService;
import de.synyx.metrics.example.service.BambooService;
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
