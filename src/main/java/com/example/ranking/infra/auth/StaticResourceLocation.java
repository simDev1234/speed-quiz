package com.example.ranking.infra.auth;

import lombok.Getter;
import java.util.Arrays;

@Getter
public enum StaticResourceLocation {

    CSS("/css/**"),
    JAVA_SCRIPT("/js/**"),
    IMAGES("/images/**"),
    WEB_JARS("/webjars/**"),
    FAVICON("/favicon.ico", "/*/icon-*"),
    CHROME_WELL_KNOWN("/.well-known/**");

    private final String[] paths;

    StaticResourceLocation(String... paths) {
        this.paths = paths;
    }

    public static String[] getAllStaticResourcePaths(){
        return Arrays.stream(StaticResourceLocation.values())
                .flatMap(resource -> Arrays.stream(resource.getPaths()))
                .toArray(String[]::new);
    }
}
