package de.synyx.meter.example.service;

/**
 * Date: 30.07.2014
 * Time: 08:21
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface BambooService<T> {

    /**
     * <p>call.</p>
     *
     * @param input a T object.
     * @return a {@link java.lang.String} object.
     */
    public abstract String call (T input);

}
