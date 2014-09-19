package de.synyx.meter.core.web;

import de.synyx.meter.core.MeterProvider;

import javax.servlet.ServletContext;

/**
 * Date: 05.09.2014
 * Time: 13:44
 */
public interface WebContext extends AutoCloseable {

    public abstract MeterProvider initialize (ServletContext context);

}
