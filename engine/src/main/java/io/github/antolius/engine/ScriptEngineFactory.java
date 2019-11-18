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

public class ScriptEngineFactory {

    private final ConcurrentMap<Language, ScriptEngine> enginesCache;

    public ScriptEngineFactory() {
        enginesCache = new ConcurrentHashMap<>();
    }

    public ScriptEngine get(Language version) {
        return enginesCache.computeIfAbsent(version, this::newEngine);
    }

    private ScriptEngine newEngine(Language version) {
        URL supplierJar = version.getJarURL();
        ClassLoader classLoader = newClassLoaderWith(supplierJar);
        Supplier<ScriptEngine> supplier =
                instantiateSupplierFrom(version.getSupplierClass(), classLoader);
        return supplier.get();
    }

    private ClassLoader newClassLoaderWith(URL jarToLoad) {
        try {
            System.out.println("Loading jar from: " + jarToLoad);
            return URLClassLoader.newInstance(new URL[] {jarToLoad}, getClass().getClassLoader());
        } catch (Exception e) {
            throw new RuntimeException("Couldn't convert supplier jar file to URL", e);
        }
    }

    private Supplier<ScriptEngine> instantiateSupplierFrom(String supplierClassName,
            ClassLoader classLoader) {
        try {
            Class<?> exactSupplierClass = Class.forName(supplierClassName, true, classLoader);
            Class<? extends Supplier<ScriptEngine>> supplierClass = subclass(exactSupplierClass);
            Constructor<? extends Supplier<ScriptEngine>> constructor =
                    supplierClass.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Couldn't load " + supplierClassName + " from class loader " + classLoader, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Supplier<ScriptEngine>> subclass(Class<?> exactSupplierClass) {
        return (Class<? extends Supplier<ScriptEngine>>) exactSupplierClass
                .asSubclass(Supplier.class);
    }

}
