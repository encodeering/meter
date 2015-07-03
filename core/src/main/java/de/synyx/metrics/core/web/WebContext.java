package de.synyx.metrics.core.web;

import de.synyx.metrics.core.MeterProvider;

import javax.servlet.ServletContext;

/**
 * Date: 05.09.2014
 * Time: 13:44
 */
public interface WebContext extends AutoCloseable {

    public abstract MeterProvider initialize (ServletContext context);

}
