package io.playqd.mini.controller.navigator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record ItemPath(String value, List<String> pathVariables, Map<String, String> queryParams) {

    public ItemPath(String path) {
        this(path, List.of(), Map.of());
    }

    public ItemPath(String p, List<String> pathVariables) {
        this(p, Collections.emptyMap(), pathVariables.toArray(String[]::new));
    }

    public ItemPath(String p, Map<String, String> queryParams, String ... pathVariables) {
        var path = p;
        var pathVars = Collections.<String>emptyList();
        if (pathVariables != null && pathVariables.length > 0) {
            path = String.format(path, (Object[])pathVariables);
            pathVars = Arrays.asList(pathVariables);
        }
        if (queryParams != null && !queryParams.isEmpty()) {
            path += "?";
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (path.endsWith("?")) {
                    path += k + "=" + v;
                } else {
                    path += "&" + k + "=" + v;
                }
            }
        }

        this(path, pathVars, queryParams);
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }

    public static ItemPath empty() {
        return new ItemPath("", Collections.emptyList(), Collections.emptyMap());
    }

}
