package com.matyrobbrt.semver;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class SemverImpl {
    static final Map<SemverOptions, SemverAPI> APIS = new ConcurrentHashMap<>();

    static SemverAPI create(SemverOptions options) {
        return APIS.computeIfAbsent(options, k -> {
            try (final InputStreamReader reader = new InputStreamReader(SemverImpl.class.getResourceAsStream("/semver.js"))) {
                final Context context = Context.newBuilder("js", "regex")
                        .allowNativeAccess(false)
                        .allowIO(false)
                        .allowCreateProcess(false)
                        .allowEnvironmentAccess(EnvironmentAccess.NONE)
                        .allowHostClassLoading(false)
                        .allowValueSharing(true)
                        .allowHostAccess(HostAccess.newBuilder()
                                .allowArrayAccess(true)
                                .allowMapAccess(true)
                                .allowListAccess(true)
                                .allowAccessAnnotatedBy(HostAccess.Export.class)
                                .build())
                        .engine(Engine.newBuilder()
                                .option("engine.WarnInterpreterOnly", "false").build())
                        .build();
                final Module module = new Module();
                context.getBindings("js").putMember("__dirname", "/dev/null");
                context.getBindings("js").putMember("module", module);
                context.eval(Source.newBuilder("js", reader, "index.js")
                        .mimeType("application/javascript+module").build());
                return createAPI(module, options);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    private static SemverAPI createAPI(Module module, SemverOptions options) {
        final VarargFunction<Integer> compare = module.getAsFunction("compare");
        final VarargFunction<Boolean> satisfies = module.getAsFunction("satisfies");
        final VarargFunction<@Nullable String> valid = module.getAsFunction("valid");
        final VarargFunction<@Nullable String> validRange = module.getAsFunction("validRange");

        final List<String> releaseTypes = Collections.unmodifiableList((List<String>) module.exports.get("RELEASE_TYPES"));

        return new SemverAPI() {
            @Override
            public @Nullable String valid(String version) {
                return valid.apply(version, options);
            }

            @Override
            public @Nullable String validRange(String range) {
                return validRange.apply(range, options);
            }

            @Override
            public int compare(String version1, String version2) {
                return compare.apply(version1, version2, options);
            }

            @Override
            public boolean satisfies(String version, String range) {
                return satisfies.apply(version, range, options);
            }

            @Override
            public List<String> getReleaseTypes() {
                return releaseTypes;
            }
        };
    }

    public static class Module {
        @HostAccess.Export
        public Map<String, Object> exports;

        public <R> VarargFunction<R> getAsFunction(String str) {
            final Function<Object[], Object> func = (Function<Object[], Object>) exports.get(str);
            return args -> {
                try {
                    return (R) func.apply(args);
                } catch (PolyglotException exception) {
                    throw new SemverException(exception.getMessage().replace("TypeError: ", ""));
                }
            };
        }
    }

    private interface VarargFunction<R> {
        R apply(Object... args);
    }
}