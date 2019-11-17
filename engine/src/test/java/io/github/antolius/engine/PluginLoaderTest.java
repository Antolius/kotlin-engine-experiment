package io.github.antolius.engine;

import io.github.antolius.engine.api.Printer;
import io.github.antolius.engine.api.Request;
import io.github.antolius.engine.api.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class PluginLoaderTest {

    @Mock
    private Printer printer;
    private PluginLoader pluginLoader;

    @Before
    public void setUp() {
        ScriptEngineFactory givenFactory = new ScriptEngineFactory();
        pluginLoader = new PluginLoader(givenFactory, printer);
    }

    @Test
    public void shouldLoadPluginFromKotlinScript() {
        // given
        File givenScript = givenScriptFile("scripts/simple_plugin.kts");

        // when
        List<Response> actualResponses = Arrays.stream(Language.values())
                .map(lang -> pluginLoader.load(givenScript, lang))
                .map(plugin -> plugin.process(new Request("Hello from Kotlin version")))
                .collect(Collectors.toList());


        // then
        then(actualResponses)
                .extracting(Response::getData)
                .containsExactly(
                        "Hello from Kotlin version 1.1.60",
                        "Hello from Kotlin version 1.2.71",
                        "Hello from Kotlin version 1.3.10"
                );
    }

    @Test
    public void shouldLoadPluginFromKotlinClass() {
        // given
        File givenScript = givenScriptFile("scripts/EchoPlugin.kt");

        // when
        AtomicInteger i = new AtomicInteger(1);
        List<Response> actualResponses = Arrays.stream(Language.values())
                .map(lang -> pluginLoader.load(givenScript, lang))
                .map(plugin -> plugin.process(new Request("Echo line " + i.getAndIncrement())))
                .collect(Collectors.toList());

        // then
        then(actualResponses)
                .extracting(Response::getData)
                .containsExactly(
                        "Echo line 1",
                        "Echo line 2",
                        "Echo line 3"
                );
    }

    @Test
    public void shouldAutowirePluginDependencies() {
        // given
        File givenScript = givenScriptFile("scripts/printing_plugin.kts");

        // when
        AtomicInteger i = new AtomicInteger(1);
        Arrays.stream(Language.values())
                .map(lang -> pluginLoader.load(givenScript, lang))
                .forEach(plugin -> plugin.process(new Request("Print line " + i.getAndIncrement())));

        // then
        ArgumentCaptor<String> lineCaptor = ArgumentCaptor.forClass(String.class);
        BDDMockito.then(printer).should(times(3)).print(lineCaptor.capture());
        then(lineCaptor.getAllValues())
                .containsExactly(
                        "Print line 1",
                        "Print line 2",
                        "Print line 3"
                );
    }

    private File givenScriptFile(String resourceName) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        return new File(Objects.requireNonNull(resource).getFile());
    }
}
