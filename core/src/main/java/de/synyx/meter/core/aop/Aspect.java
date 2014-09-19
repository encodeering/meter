package de.synyx.meter.core.aop;

/**
 * Date: 16.07.2014
 * Time: 08:46
 */
public interface Aspect {

    public abstract void before ();

    public abstract void after  (Object response, Throwable throwable);

}
