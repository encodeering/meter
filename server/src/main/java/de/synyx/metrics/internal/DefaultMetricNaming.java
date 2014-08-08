package de.synyx.metrics.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import de.synyx.metrics.MetricNaming;
import de.synyx.metrics.Substitution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

/**
 * Date: 30.07.2014
 * Time: 08:47
 */
public final class DefaultMetricNaming implements MetricNaming {

    final static Pattern Vars = Pattern.compile ("\\{([^}]+)\\}");

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    private final Substitution substitution;

    @Inject
    public DefaultMetricNaming (Substitution substitution) {
        this.substitution = substitution;
    }

    @Override
    public final String name (String proposal) {
        return Joiner.on ("").join (Iterables.transform (split (proposal), new Function<Callable<String>, String> () {

            @Override
            public final String apply (Callable<String> input) {
                try {
                    return input.call ();
                } catch (Exception e) {
                    logger.warn ("Partial defect occurred during name resolution: {} {}", e.getMessage (), e.getClass ());
                    return "$";
                }
            }

        }));
    }

    private Iterable<Callable<String>> split (String proposal) {
        List<Callable<String>> matches = new ArrayList<> ();

        int idx = 0;

        Matcher matcher;

               matcher = Vars.matcher (proposal);
        while (matcher.find ()) {
            String statictext;

                                   statictext = proposal.substring (idx, matcher.start ());
            if (!                  statictext.isEmpty ())
                matches.add (text (statictext));

            matches.add (variable (matcher.group (1)));

            idx = matcher.end ();
        }

        if (idx == 0)                 matches.add (text (proposal));
        if (idx < proposal.length ()) matches.add (text (proposal.substring (idx)));

        return matches;
    }

    private Callable<String> text (final String part) {
        return new Callable<String> () {

            @Override
            public final String call () {
                return part;
            }

        };
    }

    private Callable<String> variable (final String part) {
        return new Callable<String> () {

            @Override
            public final String call () {
                return substitution.substitute (part);
            }

        };
    }

}
