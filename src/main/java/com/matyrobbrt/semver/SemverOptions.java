package com.matyrobbrt.semver;

import org.graalvm.polyglot.HostAccess;

import java.util.Objects;

public class SemverOptions {
    /**
     * The default options: not loose and not including pre releases in ranges.
     */
    public static final SemverOptions DEFAULT = new SemverOptions(false, false);

    @HostAccess.Export
    public final boolean loose, includePrerelease;

    private SemverOptions(boolean loose, boolean includePrerelease) {
        this.loose = loose;
        this.includePrerelease = includePrerelease;
    }

    /**
     * @see <a href="https://github.com/npm/node-semver#functions">the node-semver docs</a>
     */
    public SemverOptions withLoose(boolean loose) {
        return new SemverOptions(loose, includePrerelease);
    }

    /**
     * @see <a href="https://github.com/npm/node-semver#functions">the node-semver docs</a>
     */
    public SemverOptions withIncludePrerelease(boolean includePrerelease) {
        return new SemverOptions(loose, includePrerelease);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemverOptions that = (SemverOptions) o;
        return loose == that.loose && includePrerelease == that.includePrerelease;
    }

    @Override
    public int hashCode() {
        return Objects.hash(loose, includePrerelease);
    }
}
