# semver-graaljs
Java bindings of the NPM semver library using GraalJS

## Installing
semver-graaljs can be found on Maven Central at `com.matyrobbrt:semver-graaljs`

## Using
You can interact with the semver library using the `com.matyrobbrt.semver.SemverAPI` interface.  
Example:
```java
SemverAPI.API.compare("1.0.0", "1.0.1") // -1
SemverAPI.API.satisfies("1.0.0", ">1.0.0") // false
SemverAPI.API.valid("1.0") // null
```