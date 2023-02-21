package ru.itmo.mit;

public class StaticTest {
    static int i;
    int j, h;

    static {
        i = 25;
        System.out.println("Hello1");
    }

    {
        j = 8;
        h = 3;
        System.out.println("Hello2");
    }

    public static void main(String[] args) {
        System.out.println("Hello3");
        StaticTest t = new StaticTest();
        // 1/3/2
    }
}