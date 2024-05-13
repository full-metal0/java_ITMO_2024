package ru.itmo.mit.implementor;

public interface Implementor {
    /**
     * Имплементор по данной папке с class файлами java ищет в ней java класс, которые требуется реализовать.
     * Класс записывает реализацию в папку `outputDirectory` (учитывая пакеты)
     * и возвращает полное название нового класса.
     * Реализация должна находится в том же пакете, что и исходный класс/интерфейс.
     * <p>
     * Например, требуется реализовать интерфейс `ru.itmo.AnInterface`.
     * Тогда в папке ожидается файл ru/itmo/AnInterface.class.
     * Implementor генерирует реализацию этого интерфейса, кладет её в
     * <workingDirectory>/ru/itmo/AnInterfaceImpl.java
     * и возвращает полное имя сгенерированного класса ru.itmo.AnInterfaceImpl.
     *
     * @param directoryPath путь до директории, которая содержит данный класс/интерфейс
     * @param className     полное название класса/интерфейса, которое требуется реализовать
     * @throws ImplementorException в тех ситуациях когда
     *                              1) Невозможно создать наследника класса.
     *                              2) Входной класс не найден.
     *                              3) Невозможно записать сгенерированный класс.
     */
    String implementFromDirectory(final String directoryPath, final String className) throws ImplementorException;


    /**
     * Имплементор ищет java класс/интерфейс из стандартной библеотеки, которые требуется реализовать.
     * Класс записывает реализацию в папку `outputDirectory`.
     * Реализация должна находится в default пакете.
     * <p>
     * Например, требуется реализовать интерфейс `java.lang.Comparable`
     * Имплементор генерирует реализацию этого интерфейса, кладет её в `workingDirectory`/ComparableImpl.java и
     * возвращает полное имя сгенерированного класса ComparableImpl.
     *
     * @param className полное название класса/интерфейса, которое требуется реализовать
     * @return полное название нового класса, например ComparableImpl
     * @throws ImplementorException в тех ситуациях когда
     *                              1) Невозможно создать наследника класса.
     *                              2) Входной класс не найден.
     *                              3) Невозможно записать сгенерированный класс.
     */
    String implementFromStandardLibrary(final String className) throws ImplementorException;

}
