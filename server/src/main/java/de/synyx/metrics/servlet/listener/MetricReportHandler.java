package de.synyx.metrics.servlet.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
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
 */
public abstract class MetricReportHandler {

              final String  rdefault = "20s";
              final Pattern rpattern = Pattern.compile ("^(\\d+)\\s*([a-z]{1,2})$");

    protected final Logger logger = LoggerFactory.getLogger (getClass ());

    protected abstract String scheme ();

    protected abstract ScheduledReporter reporter (MetricRegistry registry, URI location);

    public final Optional<ScheduledReporter> select (MetricRegistry registry, URI location) {
        String scheme;

              scheme = scheme ();
        if (! scheme.equals (location.getScheme ())) return Optional.absent ();

        logger.info ("{} reporter detected: {} ", scheme, location);

        return Optional.fromNullable (reporter (registry, location));
    }

    protected final ScheduledReporter start (ScheduledReporter reporter, String refresh) {
        Matcher matcher;

        matcher = rpattern.matcher (or (refresh, ""));
        if (matcher.matches ()) {
            reporter.start (parseLong (matcher.group (1)), timeunit (matcher.group (2)));
            return reporter;
        }

        logger.warn ("graphite refresh rate {} invalid, using {} instead", refresh, rdefault);

        return start (reporter, rdefault);
    }

    protected final Map<String, String> parameters (String query) {
        return Splitter.on ("&").omitEmptyStrings ().withKeyValueSeparator ("=").split (query);
    }

    protected final String or (String val, String or) {
        return Objects.toString (val, or);
    }

    protected final TimeUnit timeunit (String timeunit) {
        switch (timeunit) {
            case  "d" : return TimeUnit.DAYS;
            case  "h" : return TimeUnit.HOURS;
            case  "m" : return TimeUnit.MINUTES;
            case  "s" : return TimeUnit.SECONDS;
            case "ms" : return TimeUnit.MILLISECONDS;
            case "µs" : return TimeUnit.MICROSECONDS;
            case "ns" : return TimeUnit.NANOSECONDS;
        }

        throw new UnsupportedOperationException ("Timeunit [" + timeunit + "] unsupported");
    }

}
