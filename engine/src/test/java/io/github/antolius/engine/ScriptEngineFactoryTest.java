package io.github.antolius.engine;

import org.junit.Test;

import javax.script.ScriptEngine;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

public class ScriptEngineFactoryTest {

    @Test
    public void shouldLoadAllThreeScriptEngines() {
        // given
        ScriptEngineFactory givenFactory = new ScriptEngineFactory();

        // when
        List<ScriptEngine> actualEngines = Arrays.asList(
                givenFactory.get(Language.KOTLIN_1_1),
                givenFactory.get(Language.KOTLIN_1_2),
                givenFactory.get(Language.KOTLIN_1_3)
        );

        // then
        then(actualEngines)
                .extracting(engine -> engine.getFactory().getLanguageVersion())
                .containsExactly(
                        "1.1.61",
                        "1.2.71",
                        "1.3.10"
                );
    }

}