package de.synyx.meter.core.aspect;

import de.synyx.meter.core.aop.Aspect;

/**
* Date: 16.07.2014
* Time: 11:03
*/
public abstract class MeterAspectSupport implements Aspect {

    @Override
    public void before () {}

    @Override
    public void after (Object response, Throwable throwable) {}

}
