package org.gateway.util;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

    private static final Map<String, List<String>> VALID_PREDICATES = Map.of(
        "Path", List.of("_genkey_0"),
        "Host", List.of("_genkey_0"),
        "Method", List.of("_genkey_0"),
        "Header", List.of("header", "regexp"),
        "Query", List.of("param", "regexp"),
        "Cookie", List.of("name", "regexp"),
        "After", List.of("datetime"),
        "Before", List.of("datetime"),
        "Between", List.of("datetime1", "datetime2")
    );

    private static final Map<String, List<String>> VALID_FILTERS = Map.of(
        "AddRequestHeader", List.of("name", "value"),
        "AddResponseHeader", List.of("name", "value"),
        "RewritePath", List.of("regexp", "replacement"),
        "SetPath", List.of("template"),
        "RequestRateLimiter", List.of("redis-rate-limiter.replenishRate", "redis-rate-limiter.burstCapacity"),
        "Retry", List.of("retries", "statuses", "methods"),
        "StripPrefix", List.of("parts"),
        "SetStatus", List.of("status"),
        "RedirectTo", List.of("status", "url"),
        "ApiKeyAuth", List.of("clientId")
    );

    public boolean validatePredicate(String name, Map<String, String> args) {
        if (!VALID_PREDICATES.containsKey(name)) return false;
        return VALID_PREDICATES.get(name).stream().allMatch(args::containsKey);
    }

    public boolean validateFilter(String name, Map<String, String> args) {
        if (!VALID_FILTERS.containsKey(name)) return false;
        return VALID_FILTERS.get(name).stream().allMatch(args::containsKey);
    }
}
