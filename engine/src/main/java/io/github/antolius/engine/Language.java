package io.github.antolius.engine;

import java.net.URL;

public enum Language {

    KOTLIN_1_1(
            "io.github.antolius.engine.kotlin1.ScriptEngineSupplier",
            "/kotlin-jars/kotlin-1-module.jar"
    ),
    KOTLIN_1_2(
            "io.github.antolius.engine.kotlin2.ScriptEngineSupplier",
            "/kotlin-jars/kotlin-2-module.jar"
    ),
    KOTLIN_1_3(
            "io.github.antolius.engine.kotlin3.ScriptEngineSupplier",
            "/kotlin-jars/kotlin-3-module.jar"
    );

    private final String supplierClass;
    private final String resourceName;

    Language(String supplierClass, String resourceName) {
        this.supplierClass = supplierClass;
        this.resourceName = resourceName;
    }

    public String getSupplierClass() {
        return supplierClass;
    }

    public URL getJarURL() {
        return this.getClass().getResource(resourceName);
    }
}
