package de.synyx.meter.metrics.reporter;

import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import de.synyx.meter.core.MeterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Long.parseLong;

/**
 * Date: 31.07.2014
 * Time: 14:40
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public abstract class MetricReportHandler {

    final String  rdefault = "20s";
    final Pattern rpattern = Pattern.compile ("^(\\d+)\\s*([a-z]{1,2})$");

    final Logger logger = LoggerFactory.getLogger (getClass ());


    /**
     * <p>Returns the scheme of this reporter.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected abstract String scheme ();

    /**
     * <p>Generates a reporter.</p>
     *
     * @param mediator specifies a {@link de.synyx.meter.metrics.reporter.MetricReportMediator} object.
     * @param provider specifies a {@link de.synyx.meter.core.MeterProvider} object.
     * @param location specifies a {@link java.net.URI} object.
     * @return a {@link com.codahale.metrics.ScheduledReporter} object.
     */
    protected abstract ScheduledReporter reporter (MetricReportMediator mediator, MeterProvider provider, URI location);


    /**
     * <p>Resolves and generates a reporter if the specified URI location matches this handler.</p>
     *
     * @param mediator specifies a {@link de.synyx.meter.metrics.reporter.MetricReportMediator} object.
     * @param provider specifies a {@link de.synyx.meter.core.MeterProvider} object.
     * @param location specifies a {@link java.net.URI} object.
     * @return a {@link com.google.common.base.Optional} object.
     */
    public final Optional<ScheduledReporter> select (MetricReportMediator mediator, MeterProvider provider, URI location) {
        String scheme;

                              scheme = scheme ();
        if (! Objects.equals (scheme, location.getScheme ())) return Optional.absent ();

        logger.info ("{} reporter detected: {} ", scheme, location);

        return Optional.fromNullable (reporter (mediator, provider, location));
    }

    /**
     * <p>Starts a reporter.</p>
     *
     * @param reporter specifies a {@link com.codahale.metrics.ScheduledReporter} object.
     * @param refresh specifies a {@link java.lang.String} object.
     * @return a {@link com.codahale.metrics.ScheduledReporter} object.
     */
    protected final ScheduledReporter start (ScheduledReporter reporter, String refresh) {
        Matcher matcher;

            matcher = rpattern.matcher (or (refresh, ""));
        if (matcher.matches ()) {
                   reporter.start (parseLong (matcher.group (1)), timeunit (matcher.group (2)));
            return reporter;
        }

        logger.warn ("{} refresh rate '{}' not usable, initializing with {} instead", scheme (), refresh, rdefault);

        return start (reporter, rdefault);
    }

    /**
     * <p>Converts a URI query into a Map of key value pairs.</p>
     *
     * @param uri specifies a {@link java.net.URI} object.
     * @return a {@link java.util.Map} object.
     */
    protected final Map<String, String> parameters (URI uri) {
        return Splitter.on ("&").omitEmptyStrings ().withKeyValueSeparator ("=").split (Objects.toString (uri.getQuery (), ""));
    }

    /**
     * <p>Returns a string representation of {@code val} or the alternation of {@code or} when undefined.</p>
     *
     * @param val specifies the {@link java.lang.String wanted} text.
     * @param or specifies an alternative content which will be used if {@code val} is undefined.
     * @return a {@link java.lang.String} object.
     */
    protected final String or (String val, String or) {
        return Objects.toString (val, or);
    }

    /**
     * <p>Converts a string into a {@link java.util.concurrent.TimeUnit} representation.</p>
     * <p>
            ns" : TimeUnit.NANOSECONDS;
            µs" : TimeUnit.MICROSECONDS;
            ms" : TimeUnit.MILLISECONDS;
            "s" : TimeUnit.SECONDS;
            "m" : TimeUnit.MINUTES;
            "h" : TimeUnit.HOURS;
            "d" : TimeUnit.DAYS;
     * </p>
     * @param timeunit specifies a {@link java.lang.String} human readable timeunit.
     * @return a {@link java.util.concurrent.TimeUnit} object.
     */
    protected final TimeUnit timeunit (String timeunit) {
        switch (timeunit) {
            case "ns" : return TimeUnit.NANOSECONDS;
            case "µs" : return TimeUnit.MICROSECONDS;
            case "ms" : return TimeUnit.MILLISECONDS;
            case  "s" : return TimeUnit.SECONDS;
            case  "m" : return TimeUnit.MINUTES;
            case  "h" : return TimeUnit.HOURS;
            case  "d" : return TimeUnit.DAYS;
        }

        throw new UnsupportedOperationException ("time-unit [" + timeunit + "] unsupported");
    }

}
