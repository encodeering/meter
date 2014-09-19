package de.synyx.meter.core;

/**
 * Date: 11.08.2014
 * Time: 09:23
 */
public interface Injector {

    public abstract <T>       T create (Class<T> type);

    public abstract <T>       T inject (T instance);

}