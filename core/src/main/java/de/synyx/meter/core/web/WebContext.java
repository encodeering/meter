package de.synyx.meter.core.web;

import de.synyx.meter.core.MeterProvider;

import javax.servlet.ServletContext;

/**
 * Date: 05.09.2014
 * Time: 13:44
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface WebContext extends AutoCloseable {

    /**
     * <p>initialize.</p>
     *
     * @param context a {@link javax.servlet.ServletContext} object.
     * @return a {@link de.synyx.meter.core.MeterProvider} object.
     */
    public abstract MeterProvider initialize (ServletContext context);

}
