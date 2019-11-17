package io.github.antolius.engine.api;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Request {
    private final String data;

    public Request(@NotNull String data) {
        this.data = data;
    }

    @NotNull
    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(data, request.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "Request{" +
                "data='" + data + '\'' +
                '}';
    }
}
