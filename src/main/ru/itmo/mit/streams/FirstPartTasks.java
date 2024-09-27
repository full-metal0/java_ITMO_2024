package ru.itmo.mit.streams;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class FirstPartTasks {

    private FirstPartTasks() {}

    public static List<String> allNames(Stream<Album> albums) {
        return albums.map(Album::getName)
                .collect(Collectors.toList());
    }

    public static List<String> allNamesSorted(Stream<Album> albums) {
        return albums.map(Album::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<String> allTracksSorted(Stream<Album> albums) {
        return albums.flatMap(album -> album.getTracks().stream())
                .map(Track::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<Album> sortedFavorites(Stream<Album> albums) {
        return albums.filter(album -> album.getTracks().stream().anyMatch(track -> track.getRating() > 95))
                .sorted(Comparator.comparing(Album::getName))
                .collect(Collectors.toList());
    }

    public static Map<Artist, List<Album>> groupByArtist(Stream<Album> albums) {
        return albums.collect(Collectors.groupingBy(Album::getArtist));
    }

    public static Map<Artist, List<String>> groupByArtistMapName(Stream<Album> albums) {
        return albums.collect(Collectors.groupingBy(Album::getArtist, Collectors.mapping(Album::getName, Collectors.toList())));
    }

    public static long countAlbumDuplicates(Stream<Album> albums) {
        return albums.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .values().stream()
                .mapToLong(count -> count - 1)
                .sum();
    }

    public static Optional<Album> minMaxRating(Stream<Album> albums) {
        return albums.min(Comparator.comparing(album -> album.getTracks().stream()
                .mapToInt(Track::getRating)
                .max()
                .orElse(0)));
    }

    public static List<Album> sortByAverageRating(Stream<Album> albums) {
        return albums
                .map(album -> new AbstractMap.SimpleEntry<>(album, album.getTracks().stream()
                        .mapToInt(Track::getRating)
                        .average()
                        .orElse(0)))
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(AbstractMap.SimpleEntry::getKey)
                .collect(Collectors.toList());
    }


    public static int moduloProduction(IntStream stream, int modulo) {
        return stream.reduce(1, (a, b) -> (a * b) % modulo);
    }

    public static String joinTo(String... strings) {
        return Arrays.stream(strings)
                .collect(Collectors.joining(", ", "<", ">"));
    }

    public static <R> Stream<R> filterIsInstance(Stream<?> s, Class<R> clazz) {
        return s.filter(clazz::isInstance)
                .map(clazz::cast);
    }
}
