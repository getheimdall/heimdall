package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.repository.AppRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Service
public class LifeCycleService {

    @Autowired
    private AppRepository appRepository;

    private static PathMatcher pathMatcher = new AntPathMatcher();

    public boolean should(InterceptorLifeCycle interceptorLifeCycle,
                          Set<String> pathsAllowed,
                          Set<String> pathsNotAllowed,
                          String inboundURL,
                          String method,
                          HttpServletRequest req,
                          Long referenceId) {


        switch (interceptorLifeCycle) {
            case PLAN:
                return validatePlan(pathsAllowed, pathsNotAllowed, inboundURL, req, referenceId);
            case OPERATION:
                return validateOperation(pathsAllowed, pathsNotAllowed, inboundURL, method, req);
            case RESOURCE:
                return validateResource(pathsAllowed, pathsNotAllowed, inboundURL, req);
            default:
                return false;
        }

    }

    private boolean validatePlan(Set<String> pathsAllowed, Set<String> pathsNotAllowed, String inboundURL, HttpServletRequest req, Long referenceId) {

        if ((inboundURL != null && !inboundURL.isEmpty()) &&
                !isHostValidToInboundURL(req, inboundURL)) {
            return false;
        }

        if (listContainURI(req.getRequestURI(), pathsNotAllowed)) {
            return false;
        }

        if (req.getHeader("client_id") != null) {
            final App app = appRepository.findByClientId(req.getHeader("client_id"));

            final Plan plan = app.getPlans()
                    .stream()
                    .filter(p -> p.getId().equals(referenceId))
                    .findFirst()
                    .orElse(null);

            if (plan == null) return false;
        }

        if (pathsAllowed != null) {

            for (String path : pathsAllowed) {

                if (req.getRequestURI().contains(path)) return true;
            }
        }

        return false;
    }

    private boolean validateOperation(Set<String> pathsAllowed, Set<String> pathsNotAllowed, String inboundURL, String method, HttpServletRequest req) {

        if (!isMethodValidToRequest(req, method)) {

            return false;
        }

        if ((inboundURL != null && !inboundURL.isEmpty()) && !isHostValidToInboundURL(req, inboundURL)) {

            return false;
        }

        if (listContainURI(req.getRequestURI(), pathsNotAllowed)) {
            return false;
        }

        return listContainURI(req.getRequestURI(), pathsAllowed);

    }

    private boolean validateResource(Set<String> pathsAllowed, Set<String> pathsNotAllowed, String inboundURL, HttpServletRequest req) {

        if ((inboundURL != null && !inboundURL.isEmpty()) && !isHostValidToInboundURL(req, inboundURL)) {
            return false;
        }

        final String uri = req.getRequestURI();

        if (pathsNotAllowed != null) {

            for (String path : pathsNotAllowed) {

                String mutableUri = uri;
                if ((uri != null && !uri.isEmpty()) && StringUtils.endsWith(uri, "/")) {

                    mutableUri = StringUtils.removeEnd(uri.trim(), "/");
                }

                if (pathMatcher.match(path, mutableUri)) {
                    return false;
                }
            }
        }

        if (pathsNotAllowed != null) {

            for (String path : pathsAllowed) {

                String mutableUri = uri;
                if ((uri != null && !uri.isEmpty()) && StringUtils.endsWith(uri, "/")) {
                    mutableUri = StringUtils.removeEnd(uri.trim(), "/");
                }

                if (pathMatcher.match(path, mutableUri)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isHostValidToInboundURL(HttpServletRequest req, String inboundURL) {

        String host = req.getHeader("Host");
        if (host != null && host.isEmpty()) {

            host = req.getHeader("host");
        }

        if (host != null && !host.isEmpty()) {

            return (inboundURL != null && !inboundURL.isEmpty()) && inboundURL.toLowerCase().contains(host.toLowerCase());
        } else {

            return (inboundURL != null && !inboundURL.isEmpty()) && req.getRequestURL().toString().toLowerCase().contains(inboundURL.toLowerCase());
        }
    }

    private static boolean isMethodValidToRequest(HttpServletRequest req, String method) {

        if (method != null && !method.isEmpty()) {

            if (method.equals(HttpMethod.ALL.name())) {
                return true;
            }

            return req.getMethod().toLowerCase().equals(method.trim().toLowerCase());
        }

        return false;
    }

    private boolean listContainURI(String uri, Set<String> paths) {
        if (paths != null) {

            if ((uri != null && !uri.isEmpty()) && StringUtils.endsWith(uri, "/")) {

                uri = StringUtils.removeEnd(uri.trim(), "/");
            }

            for (String path : paths) {

                if (pathMatcher.match(path, uri)) {

                    return true;
                }
            }
        }

        return false;
    }
}
