package io.github.antolius.example;

import java.io.File;
import io.github.antolius.engine.Language;
import io.github.antolius.engine.PluginLoader;
import io.github.antolius.engine.ScriptEngineFactory;
import io.github.antolius.engine.api.Plugin;
import io.github.antolius.engine.api.Printer;
import io.github.antolius.engine.api.Request;
import io.github.antolius.engine.api.Response;

public class Main {

    /*
        Demonstrates the usage of Plugin loading and execution.
        Run it with 3 arguments:
        1. path to a Kotlin script with Plugin implementation
        2. version of kotlin that the script is using
        3. input that will be passed on to the script Plugin

        The result of running the PLugin will be passed on
        to the standard out.
    */
    public static void main(String[] args) {
        ScriptEngineFactory factory = new ScriptEngineFactory();
        Printer stdOutPrinter = System.out::println;
        PluginLoader loader = new PluginLoader(factory, stdOutPrinter);

        File scriptFile = new File(args[0]);
        Language scriptLanguage = Language.valueOf(args[1]);
        Plugin plugin = loader.load(scriptFile, scriptLanguage);

        Request request = new Request(args[2]);
        Response response = plugin.process(request);
        System.out.println(response.getData());
    }
}
