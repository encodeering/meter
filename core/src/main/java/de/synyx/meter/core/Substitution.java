package de.synyx.meter.core;

/**
 * <p>A substitution allows the modification of inline markup</p>
 *
 * Date: 30.07.2014
 * Time: 11:07
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface Substitution {

    /**
     * <p>Substitutes inline markup with another value.</p>
     * <p>
     *     The implementer is responsible to define valid markup keys and and resources, which are gathered during the process.
     *     A key shall remain as-is, if it can't be substituted by the running process.
     * </p>
     *
     * @param value specifies a string containing markup keys.
     * @return a substituted string
     */
    public abstract String substitute (String value);

}
