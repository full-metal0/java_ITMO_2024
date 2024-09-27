package ru.itmo.mit.streams;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() throws IOException {
        Path file1 = Files.createTempFile("some1", ".txt");
        Path file2 = Files.createTempFile("some2", ".txt");
        Files.write(file1, Arrays.asList("test", "line", "some here"), StandardOpenOption.WRITE);
        Files.write(file2, Arrays.asList("text", "some here x2"), StandardOpenOption.WRITE);

        List<String> paths = Arrays.asList(file1.toString(), file2.toString());
        String sequence = "some";

        List<String> expected = Arrays.asList("some here", "some here x2");
        List<String> actual = SecondPartTasks.findQuotes(paths, sequence);

        assertEquals(expected, actual);

        Files.delete(file1);
        Files.delete(file2);
    }

    @Test
    public void testPiDividedBy4() {
        double result = SecondPartTasks.piDividedBy4(1000000);
        assertEquals(0.785, result, 0.01);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> compositions = Map.of(
                "Author1", Arrays.asList("short text", "short text x2"),
                "Author2", Arrays.asList("long text that really really really long")
        );

        String expected = "Author2";
        String actual = SecondPartTasks.findPrinter(compositions);

        assertEquals(expected, actual);
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> order1 = Map.of("milk", 3, "bread", 5);
        Map<String, Integer> order2 = Map.of("milk", 2, "butter", 4);
        Map<String, Integer> order3 = Map.of("bread", 1, "butter", 1);

        List<Map<String, Integer>> orders = Arrays.asList(order1, order2, order3);

        Map<String, Integer> expected = Map.of("milk", 5, "bread", 6, "butter", 5);
        Map<String, Integer> actual = SecondPartTasks.calculateGlobalOrder(orders);

        assertEquals(expected, actual);
    }
}
