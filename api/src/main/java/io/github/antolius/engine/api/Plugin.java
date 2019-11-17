package io.github.antolius.engine.api;

import org.jetbrains.annotations.NotNull;

public interface Plugin {
    @NotNull
    Response process(@NotNull Request req);
}
