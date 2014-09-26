package de.synyx.meter.example.restlet;

import de.synyx.meter.core.Measure;

/**
 * Date: 16.07.2014
 * Time: 11:49
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class BambooResponseSize implements Measure {

    /** {@inheritDoc} */
    @Override
    public final long determine (Object response, Throwable throwable) {
        if (! (response instanceof String)) return 0;

        return ((String) response).length ();
    }

}
