package io.github.antolius.engine.api;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Response {
    private final String data;

    public Response(@NotNull String data) {
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
        Response response = (Response) o;
        return Objects.equals(data, response.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "Response{" +
                "data='" + data + '\'' +
                '}';
    }
}
