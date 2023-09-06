package com.matyrobbrt.semver;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SemverImpl {
    public static void main(String[] args) throws Exception {
        try (final InputStreamReader reader = new InputStreamReader(SemverImpl.class.getResourceAsStream("/semver.js"));
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
                     .build()) {
            final Module module = new Module();
            context.getBindings("js").putMember("__dirname", "/dev/null");
            context.getBindings("js").putMember("module", module);
            context.eval(Source.newBuilder("js", reader, "index.js")
                    .mimeType("application/javascript+module").build());
            final var api = createAPI(module);

            System.out.println(api.compare("1.2.4", "1.2.4+a"));
            System.out.println(api.satisfies("1.0.0", ">=1.0.0"));
            System.out.println(api.getReleaseTypes());
        }
    }

    private static SemverAPI createAPI(Module module) {
        final VarargFunction<Integer> compare = module.getAsFunction("compare");
        final VarargFunction<Boolean> satisfies = module.getAsFunction("satisfies");

        final List<String> releaseTypes = (List<String>) module.exports.get("RELEASE_TYPES");

        return new SemverAPI() {
            @Override
            public int compare(String version1, String version2) {
                return compare.apply(version1, version2);
            }

            @Override
            public boolean satisfies(String version, String range) {
                return satisfies.apply(version, range);
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
            return args -> (R) func.apply(args);
        }
    }

    private interface VarargFunction<R> {
        R apply(Object... args);
    }
}