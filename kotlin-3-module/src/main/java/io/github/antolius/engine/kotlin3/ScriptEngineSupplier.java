package io.github.antolius.engine.kotlin3;

import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory;

import javax.script.ScriptEngine;
import java.util.function.Supplier;

public class ScriptEngineSupplier implements Supplier<ScriptEngine> {

    @Override
    public ScriptEngine get() {
        return new KotlinJsr223JvmLocalScriptEngineFactory().getScriptEngine();
    }
}
