package com.matyrobbrt.semver.test;

import com.matyrobbrt.semver.SemverAPI;
import com.matyrobbrt.semver.SemverException;
import com.matyrobbrt.semver.SemverOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SemverTest {
    private static SemverAPI includePrereleases;
    private static SemverAPI loose;
    @BeforeAll
    static void init() {
        SemverAPI.API.getReleaseTypes(); // Classload semverapi
        includePrereleases = SemverAPI.getWithOptions(SemverOptions.DEFAULT.withIncludePrerelease(true));
        loose = SemverAPI.getWithOptions(SemverOptions.DEFAULT.withLoose(true));
    }

    @Test
    void testVersionsCompare() {
        Assertions.assertEquals(SemverAPI.API.compare("1.0.0", "1.2.1"), -1);
        Assertions.assertEquals(SemverAPI.API.compare("1.0.0", "1.0.0+a"), 0);
    }

    @Test
    void testVersionInRange() {
        Assertions.assertTrue(SemverAPI.API.satisfies("1.0.0+a", ">=1"));
        Assertions.assertFalse(SemverAPI.API.satisfies("2.5.5-beta", ">=2.5.5"));
    }

    @Test
    void testVersionCantParse() {
        Assertions.assertThrows(SemverException.class, () -> SemverAPI.API.compare("1", "1"));
        Assertions.assertNull(SemverAPI.API.valid("1.34."));
    }

    @Test
    void testRangeParsing() {
        Assertions.assertNull(SemverAPI.API.validRange("1.."));
        Assertions.assertNotNull(SemverAPI.API.validRange(">1"));
    }

    @Test
    void testIncludesPrereleases() {
        Assertions.assertTrue(includePrereleases.satisfies("193.1.3-snapshot+23w35a", "193.1.*"));
        Assertions.assertFalse(SemverAPI.API.satisfies("193.1.3-snapshot+23w35a", "193.1.*"));
    }

    @Test
    void testLoose() {
        Assertions.assertThrows(SemverException.class, () -> SemverAPI.API.actuallySatisfies("=1.1.0", ">=1"));
        Assertions.assertTrue(loose.actuallySatisfies("=1.1.0", ">=1"));

        Assertions.assertNotNull(loose.valid("=1.1.0"));
        Assertions.assertNull(SemverAPI.API.valid("=1.1.0"));
    }
}
