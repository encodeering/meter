package de.synyx.meter.core;

/**
 * <p>An Injector defines a simple injection facade for a CDI injection framework.</p>
 *
 * Date: 11.08.2014
 * Time: 09:23
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface Injector {

    /**
     * <p>Creates a new instance of the specified type.<p>
     * <p>
     *    The lifecycle will be managed by underlying injection framework.
     * </p>
     *
     * @param type specifies the type.
     * @param <T> used by {@code type} to determine the generic type.
     * @return a T instance.
     */
    public abstract <T>       T create (Class<T> type);

    /**
     * <p>Injects resolved dependencies into {@code instance} determined by the underlying injection framework.</p>
     *
     * @param instance specifies the target.
     * @param <T> used by {@code instance} to determine the generic type.
     * @return a T object.
     */
    public abstract <T>       T inject (T instance);

}
