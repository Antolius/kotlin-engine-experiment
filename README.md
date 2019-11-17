# Kotlin engine experiment

An experimental Java app capable of runtime loading and executing scripts written in different, incompatible Kotlin versions.

## Use-case

Kotlin as a language provides various features that make scripting and working with DSLs extremely nice. This makes it a good candidate to base a plugin system on. In it's simplest form an application would load multiple Kotlin scripts at runtime and execute them as needed. In version `1.1` Kotlin [added `javax.script` support](https://kotlinlang.org/docs/reference/whatsnew11.html#javaxscript-support), so runtime loading and execution of scripts from Java is really simple. You can imagine loading scripts from a git repository and triggering their execution with HTTP requests and what you get is a really simple [function as a service](https://en.wikipedia.org/wiki/Function_as_a_service) platform.

With time the service gains traction and developer community forms around writing Kotlin scripts for it. Enticed by new Kotlin language features developers start requesting platform support for Kotlin versions `1.2`, and later `1.3`. On the other hand, Kotlin is not exactly known for stability of its APIs and libraries. Things break between minor version releases. You can check the current status of individual components over at [kotlinlang.org docs](https://kotlinlang.org/docs/reference/evolution/components-stability.html). This presents a problem for the platform, as it is currently running a lot of `1.1` based scripts. Migrating to `1.3` would make some eager developers happy, but would break compatibility for a lot of existing scripts.

In this experimental project I built an app capable of simultaneously running Kotlin `1.1`, `1.2` and `1.3` scripts. 

## Architecture

Project consists of 5 modules:

1. API module, with `Plugin` interface that Kotlin scripts implement and _Engine_ executes.
1. Kotlin 1 module, that depends on Kotlin `1.1.61` and exposes its `javax.script.ScriptEngine` implementation.
1. Kotlin 2 module, that depends on Kotlin `1.2.71` and exposes its `javax.script.ScriptEngine` implementation.
1. Kotlin 3 module, that depends on Kotlin `1.3.10` and exposes its `javax.script.ScriptEngine` implementation.
1. _Engine_ module, that dynamically loads those 3 `javax.script.ScriptEngine`-s in separate class loaders, enabling it to execute scripts from all Kotlin versions at the same time.

Of note is that the _Engine_ module doesn't directly depend on Kotlin 1/2/3 modules, but loads them in runtime. In order to simplify this, Kotlin 1/2/3 modules build themselves into fat jars which are packaged as resources of the _Engine_ module. This build process is configured with [maven](https://maven.apache.org/).

```
                                       +-----+
+------------------------------------->+     +<---------------------------------------+
|           +------------------------->+ api +<-------------------------+             |
|           |       depends on         |     |         depends on       |             |
|           |                          +-++--+                          |             |
|           |                            ^^                             |             |
|           |                            ||depends                      |             |
|           |                +-----------+|on                           |             |
|           |                |            |                             |             |
| +---------------------+    |  +---------------------+       +---------------------+ |
| |         |           |    |  |         |           |       |         |           | |
| | +-------+---------+ |    |  | +-------+---------+ |       | +-------+---------+ | |
| | |                 | |    |  | |                 | |       | |                 | | |
| | | kotlin-1-module | |    |  | | kotlin-2-module | |       | | kotlin-3-module | | |
| | |                 | |    |  | |                 | |       | |                 | | |
| | +-------+---------+ |    |  | +-------+---------+ |       | +-------+---------+ | |
| |         |           |    |  |         |           |       |         |           | |
| |         |depends    |    |  |         |depends    |       |         |depends    | |
| |         |on         |    |  |         |on         |       |         |on         | |
| |         v           |    |  |         v           |       |         v           | |
| | +-------+--------+  |    |  | +-------+--------+  |       | +-------+--------+  | |
| | |                |  |    |  | |                |  |       | |                |  | |
| | | Kotlin v1.1.61 |  |    |  | | Kotlin v1.2.71 |  |       | | Kotlin v1.3.10 |  | |
| | |                |  |    |  | |                |  |       | |                |  | |
| | +----------------+  |    |  | +----------------+  |       | +----------------+  | |
| |                     |    |  |                     |       |                     | |
| +---------------------+    |  +---------------------+       +---------------------+ |
| *                     *    |  *                     *       *                     * |
| * fat .jar (Kotlin 1) *    |  * fat .jar (Kotlin 2) *       * fat .jar (Kotlin 3) * |
| *                     *    |  *                     *       *                     * |
| ***********************    |  ***********************       *********************** |
|            ^               |             ^                             ^            |
|            |               +-----------+ |includes                     |            |
|            |                depends on | |as a                         |            |
|            |                           | |resource                     |            |
|            |                         +-+------+                        |            |
|            | includes as a resource  |        | includes as a resource |            |
|            +-------------------------+ engine +------------------------+            |
|                  +-------------------+        +------------------+                  |
|  loads at runtime|                   +--------+                  |loads at runtime  |
|                  |                       |loads at runtime       |                  |
|                  v                       v                       v                  |
|            +-----+------+          +-----+------+          +-----+------+           |
| implements |script_1.kts|          |script_2.kts|          |script_3.kts| implements|
+------------+// using v1 |          |// using v2 |          |// using v3 +-----------+
             |// features |          |// features |          |// features |           |
             +------------+          +-----+------+          +------------+           |
                                           |                                          |
                                           | implements                               |
                                           +------------------------------------------+
```

## Getting started

This experiment application falls short of the FaaS platform described in the _Use-case_ section. It is only capable of loading and running Kotlin scripts. Id doesn't deal wit managing scripts themselves nor with exposing them as HTTP endpoints. Also, the `Plugin` API interface is rudimentary.

However, if you wish you can build the project locally, play around with it, and even build some more sophisticated, fully featured solution on top of it. Here are instructions on doing so.

### Prerequisites

The project uses:

* [Java 8](https://openjdk.java.net/projects/jdk8/)
* [Maven](https://maven.apache.org/)

### Installation

Since this is a maven project you can build it by executing:

```bash
$ mvn clean install
```

in the project's root directory.

## Testing

_Engine_ module includes tests that load and execute actual Kotlin scripts. You can check out `io.github.antolius.engine.PluginLoaderTest` for example of various features, such as loading plugins from `.*kts` script or from `.kt` class file, or injecting dependencies into plugins. Kotlin scripts used for testing are included in the test resources directory of the _Engine_ module.

Note that if you open the test Kotlin scripts in IntelliJ IDEA you'll probably get warning about setting up maven module to use Kotlin. This is unfortunate consequence of this implementation: _engine_ module mustn't declare dependency on any one Kotlin version. It instead loads it in runtime along with Kotlin 1/2/3 modules. So IntelliJ will always raise warnings, as it cannot detect that.

You can run tests with:

```bash
$ mvn test
```

## Author

* [Josip Antoliš](https://github.com/Antolius)