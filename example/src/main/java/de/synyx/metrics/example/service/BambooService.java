package de.synyx.metrics.example.service;

/**
 * Date: 30.07.2014
 * Time: 08:21
 */
public interface BambooService<T> {

    public abstract String call (T input);

}
