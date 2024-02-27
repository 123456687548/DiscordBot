package eu.time.discordbot.crypto;

import java.util.List;

public class CryptoComRequest<T> {
    private int id;
    private String method;
    private int code;
    private Result<T> result;

    public int getId() {
        return id;
    }

    public String getMethod() {
        return method;
    }

    public int getCode() {
        return code;
    }

    public Result<T> getResult() {
        return result;
    }

    public static class Result<B> {
        private List<B> data;

        public Result() {
        }

        public List<B> getData() {
            return data;
        }
    }
}
