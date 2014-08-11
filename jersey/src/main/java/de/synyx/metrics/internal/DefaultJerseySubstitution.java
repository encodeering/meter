package de.synyx.metrics.internal;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import de.synyx.metrics.core.Substitution;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static com.google.common.collect.Iterables.concat;
import static java.util.Collections.singleton;

/**
 * Date: 30.07.2014
 * Time: 11:09
 */
public final class DefaultJerseySubstitution implements Substitution {

    private final Logger logger = LoggerFactory.getLogger (getClass ());

    private final HttpHeaders headers;

    private final ExtendedUriInfo uri;

    private final MultivaluedParameterExtractorProvider extractors;

    @Inject
    public DefaultJerseySubstitution (HttpHeaders headers, ExtendedUriInfo uri, MultivaluedParameterExtractorProvider extractors) {
        this.headers    = headers;
        this.uri        = uri;
        this.extractors = extractors;
    }

    @Override
    public final String substitute (String value) {
        for (Parameter                 parameter : parameters ()) {
            if (Objects.equals (value, parameter.getSourceName ()))
                return substitute     (parameter);
        }

        logger.warn ("Couldn't resolve variable: {}", value);

        return value;
    }

    private String substitute (Parameter parameter) {
        MultivaluedParameterExtractor<?> extractor;

                                                 extractor = extractors.get (parameter);
        String defaultstring = Objects.toString (extractor.getDefaultValueString (), "");

             if (parameter.isAnnotationPresent (PathParam.class))   return Objects.toString (extractor.extract (segments (parameter.isEncoded ())), defaultstring);
        else if (parameter.isAnnotationPresent (QueryParam.class))  return Objects.toString (extractor.extract (queries  (parameter.isEncoded ())), defaultstring);
        else if (parameter.isAnnotationPresent (HeaderParam.class)) return Objects.toString (extractor.extract (headers  ()),                       defaultstring);
        else if (parameter.isAnnotationPresent (CookieParam.class)) return Objects.toString (extractor.extract (cookies  ()),                       defaultstring);
        else
            return defaultstring;
    }

    private MultivaluedMap<String, String> segments (boolean decode) {
        return uri.getPathParameters  (decode);
    }

    private MultivaluedMap<String, String> queries  (boolean decode) {
        return uri.getQueryParameters (decode);
    }

    private MultivaluedMap<String, String> headers () {
        return headers.getRequestHeaders ();
    }

    private MultivaluedMap<String, String> cookies () {
        return new MultivaluedHashMap<> (Maps.transformEntries (headers.getCookies (), new Maps.EntryTransformer<String, Cookie, String> () {

            @Override
            public final String transformEntry (String key, Cookie cookie) {
                return cookie.getValue ();
            }

        }));
    }

    private Iterable<Parameter> parameters () {
        return concat (FluentIterable.from (
                               concat (singleton (uri.getMatchedResourceMethod   ()),
                                                  uri.getMatchedResourceLocators ())
                       ).transform (extract ())
        );
    }

    final Function<ResourceMethod, List<Parameter>> extract () {
        return new Function<ResourceMethod, List<Parameter>> () {

            @Override
            public final List<Parameter> apply (ResourceMethod resource) {
                return resource != null ?
                       resource.getInvocable ().getParameters () : Collections.<Parameter>emptyList ();
            }

        };
    }


}
