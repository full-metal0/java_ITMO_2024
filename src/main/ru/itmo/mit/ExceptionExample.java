package ru.itmo.mit;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ExceptionExample implements Handler {
    public static void main(String[] args) {
        new ExceptionExample().handle();
    }

    @Override
    public void handle() {
        byte[] bytes;
        try {
            bytes = readFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
            // getCause() == e
        }
        throw new ConcurrentModificationException("");
        // work with bytes
    }

    public static void runAll() {
        List<Exception> failedExceptions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            try {
                readFromFile();
                // some work
            } catch (Exception e) {
                failedExceptions.add(e);
            }
        }
        if (!failedExceptions.isEmpty()) {
            RuntimeException exception = new RuntimeException();
            for (Exception failedException : failedExceptions) {
                exception.addSuppressed(failedException);
            }
            throw exception;
        }
    }

    public static byte[] readFromFile() throws IOException {
        FileInputStream stream = new FileInputStream("a.txt");
        return stream.readAllBytes();
    }
}

interface Handler {
    void handle();
}