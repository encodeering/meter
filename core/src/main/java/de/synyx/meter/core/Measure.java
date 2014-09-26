package de.synyx.meter.core;

/**
 * <p>A measure defines a mechanism or algorithm to derive a discrete measurement point from a source or event.</p>
 *
 * Date: 16.07.2014
 * Time: 13:04
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface Measure {

    /**
     * <p>Determines a discrete measurement point by either using the provided arguments or any other source or both.</p>
     *
     * @param response specifies the {@link java.lang.Object result} of the observed service call.
     * @param throwable specifies the {@link java.lang.Throwable exception} of the observed service call.
     * @return a long.
     */
    public abstract long determine (Object response, Throwable throwable);

}
