package com.matyrobbrt.semver;

import java.util.List;

public interface SemverAPI {
    int compare(String version1, String version2);

    boolean satisfies(String version, String range);

    List<String> getReleaseTypes();
}
