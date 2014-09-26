package de.synyx.meter.core.aop;

/**
 * <p>An aspect allows the specification of a cross cutting concern, which is executed before <b>and</b> after the real invocation.</p>
 *
 * Date: 16.07.2014
 * Time: 08:46
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public interface Aspect {

    /**
     * <p>Defines a hook to execute code beforehand.</p>
     * <p>
     *     Any runtime-exception thrown will be logged and discarded silently.
     * </p>
     */
    public abstract void before ();

    /**
     * <p>Defines a hook to execute code afterwards.</p>
     * <p>
     *     Any runtime-exception thrown will be logged and discarded silently.
     * </p>
     *
     * @param response specifies the {@link java.lang.Object result} from the real invocation.
     * @param throwable specifies the {@link java.lang.Throwable exception} from the real or runtime invocation (checked only).
     */
    public abstract void after  (Object response, Throwable throwable);

}
