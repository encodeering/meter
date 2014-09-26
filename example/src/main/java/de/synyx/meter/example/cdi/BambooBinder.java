package de.synyx.meter.example.cdi;

import de.synyx.meter.example.service.BambooApplicationService;
import de.synyx.meter.example.service.BambooService;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.ws.rs.ext.Provider;


/**
 * @author  Michael Clausen - clausen@synyx.de
 */
@Provider
/**
 * <p>BambooBinder class.</p>
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public class BambooBinder extends AbstractBinder {

    /** {@inheritDoc} */
    @Override
    protected final void configure () {
        bind (BambooApplicationService.class).to (new TypeLiteral<BambooService<String>> () {});
    }

}
