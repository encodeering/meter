package de.synyx.meter.core.aspect;

import de.synyx.meter.core.aop.Aspect;

/**
 * <A>A pure NOP Adapter</A>
 *
 * Date: 16.07.2014
 * Time: 11:03
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public abstract class MeterAspectSupport implements Aspect {

    /** {@inheritDoc} */
    @Override
    public void before () {}

    /** {@inheritDoc} */
    @Override
    public void after (Object response, Throwable throwable) {}

}
