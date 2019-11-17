package io.github.antolius.engine;

import io.github.antolius.engine.api.Plugin;
import io.github.antolius.engine.api.Printer;
import io.github.antolius.engine.api.PrinterAware;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;

class PluginLoader {

    private final ScriptEngineFactory scriptEngineFactory;
    private final Printer printer;

    PluginLoader(ScriptEngineFactory scriptEngineFactory, Printer printer) {
        this.scriptEngineFactory = scriptEngineFactory;
        this.printer = printer;
    }

    Plugin load(File sourceFile, Language kotlinVersion) {
        Reader sourceFileReader = readerFrom(sourceFile);
        ScriptEngine scriptEngine = scriptEngineFactory.get(kotlinVersion);
        Plugin plugin = runScript(sourceFileReader, scriptEngine);
        if (plugin == null) {
            plugin = tryInstantiating(sourceFile.getName(), scriptEngine);
        }
        autowirePrinter(plugin);
        return plugin;
    }

    private Reader readerFrom(File sourceFile) {
        try {
            FileInputStream stream = new FileInputStream(sourceFile);
            return new InputStreamReader(stream, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Couldn't create a reader for source file " + sourceFile, e);
        }
    }

    private Plugin runScript(Reader sourceFileReader, ScriptEngine scriptEngine) {
        try {
            Object result = scriptEngine.eval(sourceFileReader);
            return (Plugin) result;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't evaluate script to a Plugin instance", e);
        }
    }

    private Plugin tryInstantiating(String filename, ScriptEngine scriptEngine) {
        String className = filename
                .replace(".kts", "")
                .replace(".kt", "");
        try {
            Object result = scriptEngine.eval(className + "()");
            return (Plugin) result;
        } catch (ScriptException e) {
            throw new RuntimeException("Couldn't instantiate " + className, e);
        }
    }

    private void autowirePrinter(Plugin plugin) {
        if (PrinterAware.class.isAssignableFrom(plugin.getClass())) {
            PrinterAware aware = (PrinterAware) plugin;
            aware.set(printer);
        }
    }

}
