package ru.itmo.mit.streams;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    public static List<String> findQuotes(List<String> paths, CharSequence sequence) throws IOException {
        return paths.stream()
                .flatMap(path -> {
                    try {
                        return Files.lines(Paths.get(path));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .filter(line -> line.contains(sequence))
                .collect(Collectors.toList());
    }

    public static double piDividedBy4() {
        return piDividedBy4(10000);
    }

    public static double piDividedBy4(int n) {
        Random random = new Random();
        long hits = IntStream.range(0, n)
                .filter(i -> {
                    double x = random.nextDouble();
                    double y = random.nextDouble();
                    return x * x + y * y <= 1;
                })
                .count();
        return (double) hits / n;
    }

    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions.entrySet().stream()
                .max(Comparator.comparing(entry -> entry.getValue().stream()
                        .mapToInt(String::length)
                        .sum()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum
                ));
    }
}
