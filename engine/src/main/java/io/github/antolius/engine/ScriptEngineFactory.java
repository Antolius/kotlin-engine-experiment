package io.github.antolius.engine;

import javax.script.ScriptEngine;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

class ScriptEngineFactory {

    private final ConcurrentMap<Language, ScriptEngine> enginesCache;

    ScriptEngineFactory() {
        enginesCache = new ConcurrentHashMap<>();
    }

    ScriptEngine get(Language version) {
        return enginesCache.computeIfAbsent(version, this::newEngine);
    }

    private ScriptEngine newEngine(Language version) {
        File supplierJar = findSupplierJarFor(version.getJarFile());
        ClassLoader classLoader = newClassLoaderWith(supplierJar);
        Supplier<ScriptEngine> supplier = instantiateSupplierFrom(version.getSupplierClass(), classLoader);
        return supplier.get();
    }

    private File findSupplierJarFor(String filePath) {
        File jarFile = new File(filePath);
        if (!jarFile.exists() || !jarFile.canRead()) {
            throw new RuntimeException("Couldn't find readable supplier jar file at " + filePath);
        }
        return jarFile;
    }

    private ClassLoader newClassLoaderWith(File jarToLoad) {
        try {
            return URLClassLoader.newInstance(
                    new URL[]{jarToLoad.toURI().toURL()},
                    getClass().getClassLoader()
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException("Couldn't convert supplier jar file to URL", e);
        }
    }

    private Supplier<ScriptEngine> instantiateSupplierFrom(String supplierClassName, ClassLoader classLoader) {
        try {
            Class<?> exactSupplierClass = Class.forName(supplierClassName, true, classLoader);
            Class<? extends Supplier<ScriptEngine>> supplierClass = subclass(exactSupplierClass);
            Constructor<? extends Supplier<ScriptEngine>> constructor = supplierClass.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load " + supplierClassName + " from class loader " + classLoader, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Supplier<ScriptEngine>> subclass(Class<?> exactSupplierClass) {
        return (Class<? extends Supplier<ScriptEngine>>) exactSupplierClass.asSubclass(Supplier.class);
    }

}
