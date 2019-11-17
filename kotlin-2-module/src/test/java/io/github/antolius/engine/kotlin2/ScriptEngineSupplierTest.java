package io.github.antolius.engine.kotlin2;

import org.junit.Test;

import javax.script.ScriptEngine;
import java.util.function.Supplier;

import static org.assertj.core.api.BDDAssertions.then;

public class ScriptEngineSupplierTest {

    @Test
    public void shouldExposeEngineInfo() {
        // given
        Supplier<ScriptEngine> givenSupplier = new ScriptEngineSupplier();

        // when
        ScriptEngine actualEngine = givenSupplier.get();

        // then
        then(actualEngine.getFactory().getLanguageName()).isEqualTo("kotlin");
        then(actualEngine.getFactory().getLanguageVersion()).isEqualTo("1.2.71");
    }
}
