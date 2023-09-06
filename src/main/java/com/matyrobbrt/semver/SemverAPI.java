package com.matyrobbrt.semver;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Java bindings for working with the JavaScript <a href="https://www.npmjs.com/package/semver">semver</a> library.
 * @see #API
 */
public interface SemverAPI {
    /**
     * The instance of the API.
     */
    SemverAPI API = SemverImpl.create();

    /**
     * Tries to parse the {@code version}, returning {@code null} if it isn't valid.
     *
     * @param version the version to validate
     * @return the {@code version}, or {@code null} if it is not valid
     */
    @Nullable
    String valid(String version);

    /**
     * Tries to parse the {@code range}, returning {@code null} if it isn't valid.
     *
     * @param range the range to validate
     * @return the {@code range}, or {@code null} if it is not valid
     */
    @Nullable
    String validRange(String range);

    /**
     * Compares two semver versions.
     *
     * @param version1 the first version
     * @param version2 the second version
     * @return a negative number, {@code 0} or a positive number if the first version is smaller,
     * equal or greater than the second
     * @throws SemverException if the versions are invalid
     */
    int compare(String version1, String version2);

    /**
     * Tests if the {@code version} satisfies the given {@code range}.
     *
     * @param version the version to test
     * @param range   the range to test for
     * @return {@code true} if the {@code version} satisfies the {@code range}, or {@code false} otherwise
     * @throws SemverException if the version or the range is invalid
     */
    boolean satisfies(String version, String range);

    /**
     * Returns the release types semver supports.
     *
     * @return the release types semver supports
     */
    @Unmodifiable
    List<String> getReleaseTypes();
}
