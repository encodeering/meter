package de.synyx.meter.core.annotation;

/**
 * <p>Determines the kind of events a measure is interested in.</p>
 *
 * Date: 28.07.2014
 * Time: 15:31
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public enum Kind {

    /**
     * <p>Measures intercepted calls only, if they did not throw an exception.</p>
     * <p>
     *     Returning {@code null} will not be considered an exception.
     * </p>
     */
    Success,

    /**
     * <p>Measures intercepted calls only, if they did throw an exception</p>
     * <p>
     *     Returning {@code null} will not be considered an exception.
     * </p>
     */
    Error,

    /**
     * <p>Measures all intercepted calls, regardless if they succeeded or failed.</p>
     */
    Both

}
