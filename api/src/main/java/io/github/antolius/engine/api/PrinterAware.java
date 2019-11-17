package io.github.antolius.engine.api;

import org.jetbrains.annotations.NotNull;

public interface PrinterAware {
    void set(@NotNull Printer printer);
}
