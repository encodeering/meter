/**
 * <p>This package contains annotations to attach different types of measurement as cross cutting concerns.</p>
 *
 * <p>
 *     Name resolution of a annotated metrics works as follows:
 * </p>
 * <ul>
 *     <li>
 *         If then name starts with a #, it's considered relative to the source position of this metric, which means,
 *         the enclosing context will be used to derive the final name.
 *     </li>
 *     <li>
 *         Otherwise it's considered absolute and the value will be used without the enclosing context.
 *     </li>
 * </ul>
 *
 * <p>
 *     Example
 * </p>
 *
 * <pre>
 * <code>package my.application;
 *
 * class Main {
 *
 *     {@literal @}Counter (value = "#heavy.abc")
 *     public void heavyOp () { ... }
 *
 * }
 *
 * would be translated into 'my.application.Main.heavy.abc'
 *
 * class Main {
 *
 *     {@literal @}Counter (value = "heavy.abc")
 *     public void heavyOp () { ... }
 *
 * }
 *
 * would be translated into 'heavy.abc'
 * </code>
 * </pre>
 *
 * Date: 26.09.2014
 * Time: 13:36
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
package de.synyx.meter.core.annotation;